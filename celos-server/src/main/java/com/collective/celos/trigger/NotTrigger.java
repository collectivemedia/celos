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
    public TriggerStatusPOJO makeStatusObject(Scheduler scheduler, ScheduledTime now, ScheduledTime scheduledTime) throws Exception {
        final TriggerStatusPOJO statusPOJO = trigger.makeStatusObject(scheduler, now, scheduledTime);
        final boolean ready = !statusPOJO.isReady();
        return new TriggerStatusPOJO(ready, this.description(), Collections.singletonList(statusPOJO));
    }

    public Trigger getTrigger() {
        return trigger;
    }

}
