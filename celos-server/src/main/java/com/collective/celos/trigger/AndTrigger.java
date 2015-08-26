package com.collective.celos.trigger;

import com.collective.celos.ScheduledTime;
import com.collective.celos.Scheduler;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class AndTrigger extends Trigger {

    private final List<Trigger> triggers = new LinkedList<>();
    
    public AndTrigger(List<Trigger> triggers) throws Exception {
        this.triggers.addAll(triggers);
    }
    
    private boolean checkTrigger(List<TriggerStatusPOJO> subStatuses) throws Exception {
        for (TriggerStatusPOJO stat : subStatuses) {
            if (!stat.isReady()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public TriggerStatusPOJO makeStatusObject(Scheduler scheduler, ScheduledTime now, ScheduledTime scheduledTime) throws Exception {
        final List<TriggerStatusPOJO> subStatuses = new ArrayList<>();
        for (Trigger trigger : triggers) {
            subStatuses.add(trigger.makeStatusObject(scheduler, now, scheduledTime));
        }
        boolean ready = this.checkTrigger(subStatuses);
        return new TriggerStatusPOJO(ready, this.description(), subStatuses);
    }

    public List<Trigger> getTriggers() {
        return triggers;
    }

}
