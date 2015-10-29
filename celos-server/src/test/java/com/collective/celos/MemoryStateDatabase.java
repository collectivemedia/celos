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

import com.google.common.collect.Maps;
import org.eclipse.jetty.util.ConcurrentHashSet;
import org.joda.time.DateTime;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple implementation of StateDatabase that stores everything in a Map.
 */
public class MemoryStateDatabase implements StateDatabase {

    protected final Map<SlotID, SlotState> map = new HashMap<>();
    protected final Map<SlotID, ScheduledTime> rerun = new ConcurrentHashMap<>();
    protected final Set<WorkflowID> pausedWorkflows = new HashSet<>();
    private final MemoryStateDatabaseConnection instance = new MemoryStateDatabaseConnection();

    @Override
    public StateDatabaseConnection openConnection() {
        return getMemoryStateDatabaseConnection();
    }

    public MemoryStateDatabaseConnection getMemoryStateDatabaseConnection() {
        return instance;
    }

    protected class MemoryStateDatabaseConnection implements StateDatabaseConnection {
        @Override
        public Map<SlotID, SlotState> getSlotStates(WorkflowID id, ScheduledTime start, ScheduledTime end) throws Exception {
            Map<SlotID, SlotState> slotStates = Maps.newHashMap();
            for (Map.Entry<SlotID, SlotState> entry : map.entrySet()) {
                DateTime dateTime = entry.getKey().getScheduledTime().getDateTime();
                if (!dateTime.isBefore(start.getDateTime()) && dateTime.isBefore(end.getDateTime())) {
                    slotStates.put(entry.getKey(), entry.getValue());
                }
            }
            return slotStates;
        }

        @Override
        public Map<SlotID, SlotState> getSlotStates(WorkflowID id, Collection<ScheduledTime> times) throws Exception {
            Map<SlotID, SlotState> slotStates = Maps.newHashMap();
            for (ScheduledTime time : times) {
                SlotID slotID = new SlotID(id, time);
                SlotState slotState = getSlotState(slotID);
                if (slotState != null) {
                    slotStates.put(slotID, slotState);
                }
            }
            return slotStates;
        }

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
            rerun.put(slot, now);
        }

        @Override
        public boolean isPaused(WorkflowID workflowID) {
            return pausedWorkflows.contains(workflowID);
        }

        @Override
        public SortedSet<ScheduledTime> getTimesMarkedForRerun(WorkflowID workflowID, ScheduledTime now) throws Exception {
            SortedSet<ScheduledTime> res = new TreeSet<>();
            for (Map.Entry<SlotID, ScheduledTime> entry : rerun.entrySet()) {
                if (entry.getKey().getWorkflowID().equals(workflowID)) {
                    res.add(entry.getKey().getScheduledTime());
                    RerunState rerunState = new RerunState(entry.getValue());
                    if (rerunState.isExpired(now)) {
                        rerun.remove(entry.getKey());
                    }
                }
            }
            return res;
        }

        @Override
        public void setPaused(WorkflowID workflowID, boolean paused) {
            if (paused) {
                pausedWorkflows.add(workflowID);
            } else {
                pausedWorkflows.remove(workflowID);
            }
        }

    };

}
