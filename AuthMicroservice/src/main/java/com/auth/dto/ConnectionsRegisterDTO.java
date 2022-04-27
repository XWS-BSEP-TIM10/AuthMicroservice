package com.auth.dto;

public class ConnectionsRegisterDTO {
    private String username;

    public ConnectionsRegisterDTO() {}

    public ConnectionsRegisterDTO(String username) {
        this.username = username;
    }



    public String getUsername() {
        return username;
    }
}
