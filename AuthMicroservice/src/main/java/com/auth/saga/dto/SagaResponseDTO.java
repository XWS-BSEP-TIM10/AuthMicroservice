package com.auth.saga.dto;

public class SagaResponseDTO {

    private String id;
    private boolean success;
    private String message;

    public SagaResponseDTO() {
    }

    public SagaResponseDTO(String id, boolean success, String message) {
        this.id = id;
        this.success = success;
        this.message = message;
    }

    public String getId() {
        return id;
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
}
