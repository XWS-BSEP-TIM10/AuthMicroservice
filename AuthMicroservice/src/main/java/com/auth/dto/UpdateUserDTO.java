package com.auth.dto;

public class UpdateUserDTO {

    private String id;

    private String username;

    public UpdateUserDTO() {
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
