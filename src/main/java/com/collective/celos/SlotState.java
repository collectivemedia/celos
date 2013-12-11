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
    private final String externalID;
    /** Initially zero, increased every time the slot is rerun. */
    private final int retryCount;
    
    public enum Status {
        /** No data availability yet. */
        WAITING,
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
        this(slotID, status, null, 0);
    }

    public SlotState(SlotID slotID, Status status, String externalID, int retryCount) {
        this.slotID = Util.requireNonNull(slotID);
        this.status = Util.requireNonNull(status);
        this.externalID = externalID;
        this.retryCount = retryCount;
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
    
    public int getRetryCount() {
        return retryCount;
    }
    
    public SlotState transitionToReady() {
        assertStatus(Status.WAITING);
        return new SlotState(slotID, Status.READY, null, 0);
    }
    
    public SlotState transitionToRunning(String externalID) {
        assertStatus(Status.READY);
        return new SlotState(slotID, Status.RUNNING, externalID, retryCount);
    }

    public SlotState transitionToSuccess() {
        assertStatus(Status.RUNNING);
        return new SlotState(slotID, Status.SUCCESS, externalID, retryCount);
    }

    public SlotState transitionToFailure() {
        assertStatus(Status.RUNNING);
        return new SlotState(slotID, Status.FAILURE, externalID, retryCount);
    }

    public SlotState transitionToRetry() {
        assertStatus(Status.RUNNING);
        return new SlotState(slotID, Status.READY, null, retryCount + 1);
    }

    private void assertStatus(Status st) {
        if (!status.equals(st)) {
            throw new IllegalStateException("Expected status " + st + " but was " + status + " (slot: " + this + ")");
        }
    }

}
