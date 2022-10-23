package com.estate.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerCreator {

    private static final Logger logger
            = LoggerFactory.getLogger(LoggerCreator.class);

    public static void main(String[] args) {
        logger.info("Logger Initialization from {}", LoggerCreator.class.getSimpleName());
    }

    public static void addInfo(String info){
        logger.info(info);
    }

    public static void addDebug(String info){
        logger.debug(info);
    }

    public static void addWarn(String info){
        logger.warn(info);
    }
}