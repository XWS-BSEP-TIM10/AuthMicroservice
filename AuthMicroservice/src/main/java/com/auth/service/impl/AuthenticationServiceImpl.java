package com.auth.service.impl;

import com.auth.dto.NewUserDTO;
import com.auth.dto.RegisterDTO;
import com.auth.dto.TokenDTO;
import com.auth.model.User;
import com.auth.saga.OrchestratorService;
import com.auth.saga.dto.OrchestratorResponseDTO;
import com.auth.security.util.TokenUtils;
import com.auth.service.AuthenticationService;
import com.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.util.UUID;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final TokenUtils tokenUtils;
    private final UserService userService;

    @Autowired
    public AuthenticationServiceImpl(AuthenticationManager authenticationManager, TokenUtils tokenUtils, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.tokenUtils = tokenUtils;
        this.userService = userService;
    }

    @Override
    public TokenDTO login(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                username, password));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = (User) authentication.getPrincipal();
        return new TokenDTO(getToken(user), user.getRole().getName());
    }

    @Transactional
    @Override
    public Mono<OrchestratorResponseDTO> signUp(NewUserDTO newUserDTO) {
        RegisterDTO registerDTO = new RegisterDTO(UUID.randomUUID().toString(), newUserDTO);
        OrchestratorService orchestrator = new OrchestratorService(userService, getProfileWebClient(), getConnectionsWebClient());
        return orchestrator.registerUser(registerDTO);
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
        return tokenUtils.generateToken(user.getUsername(), user.getRole().getName());
    }
}
