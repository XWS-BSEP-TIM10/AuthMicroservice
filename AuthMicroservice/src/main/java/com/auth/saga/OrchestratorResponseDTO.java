package com.auth.saga;

import com.auth.dto.RegisterDTO;

public class OrchestratorResponseDTO {

    private String id;

    private boolean success;

    private String message;

    private String service;

    private RegisterDTO user;

    public OrchestratorResponseDTO() {
    }

    public OrchestratorResponseDTO(String username, Boolean success, String message, RegisterDTO user) {
        this.id = username;
        this.success = success;
        this.message = message;
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public RegisterDTO getUser() {
        return user;
    }

    public void setUser(RegisterDTO user) {
        this.user = user;
    }

    public String getService() {
        return service;
    }

    public boolean isSuccess() {
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

    @Override
    public String toString() {
        return "OrchestratorResponseDTO{" +
                "id='" + id + '\'' +
                ", success=" + success +
                ", message='" + message + '\'' +
                '}';
    }
}
