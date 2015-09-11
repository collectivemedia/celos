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

/**
 * A single execution of a workflow at a given time.
 */
public class SlotID extends ValueObject {
    
    protected final WorkflowID workflowID;
    protected final ZonedDateTime scheduledTime;

    public SlotID(WorkflowID workflowID, ZonedDateTime scheduledTime) {
        this.workflowID = Util.requireNonNull(workflowID);
        this.scheduledTime = Util.requireNonNull(scheduledTime);
    }
    
    public WorkflowID getWorkflowID() {
        return workflowID;
    }
    
    public ZonedDateTime getScheduledTime() {
        return scheduledTime;
    }
    
    public String toString() {
        return workflowID + "@" + scheduledTime;
    }
    
    public static SlotID fromString(String idStr) {
        Util.requireNonNull(idStr);
        String[] parts = idStr.split("@");
        if (!(parts.length == 2)) {
            throw new IllegalArgumentException("Malformed slot ID: " + idStr);
        }
        return new SlotID(new WorkflowID(parts[0]), ZonedDateTime.parse(parts[1]));
    }
    
}
