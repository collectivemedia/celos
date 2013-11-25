package com.collective.celos;

import java.util.Set;

public class WorkflowConfiguration {

    private final Set<Workflow> workflows;
    
    public WorkflowConfiguration(Set<Workflow> workflows) {
        this.workflows = Util.requireNonNull(workflows);
    }

    public Set<Workflow> getWorkflows() {
        return workflows;
    }
    
}
