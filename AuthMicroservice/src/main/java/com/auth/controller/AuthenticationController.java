package com.auth.controller;

import com.auth.dto.*;
import com.auth.exception.UserAlreadyExistsException;
import com.auth.saga.dto.OrchestratorResponseDTO;
import com.auth.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.lang.reflect.Executable;

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
            OrchestratorResponseDTO response = authenticationService.signUp(newUserDTO).block();
            if (!response.getSuccess())
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
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
        }catch(Exception ex){
            return ResponseEntity.badRequest().build();
        }
    }

}
