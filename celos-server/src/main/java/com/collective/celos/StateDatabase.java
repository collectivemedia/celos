package com.collective.celos;

import java.util.SortedSet;

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
     * 
     * If this is a rerun, then markSlotForRerun() must be used in addition.
     */
    public void putSlotState(SlotState state) throws Exception;
    
    /**
     * Marks the slot for rerun at the current wallclock time.
     */
    public void markSlotForRerun(SlotID slot, ScheduledTime now) throws Exception;
    
    /**
     * Returns the list of scheduled times of the given workflow that have been marked for rerun.
     */
    public SortedSet<ScheduledTime> getTimesMarkedForRerun(WorkflowID workflowID, ScheduledTime now) throws Exception;
    
}
