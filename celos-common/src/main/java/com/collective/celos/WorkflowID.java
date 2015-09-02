package com.collective.celos;

/**
 * Uniquely identifies a workflow.
 */
public class WorkflowID extends ValueObject implements Comparable<WorkflowID> {

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

    @Override
    public int compareTo(WorkflowID o) {
        return this.id.compareTo(o.id);
    }

    public String getId() {
        return id;
    }
}
