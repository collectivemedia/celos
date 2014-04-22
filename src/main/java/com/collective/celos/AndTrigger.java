package com.collective.celos;

import java.util.LinkedList;
import java.util.List;

public class AndTrigger implements Trigger {

    private final List<Trigger> triggers = new LinkedList<>();
    
    public AndTrigger(List<Trigger> triggers) throws Exception {
        this.triggers.addAll(triggers);
    }
    
    @Override
    public boolean isDataAvailable(ScheduledTime now, ScheduledTime t) throws Exception {
        for (Trigger trigger : triggers) {
            if (!trigger.isDataAvailable(now, t)) {
                return false;
            }
        }
        return true;
    }

    public List<Trigger> getTriggers() {
        return triggers;
    }

}
