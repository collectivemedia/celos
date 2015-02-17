package com.collective.celos;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class WorkflowConfiguration {

    private final Map<WorkflowID, Workflow> workflows = new HashMap<>();
    private final Map<WorkflowID, WorkflowInfo> workflowInfos = new HashMap<>();

    public WorkflowConfiguration() {
    }

    public Collection<Workflow> getWorkflows() {
        return workflows.values();
    }
    
    public Workflow findWorkflow(WorkflowID id) {
        return workflows.get(Util.requireNonNull(id));
    }

    public WorkflowInfo getWorkflowInfo(WorkflowID id) {
        return workflowInfos.get(Util.requireNonNull(id));
    }

    public void addWorkflow(Workflow wf, WorkflowInfo workflowInfo) {
        Util.requireNonNull(wf);
        WorkflowID id = wf.getID();
        if (findWorkflow(id) != null) {
            throw new IllegalArgumentException("Workflow with this ID already exists: " + id);
        }
        workflows.put(id, wf);
        workflowInfos.put(id, workflowInfo);
    }
    
}
