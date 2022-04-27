package com.auth.dto;

import java.util.UUID;

public class ConnectionsRegisterDTO {

    private String id;

    private String username;

    public ConnectionsRegisterDTO() {}

    public ConnectionsRegisterDTO(String id, String username) {
        this.id = id;
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }
}
