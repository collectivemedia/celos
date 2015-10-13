/*
 * Copyright 2015 Collective, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.collective.celos;

import java.time.ZonedDateTime;
import java.util.*;

/**
 * Simple implementation of StateDatabase that stores everything in a Map.
 */
public class MemoryStateDatabase implements StateDatabase {

    protected final Map<SlotID, SlotState> map = new HashMap<SlotID, SlotState>();
    protected final Set<SlotID> rerun = new HashSet<SlotID>();
    protected final Set<WorkflowID> pausedWorkflows = new HashSet<>();
    
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
    public void markSlotForRerun(SlotID slot, ZonedDateTime now) throws Exception {
        // Doesn't implement GC for rerun
        rerun.add(slot);
    }

    @Override
    public SortedSet<ZonedDateTime> getTimesMarkedForRerun(WorkflowID workflowID, ZonedDateTime now) throws Exception {
        SortedSet<ZonedDateTime> res = new TreeSet<>();
        for (SlotID slot : rerun) {
            if (slot.getWorkflowID().equals(workflowID)) {
                res.add(slot.getSlotTime());
            }
        }
        return res;
    }

    @Override
    public boolean isPaused(WorkflowID workflowID) {
        return pausedWorkflows.contains(workflowID);
    }

    @Override
    public void setPaused(WorkflowID workflowID, boolean paused) {
        if (paused) {
            pausedWorkflows.add(workflowID);
        } else {
            pausedWorkflows.remove(workflowID);
        }
    }

}
