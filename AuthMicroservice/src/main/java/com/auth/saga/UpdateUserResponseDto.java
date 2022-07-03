package com.auth.saga;

public class UpdateUserResponseDto {

    private boolean success;

    private String message;

    private UpdateUserDto oldUser;

    public UpdateUserResponseDto() {
    }

    public UpdateUserResponseDto(boolean success, String message, UpdateUserDto oldUser) {
        this.success = success;
        this.message = message;
        this.oldUser = oldUser;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UpdateUserDto getOldUser() {
        return oldUser;
    }

    public void setOldUser(UpdateUserDto oldUser) {
        this.oldUser = oldUser;
    }
}
