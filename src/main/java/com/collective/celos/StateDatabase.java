package com.collective.celos;

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
