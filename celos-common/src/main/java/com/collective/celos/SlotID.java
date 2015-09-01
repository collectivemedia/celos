package com.collective.celos;

/**
 * A slot is uniquely identified by its workflow ID and the scheduled time.
 */
public class SlotID extends ValueObject {
    
    protected final WorkflowID workflowID;
    protected final ScheduledTime scheduledTime;

    public SlotID(WorkflowID workflowID, ScheduledTime scheduledTime) {
        assert workflowID != null;
        this.workflowID = workflowID;
        assert scheduledTime != null;
        this.scheduledTime = scheduledTime;
    }
    
    public WorkflowID getWorkflowID() {
        return workflowID;
    }
    
    public ScheduledTime getScheduledTime() {
        return scheduledTime;
    }
    
    public String toString() {
        return workflowID + "@" + scheduledTime;
    }
    
}
