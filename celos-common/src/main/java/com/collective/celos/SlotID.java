package com.collective.celos;

/**
 * A slot is uniquely identified by its workflow ID and the scheduled time.
 */
public class SlotID extends ValueObject {
    
    protected final WorkflowID workflowID;
    protected final ScheduledTime scheduledTime;

    public SlotID(WorkflowID workflowID, ScheduledTime scheduledTime) {
        this.workflowID = Util.requireNonNull(workflowID);
        this.scheduledTime = Util.requireNonNull(scheduledTime);
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
    
    public static SlotID fromString(String idStr) {
        Util.requireNonNull(idStr);
        String[] parts = idStr.split("@");
        if (!(parts.length == 2)) {
            throw new IllegalArgumentException("Malformed slot ID: " + idStr);
        }
        return new SlotID(new WorkflowID(parts[0]), new ScheduledTime(parts[1]));
    }
    
}
