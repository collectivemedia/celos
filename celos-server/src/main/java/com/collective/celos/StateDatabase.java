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
import java.io.IOException;
import java.util.SortedSet;

/**
 * Stores all state needed by the scheduler.
 */
public interface StateDatabase {

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
    public void markSlotForRerun(SlotID slot, ZonedDateTime now) throws Exception;
    
    /**
     * Returns the list of scheduled times of the given workflow that have been marked for rerun.
     */
    public SortedSet<ZonedDateTime> getTimesMarkedForRerun(WorkflowID workflowID, ZonedDateTime now) throws Exception;

    /**
     * Checks if Workflow has been paused
     */
    public boolean isPaused(WorkflowID workflowID);

    /**
     * Sets paused flag for a Workflow
     */
    public void setPaused(WorkflowID workflowID, boolean paused) throws IOException;
}
