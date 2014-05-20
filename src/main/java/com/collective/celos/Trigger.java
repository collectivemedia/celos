package com.collective.celos;

import java.util.Collections;
import java.util.Set;

/**
 * A trigger is called to determine data availability for a workflow.
 */
public abstract class Trigger {
    
    /**
     * Returns true if data is available for the given scheduled time, false if not.
     */
    public abstract boolean isDataAvailable(ScheduledTime now, ScheduledTime scheduledTime) throws Exception;

    public Set<WorkflowID> getDependentWorkflows() {
        return Collections.emptySet();
    }

}
