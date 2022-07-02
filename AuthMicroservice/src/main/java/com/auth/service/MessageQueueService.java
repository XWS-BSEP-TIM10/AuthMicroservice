package com.auth.service;

import com.auth.dto.RegisterDTO;
import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.Nats;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Service
public class MessageQueueService {

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
        dispatcher.subscribe("nats.demo.reply",
                msg -> System.out.println("Received : " + new String(msg.getData())));
    }

    public void publish(RegisterDTO requestDTO) {
        nats.publish("nats.demo.service",  "Hello NATS".getBytes());
    }
}
