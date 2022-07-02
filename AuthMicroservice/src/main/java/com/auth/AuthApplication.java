package com.auth;

import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.Nats;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

@SpringBootApplication
public class AuthApplication {

	public static void main(String[] args) {

		// connect to nats server
		Connection nats = null;
		try {
			nats = Nats.connect();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		Dispatcher dispatcher = nats.createDispatcher(msg -> {
		});

		dispatcher.subscribe("nats.demo.reply", msg -> {
			System.out.println("Received : " + new String(msg.getData()));
		});


		SpringApplication.run(AuthApplication.class, args);
	}

}
