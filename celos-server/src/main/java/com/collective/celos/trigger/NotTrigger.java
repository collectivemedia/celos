package com.collective.celos.trigger;


import com.collective.celos.ScheduledTime;
import com.collective.celos.Scheduler;
import com.collective.celos.Util;

import java.util.Collections;

public class NotTrigger extends Trigger {

    private final Trigger trigger;
    
    public NotTrigger(Trigger trigger) throws Exception {
        this.trigger = Util.requireNonNull(trigger);
    }

    @Override
    public TriggerStatus getTriggerStatus(Scheduler scheduler, ScheduledTime now, ScheduledTime scheduledTime) throws Exception {
        final TriggerStatus statusPOJO = trigger.getTriggerStatus(scheduler, now, scheduledTime);
        final boolean ready = !statusPOJO.isReady();
        final String description = this.humanReadableDescription(ready, scheduledTime);
        return new TriggerStatus(ready, description, Collections.singletonList(statusPOJO));
    }

    private String humanReadableDescription(boolean ready, ScheduledTime scheduledTime) {
        return "NOT";
    }

    public Trigger getTrigger() {
        return trigger;
    }

}
