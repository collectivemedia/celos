package com.collective.celos;


public class SuccessTrigger implements Trigger {

    private WorkflowID triggerWorkflowId;

    public SuccessTrigger(String workflowName) throws Exception {
        triggerWorkflowId = new WorkflowID(workflowName);
    }

    @Override
    public boolean isDataAvailable(Scheduler scheduler, ScheduledTime now, ScheduledTime scheduledTime) throws Exception {
        SlotID slotId = new SlotID(triggerWorkflowId, scheduledTime);
        if (scheduler.getStateDatabase().getSlotState(slotId) != null) {
            return SlotState.Status.SUCCESS == scheduler.getStateDatabase().getSlotState(slotId).getStatus();
        }
        return false;
    }

    public WorkflowID getTriggerWorkflowId() {
        return triggerWorkflowId;
    }
}
