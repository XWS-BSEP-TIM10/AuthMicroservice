package com.auth.saga;

public class UpdateUserRequestDto {

    private String id;

    private String username;

    private UpdateUserDto oldUser;

    public UpdateUserRequestDto() {
    }

    public UpdateUserRequestDto(String id, String username) {
        this.id = id;
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public UpdateUserDto getOldUser() {
        return oldUser;
    }

    public void setOldUser(UpdateUserDto oldUser) {
        this.oldUser = oldUser;
    }
}
