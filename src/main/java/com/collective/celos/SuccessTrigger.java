package com.collective.celos;


import com.google.common.collect.Sets;

import java.util.Collections;
import java.util.Set;

public class SuccessTrigger extends AbstractInternalTrigger {

    private WorkflowID triggerWorkflowId;

    public SuccessTrigger(String workflowName) throws Exception {
        triggerWorkflowId = new WorkflowID(workflowName);
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

    @Override
    public Set<WorkflowID> getWorkflowsTriggerDependsOn() {
        return Sets.newHashSet(triggerWorkflowId);
    }

}
