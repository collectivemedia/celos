package com.collective.celos;


public class NotTrigger implements Trigger {

    private final Trigger trigger;
    
    public NotTrigger(Trigger trigger) throws Exception {
        this.trigger = (Trigger) Util.requireNonNull(trigger);
    }
    
    @Override
    public boolean isDataAvailable(ScheduledTime now, ScheduledTime scheduledTime) throws Exception {
        return !trigger.isDataAvailable(now, scheduledTime);
    }

    public Trigger getTrigger() {
        return trigger;
    }

}
