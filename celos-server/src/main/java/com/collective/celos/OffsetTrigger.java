package com.collective.celos;

import org.joda.time.DateTime;

/**
 * var oneDay = 60 * 60 * 24;
 * celos.andTrigger(celos.offsetTrigger(oneDay, celos.hdfsCheckTrigger("/${year}/${month}/${day}/..."))
*/
public class OffsetTrigger implements Trigger {

    private final int seconds;
    private final Trigger trigger;

    public OffsetTrigger(int seconds, Trigger trigger) {
        this.seconds = seconds;
        this.trigger = Util.requireNonNull(trigger);
    }

    public boolean isDataAvailable(Scheduler scheduler, ScheduledTime now, ScheduledTime t) throws Exception {
        return trigger.isDataAvailable(scheduler, now, t.plusSeconds(seconds));
    }

    public int getSeconds() {
        return seconds;
    }

    public Trigger getTrigger() {
        return trigger;
    }
}
