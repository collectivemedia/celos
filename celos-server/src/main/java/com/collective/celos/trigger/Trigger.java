package com.collective.celos.trigger;

import com.collective.celos.ScheduledTime;
import com.collective.celos.Scheduler;

/**
 * A trigger is called to determine data availability for a workflow.
 */
public abstract class Trigger {

    /**
     * Returns true if data is available for the given scheduled time, false if not.
     */
    public abstract TriggerStatusPOJO makeStatusObject(Scheduler scheduler, ScheduledTime now, ScheduledTime scheduledTime) throws Exception;


    public abstract String humanReadableDescription(boolean ready, ScheduledTime scheduledTime);

    public final boolean isDataAvailable(Scheduler scheduler, ScheduledTime now, ScheduledTime scheduledTime) throws Exception {
        return makeStatusObject(scheduler, now, scheduledTime).isReady();
    }

}
