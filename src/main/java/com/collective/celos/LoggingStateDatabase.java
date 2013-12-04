package com.collective.celos;

import org.apache.log4j.Logger;

/**
 * A LoggingStateDatabase wraps the underlying database implementation and logs
 * state changes and exceptions as required.
 */
public class LoggingStateDatabase implements StateDatabase {

    private final StateDatabase wrappedDatabase;
    private final SlotStateLogger slotStateLogger;

    public LoggingStateDatabase(StateDatabase wrappedDatabase) {
        this(wrappedDatabase, new SlotStateLogger(
                Logger.getLogger(wrappedDatabase.getClass())));
    }

    LoggingStateDatabase(StateDatabase wrappedDatabase,
            SlotStateLogger slotStateLogger) {
        this.wrappedDatabase = Util.requireNonNull(wrappedDatabase);
        this.slotStateLogger = slotStateLogger;
    }

    @Override
    public SlotState getSlotState(SlotID slot) throws Exception {
        try {
            return wrappedDatabase.getSlotState(slot);
        } catch (Exception e) {
            slotStateLogger.logException(null, e);
            throw e;
        }
    }

    @Override
    public void putSlotState(SlotState state) throws Exception {
        slotStateLogger.info(
                state,
                "Changing status of slot " + state.getSlotID() + " to "
                        + state.getStatus() + " with external ID = "
                        + state.getExternalID());
        try {
            wrappedDatabase.putSlotState(state);
        } catch (Exception e) {
            slotStateLogger.logException(state, e);
            throw e;
        }
    }

}
