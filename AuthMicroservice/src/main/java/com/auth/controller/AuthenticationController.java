package com.auth.controller;

import com.auth.dto.LoginDTO;
import com.auth.dto.RegisterDTO;
import com.auth.dto.TokenDTO;
import com.auth.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<RegisterDTO> addUser(@RequestBody RegisterDTO registerDTO) {
        //authenticationService.signUpClient(new Client(userDto));
        try {
            authenticationService.signUp(registerDTO);
        } catch (Exception e) {

        }
        return new ResponseEntity<>(registerDTO, HttpStatus.CREATED);
    }

    @PostMapping(value = "/login")
    public ResponseEntity<TokenDTO> login(@RequestBody @Valid LoginDTO loginDTO) {
        TokenDTO tokenDTO = authenticationService.login(loginDTO.getUsername(), loginDTO.getPassword());
        return ResponseEntity.ok(tokenDTO);
    }
    
}
