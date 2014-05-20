package com.collective.celos;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class WorkflowConfiguration {

    private final Map<WorkflowID, Workflow> workflows = new HashMap<>();
    private final Map<WorkflowID, String> workflowIdToConfigFilePath = new HashMap<>();

    public WorkflowConfiguration() {
    }

    public Collection<Workflow> getWorkflows() {
        return workflows.values();
    }
    
    public Workflow findWorkflow(WorkflowID id) {
        return workflows.get(Util.requireNonNull(id));
    }

    public String getWorkflowJSFileName(WorkflowID id) {
        return workflowIdToConfigFilePath.get(Util.requireNonNull(id));
    }


    public void addWorkflow(Workflow wf, String filePath) {
        Util.requireNonNull(wf);
        WorkflowID id = wf.getID();
        if (findWorkflow(id) != null) {
            throw new IllegalArgumentException("Workflow with this ID already exists: " + id);
        }
        workflows.put(id, wf);
        workflowIdToConfigFilePath.put(id, filePath);
    }
    
}
