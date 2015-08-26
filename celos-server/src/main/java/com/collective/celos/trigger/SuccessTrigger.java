package com.collective.celos.trigger;


import com.collective.celos.*;

import java.util.Collections;

public class SuccessTrigger extends Trigger {

    private WorkflowID triggerWorkflowId;

    public SuccessTrigger(String workflowName) throws Exception {
        triggerWorkflowId = new WorkflowID(workflowName);
    }

    public boolean isDataAvailable(Scheduler scheduler, ScheduledTime now, ScheduledTime scheduledTime) throws Exception {
        SlotID slotId = new SlotID(triggerWorkflowId, scheduledTime);
        final SlotState slotState = scheduler.getStateDatabase().getSlotState(slotId);
        return slotState != null && SlotState.Status.SUCCESS == slotState.getStatus();
    }

    @Override
    public TriggerStatusPOJO makeStatusObject(Scheduler scheduler, ScheduledTime now, ScheduledTime scheduledTime) throws Exception {
        boolean ready = isDataAvailable(scheduler, now, scheduledTime);
        return new TriggerStatusPOJO(ready, this.description(), Collections.<TriggerStatusPOJO>emptyList());
    }

    @Override
    public String description() {
        return "";
    }

    public WorkflowID getTriggerWorkflowId() {
        return triggerWorkflowId;
    }

}
