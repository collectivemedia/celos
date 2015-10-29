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
package com.collective.celos.trigger;


import com.collective.celos.ScheduledTime;
import com.collective.celos.Scheduler;
import com.collective.celos.SlotID;
import com.collective.celos.SlotState;
import com.collective.celos.StateDatabaseConnection;
import com.collective.celos.WorkflowID;

/**
 * Trigger that checks another workflow's slot (at the same scheduled time) for success.
 */
public class SuccessTrigger extends Trigger {

    private WorkflowID triggerWorkflowID;

    public SuccessTrigger(String workflowName) throws Exception {
        this.triggerWorkflowID = new WorkflowID(workflowName);
    }
    
    @Override
    public TriggerStatus getTriggerStatus(StateDatabaseConnection connection, ScheduledTime now, ScheduledTime scheduledTime) throws Exception {
        SlotID slotId = new SlotID(triggerWorkflowID, scheduledTime);
        final SlotState slotState = connection.getSlotState(slotId);
        boolean ready = slotState != null && SlotState.Status.SUCCESS == slotState.getStatus();
        return makeTriggerStatus(ready, humanReadableDescription(ready, slotId));
    }

    private String humanReadableDescription(boolean ready, SlotID slotId) {
        if (ready) {
            return "Workflow slot " + slotId.toString() + " is ready";
        } else {
            return "Workflow slot " + slotId.toString() + " is not ready";
        }
    }

    public WorkflowID getTriggerWorkflowId() {
        return triggerWorkflowID;
    }

}
