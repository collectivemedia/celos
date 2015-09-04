package com.collective.celos.trigger;


import com.collective.celos.*;

import java.util.Collections;

public class SuccessTrigger extends Trigger {

    private WorkflowID triggerWorkflowID;

    public SuccessTrigger(String workflowName) throws Exception {
        this.triggerWorkflowID = new WorkflowID(workflowName);
    }
    
    @Override
    public TriggerStatus getTriggerStatus(Scheduler scheduler, ScheduledTime now, ScheduledTime scheduledTime) throws Exception {
        StateDatabase stateDatabase = scheduler.getStateDatabase();
        SlotID slotId = new SlotID(triggerWorkflowID, scheduledTime);
        final SlotState slotState = stateDatabase.getSlotState(slotId);
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
