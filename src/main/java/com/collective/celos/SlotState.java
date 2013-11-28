package com.collective.celos;

/**
 * The execution status of a slot.
 */
public class SlotState extends ValueObject {
    
    /** Never null. */
    protected final SlotID slotID;
    /** Never null. */
    protected final Status status;
    /** Only set in RUNNING, SUCCESS, FAILURE states; null otherwise. */
    private String externalID;
    
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
    
    public ScheduledTime getScheduledTime() {
        return slotID.getScheduledTime();
    }

    public String getExternalID() {
        return externalID;
    }
    
    // TODO: test these transitions
    
    public SlotState transitionToReady() {
        assertStatus(Status.WAITING);
        SlotState newState = new SlotState(this.slotID, Status.READY);
        return newState;
    }
    
    public SlotState transitionToRunning(String externalID) {
        assertStatus(Status.READY);
        SlotState newState = new SlotState(this.slotID, Status.RUNNING);
        newState.externalID = Util.requireNonNull(externalID);
        return newState;
    }

    public SlotState transitionToSuccess() {
        assertStatus(Status.RUNNING);
        SlotState newState = new SlotState(this.slotID, Status.SUCCESS);
        return newState;
    }

    public SlotState transitionToFailure() {
        assertStatus(Status.RUNNING);
        SlotState newState = new SlotState(this.slotID, Status.FAILURE);
        return newState;
    }

    private void assertStatus(Status st) {
        if (!status.equals(st)) {
            throw new IllegalStateException("Expected status " + st + " but was " + status + " (slot: " + this + ")");
        }
    }

}
