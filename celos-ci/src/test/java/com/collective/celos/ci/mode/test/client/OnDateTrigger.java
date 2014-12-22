package com.collective.celos.ci.mode.test.client;

import com.collective.celos.ScheduledTime;
import com.collective.celos.Scheduler;
import com.collective.celos.Trigger;

public class OnDateTrigger implements Trigger {

    private final ScheduledTime scheduledTime;

    public OnDateTrigger(String scheduledTime) {
        this(new ScheduledTime(scheduledTime));
    }

    public OnDateTrigger(ScheduledTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    @Override
    public boolean isDataAvailable(Scheduler scheduler, ScheduledTime now, ScheduledTime t) throws Exception {
        return t.equals(scheduledTime);
    }
}
