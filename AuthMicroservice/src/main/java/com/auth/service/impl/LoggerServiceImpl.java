package com.auth.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggerServiceImpl {
    private final Logger logger;

    public LoggerServiceImpl(Class<?> parentClass) {
        this.logger = LogManager.getLogger(parentClass);
    }


    //--------------------------------------------------------------------------------------------------

}
