package com.collective.celos.trigger;


import com.collective.celos.ScheduledTime;
import com.collective.celos.Scheduler;
import com.collective.celos.Util;

import java.util.Collections;

/**
 * Trigger that takes a nested trigger and does a logical NOT.
 */
public class NotTrigger extends Trigger {

    private final Trigger trigger;
    
    public NotTrigger(Trigger trigger) throws Exception {
        this.trigger = Util.requireNonNull(trigger);
    }

    @Override
    public TriggerStatus getTriggerStatus(Scheduler scheduler, ScheduledTime now, ScheduledTime scheduledTime) throws Exception {
        TriggerStatus status = trigger.getTriggerStatus(scheduler, now, scheduledTime);
        boolean ready = !status.isReady();
        return makeTriggerStatus(ready, humanReadableDescription(ready), Collections.singletonList(status));
    }

    private String humanReadableDescription(boolean ready) {
        if (ready) {
            return "Ready, nested trigger isn't ready";
        } else {
            return "Not ready, nested trigger is ready";
        }
    }

    public Trigger getTrigger() {
        return trigger;
    }

}
