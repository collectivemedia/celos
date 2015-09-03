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
    
    private boolean checkTrigger(List<TriggerStatus> subStatuses) throws Exception {
        for (TriggerStatus stat : subStatuses) {
            if (!stat.isReady()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public TriggerStatus getTriggerStatus(Scheduler scheduler, ScheduledTime now, ScheduledTime scheduledTime) throws Exception {
        final List<TriggerStatus> subStatuses = new ArrayList<>();
        for (Trigger trigger : triggers) {
            subStatuses.add(trigger.getTriggerStatus(scheduler, now, scheduledTime));
        }
        boolean ready = this.checkTrigger(subStatuses);
        final String description = this.humanReadableDescription(ready);
        return new TriggerStatus(ready, description, subStatuses);
    }

    private String humanReadableDescription(boolean ready) {
        if (ready) {
            return "All nested triggers are ready";
        } else {
            return "Not all nested triggers are ready";
        }
    }

    public List<Trigger> getTriggers() {
        return triggers;
    }

}
