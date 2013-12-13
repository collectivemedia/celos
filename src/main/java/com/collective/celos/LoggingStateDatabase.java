package com.collective.celos;

/**
 * A LoggingStateDatabase wraps the underlying database implementation and logs
 * state changes required.
 */
public class LoggingStateDatabase implements StateDatabase {

    private final StateDatabase wrappedDatabase;
    private final SlotStateLogger slotStateLogger;

    public LoggingStateDatabase(StateDatabase wrappedDatabase) {
        this(wrappedDatabase, new SlotStateLogger());
    }

    LoggingStateDatabase(StateDatabase wrappedDatabase,
            SlotStateLogger stateLogger) {
        this.wrappedDatabase = Util.requireNonNull(wrappedDatabase);
        slotStateLogger = stateLogger;
    }

    @Override
    public SlotState getSlotState(SlotID slot) throws Exception {
        return wrappedDatabase.getSlotState(slot);
    }

    @Override
    public void putSlotState(SlotState state) throws Exception {
        slotStateLogger.logMessage(
                state.getSlotID(),
                "Changing status of slot " + state.getSlotID() + " to "
                        + state.getStatus() + " with external ID = "
                        + state.getExternalID());
        wrappedDatabase.putSlotState(state);
    }

}
