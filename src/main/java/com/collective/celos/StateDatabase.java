package com.collective.celos;

import java.util.List;

public interface StateDatabase {

    /**
     * Returns the state of the given slot, or null if not found.
     */
    public SlotState getSlotState(SlotID slot) throws Exception;

    /**
     * Updates the state of the given slot.
     */
    public void putSlotState(SlotState state) throws Exception;


    /**
     * Note a slot ID for rerun processing at the (current) wallclock time.
     *  WARN it returns false if slot doesn't exists
     */
    public boolean updateSlotToRerun(SlotID id, ScheduledTime current) throws Exception;


    /**
     * Get all slot IDs of the workflow noted for rerun processing. Also garbage collect any too-old rerun notes.
     *  if rerunTime > GC_TIME then remove node
     */
    public List<SlotID> getRerunSlotIDs(WorkflowID wfId, ScheduledTime current) throws Exception;

    }
