package com.lfj.messenger.shared.test;

import org.junit.jupiter.api.Test;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestLogger {
    @Test
    public void test() throws IOException {
        Logger logger = LoggerFactory.getLogger(TestLogger.class);
        logger.info("Info!");
        logger.warn("Warn!");
        logger.error("Error!");
        logger.debug("Debug!");
        logger.trace("Trace!");
    }
}
