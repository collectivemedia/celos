package com.collective.celos.trigger;

import com.collective.celos.ScheduledTime;
import com.collective.celos.Scheduler;

import java.util.LinkedList;
import java.util.List;

public class AndTrigger implements Trigger {

    private final List<Trigger> triggers = new LinkedList<>();
    
    public AndTrigger(List<Trigger> triggers) throws Exception {
        this.triggers.addAll(triggers);
    }
    
    @Override
    public boolean isDataAvailable(Scheduler sched, ScheduledTime now, ScheduledTime t) throws Exception {
        for (Trigger trigger : triggers) {
            if (!trigger.isDataAvailable(sched, now, t)) {
                return false;
            }
        }
        return true;
    }

    public List<Trigger> getTriggers() {
        return triggers;
    }

}
