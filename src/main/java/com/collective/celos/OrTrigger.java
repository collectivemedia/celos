package com.collective.celos;

import java.util.LinkedList;
import java.util.List;

public class OrTrigger implements Trigger {

    private final List<Trigger> triggers = new LinkedList<>();
    
    public OrTrigger(List<Trigger> triggers) throws Exception {
        this.triggers.addAll(triggers);
    }
    
    @Override
    public boolean isDataAvailable(Scheduler sched, ScheduledTime now, ScheduledTime t) throws Exception {
        for (Trigger trigger : triggers) {
            if (trigger.isDataAvailable(sched, now, t)) {
                return true;
            }
        }
        return false;
    }

    public List<Trigger> getTriggers() {
        return triggers;
    }

}
