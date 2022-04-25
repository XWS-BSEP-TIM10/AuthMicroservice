package com.auth.saga.dto;

public class OrchestratorResponseDTO {

    private String username;

    private Boolean success;

    private String message;

    public OrchestratorResponseDTO() {
    }

    public OrchestratorResponseDTO(String username, Boolean success, String message) {
        this.username = username;
        this.success = success;
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
