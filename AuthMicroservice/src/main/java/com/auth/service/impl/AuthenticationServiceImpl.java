package com.auth.service.impl;

import com.auth.dto.NewUserDTO;
import com.auth.dto.RegisterDTO;
import com.auth.dto.TokenDTO;
import com.auth.exception.UserAlreadyExistsException;
import com.auth.model.User;
import com.auth.model.VerificationToken;
import com.auth.saga.create.CreateUserOrchestrator;
import com.auth.saga.dto.OrchestratorResponseDTO;
import com.auth.security.util.TokenUtils;
import com.auth.service.*;
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
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.util.Date;
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

    @Autowired
    public AuthenticationServiceImpl(AuthenticationManager authenticationManager, TokenUtils tokenUtils, UserService userService, RoleService roleService, PasswordEncoder passwordEncoder, VerificationTokenService verificationTokenService, EmailService emailService) {
        this.authenticationManager = authenticationManager;
        this.tokenUtils = tokenUtils;
        this.userService = userService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.verificationTokenService = verificationTokenService;
        this.emailService = emailService;
    }


    @Override
    public TokenDTO login(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                username, password));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = (User) authentication.getPrincipal();
        return new TokenDTO(getToken(user));
    }

    @Transactional
    @Override
    public Mono<OrchestratorResponseDTO> signUp(NewUserDTO newUserDTO) throws UserAlreadyExistsException {
        if(userService.userExists(newUserDTO.getUsername()))
            throw new UserAlreadyExistsException();

        RegisterDTO registerDTO = new RegisterDTO(UUID.randomUUID().toString(), newUserDTO);


        CreateUserOrchestrator orchestrator = new CreateUserOrchestrator(userService, roleService, getProfileWebClient(), getConnectionsWebClient(), passwordEncoder);
        Mono<OrchestratorResponseDTO> response = orchestrator.registerUser(registerDTO);

        VerificationToken verificationToken = saveVerificationToken(registerDTO, response);
        emailService.sendEmail(registerDTO.getEmail(), verificationToken.getToken());

        return response;
    }

    private VerificationToken saveVerificationToken(RegisterDTO registerDTO, Mono<OrchestratorResponseDTO> response) {
        if(response.block().getSuccess()){
            User user = new User(registerDTO.getUuid(), registerDTO.getUsername(), registerDTO.getPassword(), roleService.findByName("ROLE_USER"));
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
        return tokenUtils.generateToken(user.getRole().getName(), user.getId());
    }

    @Override
    public String verifyUserAccount(String token) {

        VerificationToken verificationToken = verificationTokenService.findVerificationTokenByToken(token);
        User user = userService.findByUsername(verificationToken.getUser().getUsername());;

        if(getDifferenceInMinutes(verificationToken) < 60) {
            user.setActivated(true);
            userService.save(user);
            return user.getUsername();
        }else {
            verificationTokenService.delete(verificationToken);
            return null;
        }
    }

    private long getDifferenceInMinutes(VerificationToken verificationToken) {
        long differenceInTime = (new Date()).getTime() - verificationToken.getCreatedDateTime().getTime();
        long differenceInMinutes = (differenceInTime / (1000 * 60)) % 60;
        return differenceInMinutes;
    }
}
