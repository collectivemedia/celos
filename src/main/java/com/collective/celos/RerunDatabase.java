package com.collective.celos;

import java.util.List;

public interface RerunDatabase {

    // Note a slot ID for rerun processing at the (current) wallclock time.
    public void addSlotID(SlotID id, ScheduledTime current) throws Exception;

    // Get all slot IDs of the workflow noted for rerun processing. Also garbage collect any too-old rerun notes.
    public List<SlotID> getSlotIDs(WorkflowID wfid, ScheduledTime current) throws Exception;

}
