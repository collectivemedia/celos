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
package com.collective.celos.state;

import com.collective.celos.ScheduledTime;
import com.collective.celos.SlotID;
import com.collective.celos.SlotState;
import com.collective.celos.WorkflowID;

import java.io.File;
import java.io.IOException;
import java.util.SortedSet;

/**
 * Stores all state needed by the scheduler.
 */
public abstract class StateDatabase {

    /**
     * Returns the state of the given slot, or null if not found.
     */
    public abstract SlotState getSlotState(SlotID slot) throws Exception;

    /**
     * Updates the state of the given slot.
     * 
     * If this is a rerun, then markSlotForRerun() must be used in addition.
     */
    public abstract void putSlotState(SlotState state) throws Exception;
    
    /**
     * Marks the slot for rerun at the current wallclock time.
     */
    protected abstract void markSlotForRerun(SlotID slot, ScheduledTime now) throws Exception;
    
    /**
     * Returns the list of scheduled times of the given workflow that have been marked for rerun.
     */
    public abstract SortedSet<ScheduledTime> getTimesMarkedForRerun(WorkflowID workflowID, ScheduledTime now) throws Exception;


    public void updateSlotForRerun(SlotID slotID, ScheduledTime now) throws Exception {
        SlotState state = getSlotState(slotID);
        if (state != null) {
            SlotState newState = state.transitionToRerun();
            putSlotState(newState);
        }
        markSlotForRerun(slotID, now);
    }

    public static StateDatabase makeFSDatabase(File dir) throws IOException {
        return new FileSystemStateDatabase(dir);
    }

}
