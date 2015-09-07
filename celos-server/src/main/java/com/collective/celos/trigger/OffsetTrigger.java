package com.collective.celos.trigger;

import com.collective.celos.ScheduledTime;
import com.collective.celos.Scheduler;
import com.collective.celos.Util;

import java.util.Collections;

/**
 * Trigger that offsets a nested trigger into the past or future.
 */
public class OffsetTrigger extends Trigger {

    private final int seconds;
    private final Trigger trigger;

    public OffsetTrigger(int seconds, Trigger trigger) {
        this.seconds = seconds;
        this.trigger = Util.requireNonNull(trigger);
    }

    @Override
    public TriggerStatus getTriggerStatus(Scheduler scheduler, ScheduledTime now, ScheduledTime scheduledTime) throws Exception {
        TriggerStatus status = trigger.getTriggerStatus(scheduler, now, scheduledTime.plusSeconds(seconds));
        boolean ready = status.isReady();
        return makeTriggerStatus(ready, humanReadableDescription(ready), Collections.singletonList(status));
    }

    private String humanReadableDescription(boolean ready) {
        if (ready) {
            return "Nested trigger offset by " + getSeconds() + " seconds ready";
        } else {
            return  "Nested trigger offset by " + getSeconds() + " seconds not ready";
        }
    }

    public int getSeconds() {
        return seconds;
    }

    public Trigger getTrigger() {
        return trigger;
    }

}
