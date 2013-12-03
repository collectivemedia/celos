package com.collective.celos;

import org.apache.log4j.Logger;

/**
 * A LoggingStateDatabase wraps the underlying database implementation and logs
 * state changes and exceptions as required.
 */
public class LoggingStateDatabase implements StateDatabase {

    private final StateDatabase wrappedDatabase;
    private final SlotStateLogger logger;

    public LoggingStateDatabase(StateDatabase wrappedDatabase) {
        this.wrappedDatabase = Util.requireNonNull(wrappedDatabase);
        logger = new SlotStateLogger(Logger.getLogger(wrappedDatabase
                .getClass()));
    }

    @Override
    public SlotState getSlotState(SlotID slot) throws Exception {
        try {
            return wrappedDatabase.getSlotState(slot);
        } catch (Exception e) {
            logger.logException(null, e);
            throw e;
        }
    }

    @Override
    public void putSlotState(SlotState state) throws Exception {
        logger.info(state, "Changing status of slot "
                + state.getSlotID() + " to " + state.getStatus()
                + " with external ID = " + state.getExternalID());
        try {
            wrappedDatabase.putSlotState(state);
        } catch (Exception e) {
            logger.logException(state, e);
            throw e;
        }
    }

}
