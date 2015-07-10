package com.collective.celos;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simple implementation of StateDatabase that stores everything in a Map.
 */
public class MemoryRerunDatabase implements RerunDatabase {

    protected final Map<SlotID, SlotState> map = new HashMap<SlotID, SlotState>();

    public int size() {
        return map.size();
    }

    @Override
    public void addSlotID(SlotID id, ScheduledTime current) throws Exception {
        assert false;
    }

    @Override
    public List<SlotID> getSlotIDs(WorkflowID wfid, ScheduledTime current) throws Exception {
        assert false;
        return null;
    }
}
