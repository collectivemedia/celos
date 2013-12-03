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
        return wrappedDatabase.getSlotState(slot);
    }

    @Override
    public void putSlotState(SlotState state) throws Exception {
        logger.info("Changing status of slot " + state.getSlotID() + " to "
                + state.getStatus() + " with external ID = "
                + state.getExternalID());
        wrappedDatabase.putSlotState(state);
    }

}
