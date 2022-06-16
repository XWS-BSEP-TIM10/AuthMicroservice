package com.auth.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggerServiceImpl {
    private final Logger logger;

    public LoggerServiceImpl(Class<?> parentClass) {
        this.logger = LogManager.getLogger(parentClass);
    }


    public void userSignedUp(String id) {
        logger.info("New user successfully signed up. User ID: {}", id);
    }

    public void userSigningUpFailed(String message) {
        logger.warn("New user signing up failed: {}. ID: {}", message);
    }

    public void loginSuccess(String username) {
        logger.info("Login successful. Username: {}", username);
    }

    public void loginFailed(String username) {
        logger.warn("Login failed. Username: {}", username);
    }

    public void generateAPITokenSuccess(String id) {
        logger.info("Generated API token successfully. User ID: {}", id);
    }

    public void generateAPITokenFailed(String id) {
        logger.warn("Generated API token unsuccessfully. User ID: {}", id);
    }


    public void accountConfirmed(String username) {
        logger.info("Account confirmed successfully. Username: {}", username);
    }


    public void accountConfirmedFailedTokenExpired(String token) {
        logger.warn("Failed to confirm account, token {} expired.", token);
    }

    public void passwordChanged(String id) {
        logger.info("Password successfully changed. User ID: {}", id);
    }

    public void passwordChangingFailed(String message, String id) {
        logger.warn("Password changing failed: {}. User ID: {}", message, id);
    }

    public void accountRecovered(String id) {
        logger.info("Account recovered successfully. User ID: {}", id);
    }

    public void accountRecoverFailed(String id) {
        logger.warn("Account recover failed. User ID: {}", id);
    }

    public void passwordRecovered(String userId) {
        logger.info("Password recovered successfully. User id: {}", userId);
    }

    public void passwordRecoverFailed(String message, String userId) {
        logger.warn("Password recovering failed: {}. User id: {}", message, userId);
    }

    public void passwordlessTokenGenerated(String userId) {
        logger.info("Passwordless token successfully generated. User id: {}", userId);
    }

    public void passwordlessTokenGeneratingFailed(String message, String userId) {
        logger.warn("Passwordless token generation failed: {}. User id: {}", message, userId);
    }

    public void passwordlessLoginSuccess(String userId) {
        logger.info("Passwordless login successful. User id: {}.", userId);
    }

    public void login2FAFailedCodeNotMatching(String username) {
        logger.warn("Two-factor login failed, invalid code. Username: {}", username);
    }

    public void twoFAStatusChanged(boolean enable2FA, String username) {
        logger.info("Two-factor authentication status changed on: {}. Username: {}", enable2FA, username);
    }

    public void twoFAStatusChangeFailed(boolean enable2FA, String username) {
        logger.warn("Two-factor authentication status changing on: {} failed. Username: {}", enable2FA, username);
    }

    public void twoFAStatusCheck(String username) {
        logger.info("Two-factor authentication status check done. Username: {}", username);
    }

    public void two2FACheckFailed(String username) {
        logger.warn("Checking two-factor status failed. Username: {}", username);
    }
}
