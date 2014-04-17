package com.collective.celos;

/**
 * Uniquely identifies a workflow.
 */
public class WorkflowID extends ValueObject {

    protected final String id;
    
    public WorkflowID(String id) {
        this.id = Util.requireNonNull(id);
        if (id.trim().equals("")) {
            throw new IllegalArgumentException("Workflow ID can't be only whitespace.");
        }
        if (id.contains("/")) {
            throw new IllegalArgumentException("Workflow ID can't contain slash.");
        }
    }

    public String toString() {
        return id;
    }
    
}
