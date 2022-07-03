package com.auth.dto;

public class ConnectionsRegisterDTO {

    private String uuid;

    private String username;

    public ConnectionsRegisterDTO() {}

    public ConnectionsRegisterDTO(String id, String username) {
        this.uuid = id;
        this.username = username;
    }

    public String getUuid() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }
}
