package com.collective.celos;

import java.util.*;

public class AndTrigger extends Trigger {

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

    public Set<WorkflowID> getWorkflowsTriggerDependsOn() {
        Set<WorkflowID> result = new HashSet<>();
        for (Trigger trigger : triggers) {
            result.addAll(trigger.getWorkflowsTriggerDependsOn());
        }
        return result;
    }


}
