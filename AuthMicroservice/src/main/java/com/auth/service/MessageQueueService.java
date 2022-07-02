package com.auth.service;

import com.auth.dto.RegisterDTO;
import com.auth.model.Role;
import com.auth.model.User;
import com.auth.model.VerificationToken;
import com.auth.saga.OrchestratorResponseDTO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.Nats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class MessageQueueService {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private VerificationTokenService verificationTokenService;

    @Autowired
    private EmailService emailService;

    private Connection nats;

    public MessageQueueService() {
        try {
            this.nats = Nats.connect();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @PostConstruct
    public void subscribe() {
        Dispatcher dispatcher = nats.createDispatcher(msg -> {
        });
        dispatcher.subscribe("nats.demo.reply", msg -> {

            Gson gson = new Gson();
            String json = new String(msg.getData(), StandardCharsets.UTF_8);
            OrchestratorResponseDTO responseDTO = gson.fromJson(json, OrchestratorResponseDTO.class);
            if (responseDTO.getSuccess() && responseDTO.getService().equals("Profile")) {
                publish(responseDTO.getUser(), "nats.connections");
            } else if (!responseDTO.isSuccess() && responseDTO.getService().equals("Connections")) {
                publishRevert(responseDTO.getId(), "nats.profile.revert");
                userService.deleteById(responseDTO.getId());
            } else if (!responseDTO.isSuccess() && responseDTO.getService().equals("Profile")){
                userService.deleteById(responseDTO.getId());
            } else {
                VerificationToken verificationToken = saveVerificationToken(responseDTO.getUser());
                emailService.sendEmail(responseDTO.getUser().getEmail(), "Account verification", "https://localhost:4200/confirm/" + verificationToken.getToken() + " Click on this link to activate your account");
            }
        } );
    }

    private VerificationToken saveVerificationToken(RegisterDTO registerDTO) {
        List<Role> roles = new ArrayList<>();
        roles.add(roleService.findByName("ROLE_USER"));
        User user = new User(registerDTO.getUuid(), registerDTO.getUsername(), registerDTO.getPassword(), roles);
        VerificationToken verificationToken = new VerificationToken(user);
        verificationTokenService.saveVerificationToken(verificationToken);
        return verificationToken;
    }

    public void publish(RegisterDTO requestDTO, String serviceChannel) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String json = gson.toJson(requestDTO);
        nats.publish(serviceChannel,  json.getBytes());
    }

    public void publishRevert(String userId, String serviceChannel) {
        nats.publish(serviceChannel,  userId.getBytes());
    }
}
