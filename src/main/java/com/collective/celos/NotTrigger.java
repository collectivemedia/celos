package com.collective.celos;


public class NotTrigger implements Trigger {

    private final Trigger trigger;
    
    public NotTrigger(Trigger trigger) throws Exception {
        this.trigger = Util.requireNonNull(trigger);
    }
    
    @Override
    public boolean isDataAvailable(Scheduler scheduler, ScheduledTime now, ScheduledTime scheduledTime) throws Exception {
        return !trigger.isDataAvailable(scheduler, now, scheduledTime);
    }

    public Trigger getTrigger() {
        return trigger;
    }

}
