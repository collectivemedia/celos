package com.collective.celos;

/**
 * The execution status of a slot.
 */
public class SlotState extends ValueObject {
    
    protected final SlotID slotID;
    protected final Status status;
    
    public enum Status {
        WAITING,
        READY,
        RUNNING,
        SUCCESS,
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
