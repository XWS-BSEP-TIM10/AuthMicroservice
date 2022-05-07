package com.auth.saga.dto;

public class OrchestratorResponseDTO {

    private String id;

    private Boolean success;

    private String message;

    public OrchestratorResponseDTO() {
    }

    public OrchestratorResponseDTO(String username, Boolean success, String message) {
        this.id = username;
        this.success = success;
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
