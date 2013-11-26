package com.collective.celos;

import java.util.Collections;
import java.util.Set;

public class WorkflowConfiguration {

    private final Set<Workflow> workflows;
    
    public WorkflowConfiguration(Set<Workflow> workflows) {
        this.workflows = Collections.unmodifiableSet(Util.requireNonNull(workflows));
    }

    public Set<Workflow> getWorkflows() {
        return workflows;
    }
    
}
