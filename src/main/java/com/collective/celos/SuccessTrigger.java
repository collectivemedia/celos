package com.collective.celos;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class SuccessTrigger extends AbstractInternalTrigger {

    public static final String COMMAND_PROP = "celos.successTrigger.workflow";

    private WorkflowID triggerWorkflowId;

    public SuccessTrigger(ObjectNode properties) throws Exception {
        triggerWorkflowId = new WorkflowID(Util.getStringProperty(properties, COMMAND_PROP));
    }

    @Override
    public boolean isDataAvailable(Scheduler s, ScheduledTime now, ScheduledTime scheduledTime) throws Exception {
        SlotID slotId = new SlotID(triggerWorkflowId, scheduledTime);
        if (s.getStateDatabase().getSlotState(slotId) != null) {
            return SlotState.Status.SUCCESS == s.getStateDatabase().getSlotState(slotId).getStatus();
        }
        return false;
    }

    public WorkflowID getTriggerWorkflowId() {
        return triggerWorkflowId;
    }
}
