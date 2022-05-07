package com.auth.controller;

import com.auth.dto.ResponseDTO;
import com.auth.dto.UpdateUserDTO;
import com.auth.model.User;
import com.auth.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PutMapping
    public ResponseEntity<ResponseDTO> update(@RequestBody UpdateUserDTO updateUserDTO) {
        User updatedUser = userService.update(updateUserDTO.getId(), updateUserDTO.getUsername());
        if (updatedUser == null)
            return new ResponseEntity<>(new ResponseDTO(false, "update failed!"), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(new ResponseDTO(true, "success!"), HttpStatus.OK);
    }
}
