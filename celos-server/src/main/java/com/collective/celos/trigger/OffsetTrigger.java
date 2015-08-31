package com.collective.celos.trigger;

import com.collective.celos.ScheduledTime;
import com.collective.celos.Scheduler;
import com.collective.celos.Util;

import java.util.Collections;

/**
 * var oneDay = 60 * 60 * 24;
 * andTrigger(offsetTrigger(oneDay, hdfsCheckTrigger("/${year}/${month}/${day}/..."))
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
        final TriggerStatus statusPOJO = trigger.getTriggerStatus(scheduler, now, scheduledTime.plusSeconds(seconds));
        final boolean ready = statusPOJO.isReady();
        final String description = this.humanReadableDescription(ready, scheduledTime);
        return new TriggerStatus(ready, description, Collections.singletonList(statusPOJO));
    }

    private String humanReadableDescription(boolean ready, ScheduledTime scheduledTime) {
        return "Offset " + Integer.toString(seconds) + " seconds";
    }

    public int getSeconds() {
        return seconds;
    }

    public Trigger getTrigger() {
        return trigger;
    }

}
