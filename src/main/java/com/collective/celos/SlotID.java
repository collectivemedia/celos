package com.collective.celos;

/**
 * A slot is uniquely identified by its workflow ID and the nominal time
 */
public class SlotID extends ValueObject {
    
    protected final WorkflowID workflowID;
    protected final NominalTime nominalTime;

    public SlotID(WorkflowID workflowID, NominalTime nominalTime) {
        this.workflowID = Util.requireNonNull(workflowID);
        this.nominalTime = Util.requireNonNull(nominalTime);
    }
    
    public WorkflowID getWorkflowID() {
        return workflowID;
    }
    
    public NominalTime getNominalTime() {
        return nominalTime;
    }
    
}
