package com.collective.celos;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

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
    
    // JSON support
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String STATUS_PROP = "status";
    private static final String EXTERNAL_ID_PROP = "externalID";
    private static final String RETRY_COUNT_PROP = "retryCount";
    
    public enum Status {
        /** No data availability yet, but keep on trying. */
        WAITING,
        /** No data availability for too long, consider as unsuccessful. */
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
    
    public SlotState transitionToTimeout() {
        assertStatus(Status.WAITING);
        return new SlotState(slotID, Status.TIMEOUT, null, retryCount);
    }

    public SlotState transitionToReady() {
        assertStatus(Status.WAITING);
        return new SlotState(slotID, Status.READY, null, retryCount);
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
        return new SlotState(slotID, Status.WAITING, null, retryCount + 1);
    }

    public SlotState transitionToRerun() {
        boolean successOrFailure = isSuccessful() || isUnsuccessful();
        if (!successOrFailure) {
            throw new IllegalStateException("Slot must be successful, timed out, or failed, but is: " + status);
        }
        return new SlotState(slotID, Status.WAITING, null, 0); // reset retryCount to 0
    }

    private boolean isSuccessful() {
        return status.equals(Status.SUCCESS);
    }

    private boolean isUnsuccessful() {
        return status.equals(Status.FAILURE) || status.equals(Status.TIMEOUT);
    }
    
    private void assertStatus(Status st) {
        if (!status.equals(st)) {
            throw new IllegalStateException("Expected status " + st + " but was " + status + " (slot: " + this + ")");
        }
    }

    public ObjectNode toJSONNode() {
        ObjectNode node = MAPPER.createObjectNode();
        node.put(STATUS_PROP, this.getStatus().toString());
        node.put(EXTERNAL_ID_PROP, this.getExternalID());
        node.put(RETRY_COUNT_PROP, this.getRetryCount());
        return node;
    }

    public static SlotState fromJSONNode(SlotID id, ObjectNode node) {
        SlotState.Status status = SlotState.Status.valueOf(node.get(STATUS_PROP).textValue());
        String externalID = node.get(EXTERNAL_ID_PROP).textValue();
        int retryCount = node.get(RETRY_COUNT_PROP).intValue();
        return new SlotState(id, status, externalID, retryCount);
    }

}
