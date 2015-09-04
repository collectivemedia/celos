package com.collective.celos.trigger;

import com.collective.celos.ScheduledTime;
import com.collective.celos.Scheduler;

import java.util.Collections;

/**
 * Trivial trigger that always signals data availability,
 * for use when a workflow doesn't have any data dependencies
 * and simply needs to run at every scheduled time. 
 */
public class AlwaysTrigger extends Trigger {

    public AlwaysTrigger() {    
    }

    @Override
    public TriggerStatus getTriggerStatus(Scheduler scheduler, ScheduledTime now, ScheduledTime scheduledTime) throws Exception {
        final String description = this.humanReadableDescription(true, scheduledTime);
        return new TriggerStatus(true, description, Collections.<TriggerStatus>emptyList());
    }

    private String humanReadableDescription(boolean ready, ScheduledTime scheduledTime) {
        return "Always succeed";
    }

}
