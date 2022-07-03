package com.auth.saga;

import com.auth.dto.RegisterDTO;
import com.auth.model.Role;
import com.auth.model.User;
import com.auth.service.MessageQueueService;
import com.auth.service.RoleService;
import com.auth.service.UserService;
import java.util.ArrayList;
import java.util.List;

public class CreateUserOrchestrator {

    private final UserService userService;

    private final RoleService roleService;

    private final MessageQueueService messageQueue;


    public CreateUserOrchestrator(UserService userService, RoleService roleService, MessageQueueService messageQueue) {
        this.userService = userService;
        this.roleService = roleService;
        this.messageQueue = messageQueue;
    }

    public void registerUser(RegisterDTO registerDTO) {
        List<Role> roles = new ArrayList<>();
        roles.add(roleService.findByName("ROLE_USER"));
        userService.saveOrRewrite(new User(registerDTO.getUuid(), registerDTO.getUsername(), registerDTO.getPassword(), roles));
        messageQueue.publishCreateUser(registerDTO, "nats.profile");
    }
}
