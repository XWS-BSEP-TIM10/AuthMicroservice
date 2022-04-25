package com.auth.service.impl;

import com.auth.dto.RegisterDTO;
import com.auth.dto.TokenDTO;
import com.auth.exception.UserAlreadyExistsException;
import com.auth.model.User;
import com.auth.saga.OrchestratorService;
import com.auth.saga.dto.OrchestratorResponseDTO;
import com.auth.security.util.TokenUtils;
import com.auth.service.AuthenticationService;
import com.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

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

    @Override
    public Mono<OrchestratorResponseDTO> signUp(RegisterDTO registerDTO) throws UserAlreadyExistsException {
        if (userService.userExists(registerDTO.getUsername())) {
            throw new UserAlreadyExistsException();
        }

        OrchestratorService orchestrator = new OrchestratorService(userService);
        return orchestrator.registerUser(registerDTO);
    }

    private String getToken(User user) {
        return tokenUtils.generateToken(user.getUsername(), user.getRole().getName());
    }
}
