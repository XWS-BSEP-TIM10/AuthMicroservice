package com.auth.service;

import com.auth.dto.RegisterDTO;
import com.auth.dto.TokenDTO;
import com.auth.exception.UserAlreadyExistsException;
import com.auth.saga.dto.OrchestratorResponseDTO;
import reactor.core.publisher.Mono;

public interface AuthenticationService {
    TokenDTO login(String username, String password);

    Mono<OrchestratorResponseDTO> signUp(RegisterDTO registerDTO) throws UserAlreadyExistsException;
}
