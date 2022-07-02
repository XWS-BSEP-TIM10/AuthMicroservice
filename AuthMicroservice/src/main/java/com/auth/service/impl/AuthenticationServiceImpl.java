package com.auth.service.impl;

import com.auth.dto.NewUserDTO;
import com.auth.dto.RegisterDTO;
import com.auth.dto.TokenDTO;
import com.auth.exception.*;
import com.auth.model.User;
import com.auth.model.VerificationToken;
import com.auth.saga.CreateUserOrchestrator;
import com.auth.security.util.TokenUtils;
import com.auth.service.*;
import de.taimos.totp.TOTP;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final TokenUtils tokenUtils;
    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenService verificationTokenService;
    private final EmailService emailService;
    private final LoggerServiceImpl loggerService;
    private static final int REGISTRATION_TOKEN_EXPIRES = 60;
    private static final int RECOVERY_TOKEN_EXPIRES = 60;

    @Autowired
    public AuthenticationServiceImpl(AuthenticationManager authenticationManager, TokenUtils tokenUtils, UserService userService, RoleService roleService, PasswordEncoder passwordEncoder, VerificationTokenService verificationTokenService, EmailService emailService) {
        this.authenticationManager = authenticationManager;
        this.tokenUtils = tokenUtils;
        this.userService = userService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.verificationTokenService = verificationTokenService;
        this.emailService = emailService;
        this.loggerService = new LoggerServiceImpl(this.getClass());
    }


    @Override
    public TokenDTO login(String username, String password, String code) {
        User user = userService.findByUsername(username);

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                username, password));

        if (user.isUsing2FA() && (code == null || !code.equals(getTOTPCode(user.getSecret())))) {
            throw new CodeNotMatchingException();
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return new TokenDTO(getToken(user), getRefreshToken(user));
    }

    private String getTOTPCode(String secretKey) {
        Base32 base32 = new Base32();
        byte[] bytes = base32.decode(secretKey);
        String hexKey = Hex.encodeHexString(bytes);
        return TOTP.getOTP(hexKey);
    }

    @Override
    public String generateAPIToken(String userId) {
        User agent = userService.findByUsername("agent");
        return getAPIToken(agent, userId);

    }

    @Override
    public String change2FAStatus(String userId, boolean enableFA) {
        return userService.change2FAStatus(userId, enableFA);
    }

    @Override
    public boolean checkTwoFaStatus(String userId) {
        User user = userService.findById(userId);
        if(user == null) throw new UserNotFoundException();
        return user.isUsing2FA();
    }

    @Transactional
    @Override
    public RegisterDTO signUp(NewUserDTO newUserDTO) throws UserAlreadyExistsException, EmailAlreadyExistsException {

        RegisterDTO registerDTO;

        if (userNotActivated(newUserDTO.getId())) {
            registerDTO = new RegisterDTO(newUserDTO.getId(), newUserDTO);
        } else {
            if (userService.userExists(newUserDTO.getUsername()))
                throw new UserAlreadyExistsException();
            if (userService.findById(newUserDTO.getId()) != null && userService.findById(newUserDTO.getId()).isActivated())
                throw new EmailAlreadyExistsException();

            registerDTO = new RegisterDTO(UUID.randomUUID().toString(), newUserDTO);
        }

        CreateUserOrchestrator orchestrator = new CreateUserOrchestrator(userService, roleService, new MessageQueueService());

        orchestrator.registerUser(registerDTO);
        return registerDTO;
    }

    @Override
    public void changePassword(String userId, String oldPassword, String newPassword, String repeatedNewPassword) throws PasswordsNotMatchingException, RepeatedPasswordNotMatchingException {

        User user = userService.findById(userId);
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new PasswordsNotMatchingException();
        }
        if (!newPassword.equals(repeatedNewPassword)) {
            throw new RepeatedPasswordNotMatchingException();
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userService.save(user);

    }

    private String getToken(User user) {
        return tokenUtils.generateToken(user.getRoles().get(0).getName(), user.getId(), false);
    }

    private String getAPIToken(User agent, String userId) {
        return tokenUtils.generateAPIToken(agent.getRoles().get(0).getName(), agent.getId(), userId);
    }

    private String getRefreshToken(User user) {
        return tokenUtils.generateToken(user.getRoles().get(0).getName(), user.getId(), true);
    }

    @Override
    public String verifyUserAccount(String token) throws TokenExpiredException {

        VerificationToken verificationToken = verificationTokenService.findVerificationTokenByToken(token);
        if (verificationToken == null) {
            throw new TokenExpiredException();
        }
        User user = userService.findByUsername(verificationToken.getUser().getUsername());

        verificationTokenService.delete(verificationToken);

        if (getDifferenceInMinutes(verificationToken) < REGISTRATION_TOKEN_EXPIRES) {
            user.setActivated(true);
            userService.save(user);
            return user.getUsername();
        } else {
            throw new TokenExpiredException();
        }
    }

    @Override
    public boolean recoverAccount(String id, String email) {
        try {
            User user = userService.findById(id);
            VerificationToken verificationToken = new VerificationToken(user);
            verificationTokenService.saveVerificationToken(verificationToken);
            emailService.sendEmail(email, "Account recovery", "https://localhost:4200/recover/" + verificationToken.getToken() + " Click on this link to change your password");
            return true;
        } catch (NullPointerException ex) {

            return false;
        }
    }

    @Override
    public User changePasswordRecovery(String newPassword, String repeatedNewPassword, String token) throws RepeatedPasswordNotMatchingException, TokenExpiredException {

        VerificationToken verificationToken = verificationTokenService.findVerificationTokenByToken(token);

        if (verificationToken == null) {
            throw new TokenExpiredException();
        }

        User user = userService.findByUsername(verificationToken.getUser().getUsername());

        if (!newPassword.equals(repeatedNewPassword)) {
            loggerService.passwordRecoverFailed("Repeated password not matching!", user.getId());
            throw new RepeatedPasswordNotMatchingException();
        }

        verificationTokenService.delete(verificationToken);

        if (getDifferenceInMinutes(verificationToken) >= RECOVERY_TOKEN_EXPIRES) throw new TokenExpiredException();

        user.setPassword(passwordEncoder.encode(newPassword));
        return userService.save(user);
    }

    private long getDifferenceInMinutes(VerificationToken verificationToken) {
        LocalDateTime tokenCreated = LocalDateTime.ofInstant(verificationToken.getCreatedDateTime().toInstant(), ZoneId.systemDefault());
        return ChronoUnit.MINUTES.between(tokenCreated, LocalDateTime.now());
    }

    @Override
    public TokenDTO passwordlessSignIn(String token) throws TokenExpiredException {
        VerificationToken verificationToken = verificationTokenService.findVerificationTokenByToken(token);

        if (verificationToken == null) {
            throw new TokenExpiredException();
        }
        User user = userService.findByUsername(verificationToken.getUser().getUsername());

        verificationTokenService.delete(verificationToken);

        if (getDifferenceInMinutes(verificationToken) >= RECOVERY_TOKEN_EXPIRES) throw new TokenExpiredException();

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getUsername(), null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        loggerService.passwordlessLoginSuccess(user.getId());
        return new TokenDTO(getToken(user), getRefreshToken(user));

    }

    @Override
    public boolean generateTokenPasswordless(String id, String email) {
        User user = userService.findById(id);
        if (user != null) {
            VerificationToken verificationToken = new VerificationToken(user);
            verificationTokenService.saveVerificationToken(verificationToken);
            emailService.sendEmail(email, "Passwordless login", "https://localhost:4200/login/passwordless/" + verificationToken.getToken() + " Click on this link to sign in");
            return true;
        }
        return false;
    }

    @Override
    public TokenDTO refreshToken(String token) {
        String id = tokenUtils.getUsernameFromToken(token.split(" ")[1]);
        User user = userService.findById(id);
        return new TokenDTO(getToken(user), getRefreshToken(user));
    }

    @Override
    public boolean checkToken(String token) {
        VerificationToken verificationToken = verificationTokenService.findVerificationTokenByToken(token);
        return verificationToken != null && getDifferenceInMinutes(verificationToken) < RECOVERY_TOKEN_EXPIRES;
    }

    @Override
    public boolean userNotActivated(String id) {
        VerificationToken verificationToken = verificationTokenService.findVerificationTokenByUser(id);
        if (verificationToken == null) return false;
        verificationTokenService.delete(verificationToken);
        return userService.findById(id) != null && !userService.findById(id).isActivated();
    }
}
