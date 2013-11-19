package com.collective.celos;

/**
 * Uniquely identifies a workflow.
 */
public class WorkflowID extends ValueObject {

    private final String id;
    
    public WorkflowID(String id) {
        this.id = Util.requireNonNull(id);
        if (id.trim().equals("")) {
            throw new IllegalArgumentException("Workflow ID can't be only whitespace.");
        }
    }

    public String getID() {
        return id;
    }
    
}
