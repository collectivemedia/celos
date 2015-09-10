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

import com.collective.celos.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

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
    protected abstract void putSlotState(SlotState state) throws Exception;
    
    /**
     * Marks the slot for rerun at the current wallclock time.
     */
    protected abstract void markSlotForRerun(SlotID slot) throws Exception;

    /**
     * Unmarks the slot for rerun at the current wallclock time.
     */
    protected abstract void unMarkSlotForRerun(SlotID slot) throws Exception;

    /**
     * Returns the list of scheduled times of the given workflow that have been marked for rerun.
     */
    protected abstract SortedSet<ScheduledTime> getTimesMarkedForRerun(WorkflowID workflowID) throws Exception;

    public void updateSlotToSuccess(SlotState slot) throws Exception {
        SlotState newState = slot.transitionToSuccess();
        putSlotState(newState);
        unMarkSlotForRerun(newState.getSlotID());
    }

    public void updateSlotToFailure(SlotState slot) throws Exception {
        SlotState newState = slot.transitionToFailure();
        putSlotState(newState);
        unMarkSlotForRerun(newState.getSlotID());
    }

    public void updateSlotForRerun(SlotID slotID) throws Exception {
        SlotState state = getSlotState(slotID);
        if (state != null) {
            SlotState newState = state.transitionToRerun();
            putSlotState(newState);
        }
        markSlotForRerun(slotID);
    }

    public void updateSlotToRunning(SlotState slot, String extId) throws Exception {
        SlotState newState = slot.transitionToRunning(extId);
        putSlotState(newState);
    }

    public void updateSlotToReady(SlotState slot) throws Exception {
        SlotState newState = slot.transitionToReady();
        putSlotState(newState);
    }

    public void updateSlotToWaitTimeout(SlotState slot) throws Exception {
        SlotState newState = slot.transitionToWaitTimeout();
        putSlotState(newState);
    }

    public void updateSlotToRetry(SlotState slot) throws Exception {
        SlotState newState = slot.transitionToRetry();
        putSlotState(newState);
    }

    public static StateDatabase makeFSDatabase(File dir) throws IOException {
        return new FileSystemStateDatabase(dir);
    }


    public List<SlotState> fetchSlotStates(Workflow wf, SortedSet<ScheduledTime> scheduledTimes) throws Exception {
        scheduledTimes.addAll(getTimesMarkedForRerun(wf.getID()));
        List<SlotState> slotStates = new ArrayList<SlotState>(scheduledTimes.size());
        for (ScheduledTime t : scheduledTimes) {
            SlotID slotID = new SlotID(wf.getID(), t);
            SlotState slotState = getSlotState(slotID);
            if (slotState != null) {
                slotStates.add(slotState);
            } else {
                // Database doesn't have any info on the slot yet -
                // synthesize a fresh waiting slot and put it in the list
                // (not in the database).
                slotStates.add(new SlotState(slotID, SlotState.Status.WAITING));
            }
        }
        return Collections.unmodifiableList(slotStates);
    }


}
