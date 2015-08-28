package com.collective.celos;

import com.collective.celos.trigger.Trigger;
import com.collective.celos.trigger.TriggerStatusPOJO;

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
    public TriggerStatusPOJO makeStatusObject(Scheduler scheduler, ScheduledTime now, ScheduledTime time) throws Exception {
        final boolean ready = time.equals(scheduledTime);
        final String description = this.humanReadableDescription(ready, scheduledTime);
        return new TriggerStatusPOJO(ready, description, Collections.<TriggerStatusPOJO>emptyList());
    }

    @Override
    public String humanReadableDescription(boolean ready, ScheduledTime scheduledTime) {
        return "OnDateTrigger";
    }


}
