package com.collective.celos;

import java.util.*;

/**
 * Simple implementation of StateDatabase that stores everything in a Map.
 */
public class MemoryRerunDatabase implements RerunDatabase {

    protected final Map<SlotID, RerunState> idsMap = Collections.emptyMap();

    public int size() {
        return idsMap.size();
    }

    @Override
    public void addSlotID(SlotID id, ScheduledTime current) throws Exception {
        idsMap.put(id, RerunState.fromTime(current));
    }

    @Override
    public List<SlotID> getSlotIDs(WorkflowID wfid, ScheduledTime current) throws Exception {
        final Set<SlotID> slotIds = idsMap.keySet();
        return new ArrayList<>(slotIds);
    }
}
