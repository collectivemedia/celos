package com.collective.celos;

import org.apache.log4j.Logger;

/**
 * A LoggingStateDatabase wraps the underlying database implementation and logs
 * state changes and exceptions as required.
 */
public class LoggingStateDatabase implements StateDatabase {

    private final StateDatabase wrappedDatabase;
    private final Logger logger;

    public LoggingStateDatabase(StateDatabase wrappedDatabase) {
        this.wrappedDatabase = Util.requireNonNull(wrappedDatabase);
        logger = Logger.getLogger(wrappedDatabase.getClass());
    }

    @Override
    public SlotState getSlotState(SlotID slot) throws Exception {
        try {
            return wrappedDatabase.getSlotState(slot);
        } catch (Exception e) {
            logException(null, e);
            throw e;
        }
    }

    @Override
    public void putSlotState(SlotState state) throws Exception {
        logger.info(decorate(state) + "Changing status of slot "
                + state.getSlotID() + " to " + state.getStatus()
                + " with external ID = " + state.getExternalID());
        try {
            wrappedDatabase.putSlotState(state);
        } catch (Exception e) {
            logException(state, e);
            throw e;
        }
    }

    /*
     * I will extract these methods so that they can be used elsewhere.
     */
    private void logException(SlotState state, Exception e) {
        logger.error(decorate(state) + e);
        for (StackTraceElement ste : e.getStackTrace()) {
            logger.error(decorate(state) + "\t" + ste);
        }
    }
    
    private String decorate(SlotState state) {
        String text = state == null ? "none" : state.getSlotID().toString();
        return "[" + text + "] ";
    }

}
