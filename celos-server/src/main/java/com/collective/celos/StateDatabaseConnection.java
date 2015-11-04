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

import java.util.Collection;
import java.util.Map;
import java.util.SortedSet;

import com.google.common.collect.Maps;

/**
 * Stores all state needed by the scheduler.
 */
public interface StateDatabaseConnection extends AutoCloseable {

    /**
     * Returns the state of the slots, specified by start time (inclusive) and end time (exclusive).
     */
    public Map<SlotID, SlotState> getSlotStates(WorkflowID id, ScheduledTime start, ScheduledTime end) throws Exception;

    /**
     * Returns the state of the given slots. If a slot is not found, it will not be contained in the returned Map.
     */
    public default Map<SlotID, SlotState> getSlotStates(WorkflowID id, Collection<ScheduledTime> times) throws Exception {
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

    /**
     * Checks if workflow has been paused.
     */
    public boolean isPaused(WorkflowID workflowID) throws Exception;

    /**
     * Sets paused flag for a workflow.
     */
    public void setPaused(WorkflowID workflowID, boolean paused) throws Exception;

}
