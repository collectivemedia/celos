package com.collective.celos;

/**
 * Stores all state needed by the scheduler.
 */
public interface StateDatabase {

    /**
     * Returns the state of the given slot, or null if not found.
     */
    public SlotState getSlotState(SlotID slot) throws Exception;

    /**
     * Updates the state of the given slot.
     */
    public void putSlotState(SlotState state) throws Exception;
    
}
