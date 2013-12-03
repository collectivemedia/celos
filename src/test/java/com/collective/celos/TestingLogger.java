package com.collective.celos;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.rules.ExternalResource;

/*
 * The TestingLogger Rule adds to log4j an Appender that collects all logged messages
 * and can be asked to return or clear these logged messages.
 */
public class TestingLogger extends ExternalResource {
    
    /*
     * Only flag log4j initialization issues once (per instance).
     * 
     * Maybe this should be static.  I don't know.
     */
    private boolean first = true;
    
    /*
     * This is the object that hangs on to the log messages.
     */
    private TestingAppender testingAppender;
    
    /*
     * The original log level.  Reinstate it when we're done.
     */
    private Level originalLevel;
    
    public String[] getMessages() {
        return testingAppender.getMessages();
    }

    public void clear() {
        testingAppender.clear();
    }

    @Override
    protected void before() throws Throwable {
        testingAppender = new TestingAppender();
        setupLog4j(testingAppender);
    }

    @Override
    protected void after() {
        resetLog4j(testingAppender);
    }

    private void setupLog4j(TestingAppender appender) {
        // Get the root logger for the
        Logger root = getRootLogger();

        // Set to appropriate log level and add custom appender
        originalLevel = root.getLevel();
        root.setLevel(Level.ERROR); // Not sure we really need to do this
        root.addAppender(appender);
    }

    private void resetLog4j(TestingAppender appender) {
        Logger root = getRootLogger();
        root.setLevel(originalLevel);
        root.removeAppender(appender);
    }

    private Logger getRootLogger() {
        Logger root = Logger.getRootLogger();
        if (first) {
            first = false;
            // If there are no appenders, means Log4j is not initialized properly
            if (!root.getAllAppenders().hasMoreElements()) {
                System.out.println("*** Log4j is not initialized ***");
            }
        }
        return root;
    }

}
