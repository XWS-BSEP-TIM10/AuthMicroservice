package com.auth.service;

import com.auth.dto.RegisterDTO;
import com.auth.saga.dto.OrchestratorResponseDTO;
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

@Service
public class MessageQueueService {

    @Autowired
    private UserService userService;

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
            }else if(!responseDTO.isSuccess() && responseDTO.getService().equals("Profile")){
                userService.deleteById(responseDTO.getId());
            }else{
                System.out.println("JEEEEEJ");
            }
        } );
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
