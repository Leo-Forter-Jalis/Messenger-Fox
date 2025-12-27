package com.lfj.messager.shared.test;

import org.junit.jupiter.api.Test;
import com.lfj.messager.logger.Logger;
import org.apache.logging.log4j.Level;

public class TestLogger {
    @Test
    public void test(){
        Logger logger = new Logger(getClass());
        logger.log(Level.INFO, "Test log info");
        logger.log(Level.DEBUG, "Test log debug");
        logger.log(Level.WARN, "Test log warn");
        logger.log(Level.ERROR, "Test log error");
        logger.log(Level.FATAL, "Test log fatal");
        logger.log(Level.ALL, "Test log all");
        logger.log(Level.TRACE, "Test log trace");
    }
}
