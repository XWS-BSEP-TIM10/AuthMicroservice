package com.auth.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggerServiceImpl {
    private final Logger logger;

    public LoggerServiceImpl(Class<?> parentClass) {
        this.logger = LogManager.getLogger(parentClass);
    }


    //--------------------------------------------------------------------------------------------------

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
        logger.warn("Passwordless token generation failed: {}. User id: {}", message, userId) ;
    }

    public void passwordlessLoginSuccess(String userId) {
        logger.info("Passwordless login successful. User id: {}.", userId);
    }
}
