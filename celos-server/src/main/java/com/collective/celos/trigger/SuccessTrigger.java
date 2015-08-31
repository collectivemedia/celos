package com.collective.celos.trigger;


import com.collective.celos.*;

import java.util.Collections;

public class SuccessTrigger extends Trigger {

    private WorkflowID triggerWorkflowID;

    public SuccessTrigger(String workflowName) throws Exception {
        this.triggerWorkflowID = new WorkflowID(workflowName);
    }

    private boolean checkTrigger(Scheduler scheduler, ScheduledTime scheduledTime) throws Exception {
        StateDatabase stateDatabase = scheduler.getStateDatabase();
        SlotID slotId = new SlotID(triggerWorkflowID, scheduledTime);
        final SlotState slotState = stateDatabase.getSlotState(slotId);
        return slotState != null && SlotState.Status.SUCCESS == slotState.getStatus();
    }

    @Override
    public TriggerStatus getTriggerStatus(Scheduler scheduler, ScheduledTime now, ScheduledTime scheduledTime) throws Exception {
        boolean ready = checkTrigger(scheduler, scheduledTime);
        String description = this.humanReadableDescription(ready, scheduledTime);
        return new TriggerStatus(ready, description, Collections.<TriggerStatus>emptyList());
    }

    private String humanReadableDescription(boolean ready, ScheduledTime scheduledTime) {
        SlotID slotId = new SlotID(triggerWorkflowID, scheduledTime);
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
