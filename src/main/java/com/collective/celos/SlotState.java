package com.collective.celos;

/**
 * The execution status of a slot.
 */
public class SlotState extends ValueObject {
    
    protected final SlotID slotID;
    protected final Status status;
    
    public enum Status {
        /** No data availability yet. */
        WAITING,
        /** No data availability for too long, workflow will not run. */
        TIMEOUT,
        /** Data is available and the workflow will be run shortly. 
            Workflow will also enter this state when it is retried. */
        READY,
        /** The workflow is currently running. */
        RUNNING,
        /** The workflow has succeeded. */
        SUCCESS,
        /** The workflow has failed and will not be retried. */
        FAILURE
    };
    
    public SlotState(SlotID slotID, Status status) {
        this.slotID = Util.requireNonNull(slotID);
        this.status = Util.requireNonNull(status);
    }
    
    public SlotID getSlotID() {
        return slotID;
    }
    
    public Status getStatus() {
        return status;
    }
    
}
