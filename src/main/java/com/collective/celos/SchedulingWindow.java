package com.collective.celos;

import java.util.Collections;
import java.util.Set;

public class SchedulingWindow {

    private final Set<WorkflowID> workflowIDs;
    private final ScheduledTime startTime;
    private final ScheduledTime endTime;
    
    public SchedulingWindow(Set<WorkflowID> workflowIDs, ScheduledTime startTime, ScheduledTime endTime) {
        this.workflowIDs = Collections.unmodifiableSet(Util.requireNonNull(workflowIDs));
        this.startTime = Util.requireNonNull(startTime);
        this.endTime = Util.requireNonNull(endTime);
    }

    public Set<WorkflowID> getWorkflowIDs() {
        return workflowIDs;
    }

    public ScheduledTime getStartTime() {
        return startTime;
    }

    public ScheduledTime getEndTime() {
        return endTime;
    }
    
}
