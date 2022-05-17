package com.auth.controller;

import com.auth.dto.LoginDTO;
import com.auth.dto.NewUserDTO;
import com.auth.dto.RoleDto;
import com.auth.dto.TokenDTO;
import com.auth.dto.UserRoleDto;
import com.auth.exception.UserAlreadyExistsException;
import com.auth.model.User;
import com.auth.saga.dto.OrchestratorResponseDTO;
import com.auth.service.AuthenticationService;
import com.auth.service.impl.CustomUserDetailsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
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

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService, CustomUserDetailsService customUserDetailsService) {
        this.authenticationService = authenticationService;
        this.customUserDetailsService = customUserDetailsService;
    }

    @PostMapping("/signup")
    public ResponseEntity<NewUserDTO> addUser(@RequestBody NewUserDTO newUserDTO) {
        try {
            OrchestratorResponseDTO response = authenticationService.signUp(newUserDTO).block();
            if (!response.getSuccess())
                return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
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
    
    @PostMapping
    public ResponseEntity<UserRoleDto> getAutethemtio(@RequestBody @Valid String username) {
        try {
            User userTemp = (User) customUserDetailsService.loadUserByUsername(username);
        	User user = new User();
        	RoleDto roleDto  = new RoleDto(userTemp.getRole().getId(), userTemp.getRole().getName());
        	UserRoleDto userRoleDto = new UserRoleDto(userTemp.getId(), userTemp.getUsername(), userTemp.getPassword(), roleDto);
        	user.setRole(userTemp.getRole());
            return ResponseEntity.ok(userRoleDto);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().build();
        }
    }

}
