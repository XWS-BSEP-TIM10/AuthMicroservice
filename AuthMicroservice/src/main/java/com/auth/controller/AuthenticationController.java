package com.auth.controller;

import com.auth.dto.LoginDTO;
import com.auth.dto.NewUserDTO;
import com.auth.dto.TokenDTO;
import com.auth.exception.EmailAlreadyExistsException;
import com.auth.exception.UserAlreadyExistsException;
import com.auth.model.User;
import com.auth.model.VerificationToken;
import com.auth.saga.dto.OrchestratorResponseDTO;
import com.auth.service.AuthenticationService;
import com.auth.service.EmailService;
import com.auth.service.impl.CustomUserDetailsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<NewUserDTO> addUser(@RequestBody NewUserDTO newUserDTO) {
        try {
            OrchestratorResponseDTO response = authenticationService.signUp(newUserDTO);
            if (!response.getSuccess()) {
                return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            return new ResponseEntity<>(newUserDTO, HttpStatus.CREATED);
        } catch (UserAlreadyExistsException | EmailAlreadyExistsException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
