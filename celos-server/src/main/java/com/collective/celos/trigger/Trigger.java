package com.collective.celos.trigger;

import java.util.Collections;
import java.util.List;

import com.collective.celos.ScheduledTime;
import com.collective.celos.Scheduler;

/**
 * A trigger is called to determine data availability for a workflow.
 */
public abstract class Trigger {

    /**
     * Returns information about data availablity for the given scheduled time.
     */
    public abstract TriggerStatus getTriggerStatus(Scheduler scheduler, ScheduledTime now, ScheduledTime scheduledTime) throws Exception;

    /**
     * Returns true if data is available for the given scheduled time, false if not.
     * For clients that don't need the full TriggerStatus information.
     */
    public final boolean isDataAvailable(Scheduler scheduler, ScheduledTime now, ScheduledTime scheduledTime) throws Exception {
        return getTriggerStatus(scheduler, now, scheduledTime).isReady();
    }

    protected final TriggerStatus makeTriggerStatus(boolean ready, String description, List<TriggerStatus> subStatuses) {
        return new TriggerStatus(this.getClass().getName(), ready, description, subStatuses);
    }
    
    protected final TriggerStatus makeTriggerStatus(boolean ready, String description) {
        return makeTriggerStatus(ready, description, Collections.emptyList());
    }
    
}
