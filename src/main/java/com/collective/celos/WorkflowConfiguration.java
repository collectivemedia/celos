package com.collective.celos;

import java.util.*;

public class WorkflowConfiguration {

    private final Map<WorkflowID, Workflow> workflows = new HashMap<>();
    private final Map<WorkflowID, String> workflowIdToConfigFilePath = new HashMap<>();
    private final Map<WorkflowID, Set<WorkflowID>> workflowDependencies = new HashMap<>();

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

    public Set<WorkflowID> getDependentWorkflows(WorkflowID workflowID) {
        return workflowDependencies.get(workflowID);
    }

    public void addWorkflow(Workflow wf, String filePath) {
        Util.requireNonNull(wf);
        WorkflowID id = wf.getID();
        if (findWorkflow(id) != null) {
            throw new IllegalArgumentException("Workflow with this ID already exists: " + id);
        }
        workflows.put(id, wf);
        workflowIdToConfigFilePath.put(id, filePath);
        updateWorkflowDependencies(wf);
    }

    private void updateWorkflowDependencies(Workflow wf) {
        for(WorkflowID depId : wf.getTrigger().getWorkflowsTriggerDependsOn()) {
            Set<WorkflowID> deps = workflowDependencies.get(depId);
            if (deps == null) {
                deps = new HashSet<>();
                workflowDependencies.put(depId, deps);
            }
            deps.add(wf.getID());
        }
    }



}
