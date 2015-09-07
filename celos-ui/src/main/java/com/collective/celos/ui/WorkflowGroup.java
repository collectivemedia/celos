package com.collective.celos.ui;

import java.util.Collections;
import java.util.List;

import com.collective.celos.Util;
import com.collective.celos.WorkflowID;

/**
 * A named group of workflows, for rendering the workflows list.
 */
public class WorkflowGroup {
    
    private final String name;
    private final List<WorkflowID> workflows;
    
    public WorkflowGroup(String name, List<WorkflowID> workflows) {
        this.name = Util.requireNonNull(name);
        this.workflows = Collections.unmodifiableList(Util.requireNonNull(workflows));
    }

    public String getName() {
        return name;
    }

    public List<WorkflowID> getWorkflows() {
        return workflows;
    }
    
}
