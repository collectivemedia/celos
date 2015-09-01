package com.collective.celos;

/**
 * A trigger is called to determine data availability for a workflow.
 */
public interface Trigger {
    
    /**
     * Returns true if data is available for the given scheduled time, false if not.
     */
    public boolean isDataAvailable(Scheduler scheduler, ScheduledTime now, ScheduledTime scheduledTime) throws Exception;

}
