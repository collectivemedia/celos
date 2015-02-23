package com.collective.celos;

import java.util.SortedSet;

/**
 * Makes a workflow use the same schedule as another workflow.
 */
public class DependentSchedule implements Schedule {

    private final WorkflowID otherWorkflowID;

    public DependentSchedule(WorkflowID otherWorkflowID) {
        this.otherWorkflowID = Util.requireNonNull(otherWorkflowID);
    }
    
    @Override
    public SortedSet<ScheduledTime> getScheduledTimes(Scheduler scheduler, ScheduledTime start, ScheduledTime end) {
        Workflow otherWorkflow = scheduler.getWorkflowConfiguration().findWorkflow(otherWorkflowID);
        if (otherWorkflow == null) {
            throw new RuntimeException("Workflow " + otherWorkflowID + " doesn't exist.");
        }
        Schedule otherSchedule = otherWorkflow.getSchedule();
        return otherSchedule.getScheduledTimes(scheduler, start, end);
    }

}
