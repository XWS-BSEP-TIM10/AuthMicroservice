package com.auth.service;

public interface EmailService {
    void sendEmail(String userEmail, String verificationToken);
}
