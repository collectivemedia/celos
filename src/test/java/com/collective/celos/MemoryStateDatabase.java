package com.collective.celos;

import java.util.*;

/**
 * Simple implementation of StateDatabase that stores everything in a Map.
 */
public class MemoryStateDatabase implements StateDatabase {

    protected final Map<SlotID, SlotState> map = new HashMap<>();
    protected final Map<SlotID, RerunState> idsMap = new HashMap<>();

    @Override
    public SlotState getSlotState(SlotID id) throws Exception {
        return map.get(id);
    }

    @Override
    public void putSlotState(SlotState state) throws Exception {
        map.put(state.getSlotID(), state);
    }
    
    public int size() {
        return map.size();
    }

    @Override
    public boolean updateSlotToRerun(SlotID id, ScheduledTime current) throws Exception {
        if (!map.containsKey(id)) {
            throw new IllegalStateException();
        }
        idsMap.put(id, RerunState.fromTime(id.workflowID, id.getScheduledTime(), current));
        final SlotState.Status newStatus = getSlotState(id).transitionToRerun().getStatus();
        map.put(id, new SlotState(id, newStatus));
        return true;
    }

    @Override
    public List<SlotID> getRerunSlotIDs(WorkflowID wfid, ScheduledTime current) throws Exception {
        final Set<SlotID> slotIds = idsMap.keySet();
        return new ArrayList<>(slotIds);
    }

}
