package com.collective.celos;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * The set of all workflows loaded into the scheduler.
 */
public class WorkflowConfiguration {

    private final Map<WorkflowID, Workflow> workflows = new HashMap<>();

    public Collection<Workflow> getWorkflows() {
        return workflows.values();
    }
    
    public Workflow findWorkflow(WorkflowID id) {
        return workflows.get(Util.requireNonNull(id));
    }

    public void addWorkflow(Workflow wf) {
        Util.requireNonNull(wf);
        WorkflowID id = wf.getID();
        if (findWorkflow(id) != null) {
            throw new IllegalArgumentException("Workflow with this ID already exists: " + id);
        }
        workflows.put(id, wf);
    }
    
}
