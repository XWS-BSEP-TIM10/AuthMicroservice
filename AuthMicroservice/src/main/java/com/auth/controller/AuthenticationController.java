package com.auth.controller;

import com.auth.dto.LoginDTO;
import com.auth.dto.NewUserDTO;
import com.auth.dto.TokenDTO;
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
    
    private final CustomUserDetailsService customUserDetailsService;

    private final EmailService emailService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService, CustomUserDetailsService customUserDetailsService, EmailService emailService) {
        this.authenticationService = authenticationService;
        this.customUserDetailsService = customUserDetailsService;
        this.emailService = emailService;
    }

    @PostMapping("/signup")
    public ResponseEntity<NewUserDTO> addUser(@RequestBody NewUserDTO newUserDTO) {
        try {
            OrchestratorResponseDTO response = authenticationService.signUp(newUserDTO);
            if (!response.getSuccess()) {
                return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            return new ResponseEntity<>(newUserDTO, HttpStatus.CREATED);
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping(value = "/login")
    public ResponseEntity<TokenDTO> login(@RequestBody @Valid LoginDTO loginDTO) {

        try {
            TokenDTO tokenDTO = authenticationService.login(loginDTO.getUsername(), loginDTO.getPassword());
            return ResponseEntity.ok(tokenDTO);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().build();
        }
    }
    
 

}
