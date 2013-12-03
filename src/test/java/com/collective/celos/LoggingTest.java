package com.collective.celos;

import static org.junit.Assert.assertEquals;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Rule;
import org.junit.Test;

public class LoggingTest {

    @Rule
    public TestingLogger testingLogger = new TestingLogger();

    @Test
    public void testTrace() {
        testSetLevel(Level.TRACE, "trace\ndebug\ninfo\nwarn\nerror");
    }
    
    @Test
    public void testDebug() {
        testSetLevel(Level.DEBUG, "debug\ninfo\nwarn\nerror");
    }
    
    @Test
    public void testInfo() {
        testSetLevel(Level.INFO, "info\nwarn\nerror");
    }
    
    @Test
    public void testWarn() {
        testSetLevel(Level.WARN, "warn\nerror");
    }
    
    @Test
    public void testError() {
        testSetLevel(Level.ERROR, "error");
    }
    
    private void testSetLevel(Level level, String expected) {
        Logger logger = Logger.getLogger(getClass());
        logger.setLevel(level);
        logger.trace("trace");
        logger.debug("debug");
        logger.info("info");
        logger.warn("warn");
        logger.error("error");
        assertEquals(expected,
                StringUtils.join(testingLogger.getMessages(), "\n"));
    }
    
    @Test
    public void testClear() {
        Logger logger = Logger.getLogger(getClass());
        logger.error("error1");
        assertEquals("error1",
                StringUtils.join(testingLogger.getMessages(), "\n"));
        testingLogger.clear();
        logger.error("error2");
        assertEquals("error2",
                StringUtils.join(testingLogger.getMessages(), "\n"));
    }
    
    @Test
    public void testMultipleMessages() {
        Logger logger = Logger.getLogger(getClass());
        logger.error("error1");
        logger.error("error2");
        logger.error("error3");
        assertEquals("error1\nerror2\nerror3",
                StringUtils.join(testingLogger.getMessages(), "\n"));
    }

}
