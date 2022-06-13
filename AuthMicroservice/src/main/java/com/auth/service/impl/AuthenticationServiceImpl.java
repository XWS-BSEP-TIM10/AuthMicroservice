package com.auth.service.impl;

import com.auth.dto.NewUserDTO;
import com.auth.dto.RegisterDTO;
import com.auth.dto.TokenDTO;
import com.auth.exception.EmailAlreadyExistsException;
import com.auth.exception.PasswordsNotMatchingException;
import com.auth.exception.RepeatedPasswordNotMatchingException;
import com.auth.exception.TokenExpiredException;
import com.auth.exception.UserAlreadyExistsException;
import com.auth.model.Role;
import com.auth.model.User;
import com.auth.model.VerificationToken;
import com.auth.saga.create.CreateUserOrchestrator;
import com.auth.saga.dto.OrchestratorResponseDTO;
import com.auth.security.util.TokenUtils;
import com.auth.service.AuthenticationService;
import com.auth.service.EmailService;
import com.auth.service.RoleService;
import com.auth.service.UserService;
import com.auth.service.VerificationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
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
    private final int REGISTRATION_TOKEN_EXPIRES = 60;
    private final int RECOVERY_TOKEN_EXPIRES = 60;

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
    public TokenDTO login(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                username, password));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = (User) authentication.getPrincipal();
        return new TokenDTO(getToken(user), getRefreshToken(user));
    }

    @Override
    public String generateAPIToken(String userId) {
        User agent = userService.findByUsername("agent");
        return getAPIToken(agent, userId);

    }

    @Transactional
    @Override
    public OrchestratorResponseDTO signUp(NewUserDTO newUserDTO) throws UserAlreadyExistsException, EmailAlreadyExistsException {

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

        CreateUserOrchestrator orchestrator = new CreateUserOrchestrator(userService, roleService, getProfileWebClient(), getConnectionsWebClient(), passwordEncoder);
        OrchestratorResponseDTO response = orchestrator.registerUser(registerDTO).block();

        VerificationToken verificationToken = saveVerificationToken(registerDTO, response);
        emailService.sendEmail(registerDTO.getEmail(), "Account verification", "https://localhost:4200/confirm/" + verificationToken.getToken() + " Click on this link to activate your account");

        return response;
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

    private VerificationToken saveVerificationToken(RegisterDTO registerDTO, OrchestratorResponseDTO response) {
        if (response.getSuccess()) {
            List<Role> roles = new ArrayList<Role>();
            roles.add(roleService.findByName("ROLE_USER"));
            User user = new User(registerDTO.getUuid(), registerDTO.getUsername(), registerDTO.getPassword(), roles);
            VerificationToken verificationToken = new VerificationToken(user);
            verificationTokenService.saveVerificationToken(verificationToken);
            return verificationToken;
        }
        return null;
    }

    private WebClient getProfileWebClient() {
        String profileHost = System.getenv("PROFILE_HOST") == null ? "localhost" : System.getenv("PROFILE_HOST");
        String profilePort = System.getenv("PROFILE_PORT") == null ? "8081" : System.getenv("PROFILE_PORT");
        String baseUrl = String.format("http://%s:%s/", profileHost, profilePort);
        return WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create()))
                .build();
    }

    private WebClient getConnectionsWebClient() {
        String connectionsHost = System.getenv("CONNECTIONS_HOST") == null ? "localhost" : System.getenv("CONNECTIONS_HOST");
        String connectionsPort = System.getenv("CONNECTIONS_PORT") == null ? "8082" : System.getenv("CONNECTIONS_PORT");
        String baseUrl = String.format("http://%s:%s/", connectionsHost, connectionsPort);
        return WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create()))
                .build();
    }

    private String getToken(User user) {
        return tokenUtils.generateToken(user.getRoles().get(0).getName(), user.getId(), false);
    }

    private String getAPIToken(User agent, String userId) {
        return tokenUtils.generateAPIToken(agent.getRoles().get(0).getName(), agent.getId(), userId, false);
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
    public Boolean checkToken(String token) {
        VerificationToken verificationToken = verificationTokenService.findVerificationTokenByToken(token);
        return verificationToken != null && getDifferenceInMinutes(verificationToken) < RECOVERY_TOKEN_EXPIRES;
    }

    @Override
    public Boolean userNotActivated(String id) {
        VerificationToken verificationToken = verificationTokenService.findVerificationTokenByUser(id);
        if (verificationToken == null) return false;
        verificationTokenService.delete(verificationToken);
        return userService.findById(id) != null && !userService.findById(id).isActivated();
    }
}
