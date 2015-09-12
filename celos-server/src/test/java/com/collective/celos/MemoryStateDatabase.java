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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Simple implementation of StateDatabase that stores everything in a Map.
 */
public class MemoryStateDatabase implements StateDatabase {

    protected final Map<SlotID, SlotState> map = new HashMap<SlotID, SlotState>();
    protected final Set<SlotID> rerun = new HashSet<SlotID>();
    
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
    public void markSlotForRerun(SlotID slot, ScheduledTime now) throws Exception {
        // Doesn't implement GC for rerun
        rerun.add(slot);
    }

    @Override
    public SortedSet<ScheduledTime> getTimesMarkedForRerun(WorkflowID workflowID, ScheduledTime now) throws Exception {
        SortedSet<ScheduledTime> res = new TreeSet<>();
        for (SlotID slot : rerun) {
            if (slot.getWorkflowID().equals(workflowID)) {
                res.add(slot.getScheduledTime());
            }
        }
        return res;
    }

}
