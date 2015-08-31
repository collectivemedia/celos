package com.collective.celos;

import com.collective.celos.trigger.Trigger;
import com.collective.celos.trigger.TriggerStatus;

import java.util.Collections;

public class OnDateTrigger extends Trigger {

    private final ScheduledTime scheduledTime;

    public OnDateTrigger(String scheduledTime) {
        this(new ScheduledTime(scheduledTime));
    }

    public OnDateTrigger(ScheduledTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }


    @Override
    public TriggerStatus getTriggerStatus(Scheduler scheduler, ScheduledTime now, ScheduledTime time) throws Exception {
        final boolean ready = time.equals(scheduledTime);
        final String description = "OnDateTrigger";
        return new TriggerStatus(ready, description, Collections.<TriggerStatus>emptyList());
    }


}
