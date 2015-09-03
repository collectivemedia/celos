package com.collective.celos.trigger;

import com.collective.celos.ScheduledTime;
import com.collective.celos.Scheduler;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class OrTrigger extends Trigger {

    private final List<Trigger> triggers = new LinkedList<>();
    
    public OrTrigger(List<Trigger> triggers) throws Exception {
        this.triggers.addAll(triggers);
    }

    private boolean checkTrigger(List<TriggerStatus> subStatuses) throws Exception {
        for (TriggerStatus status : subStatuses) {
            if (status.isReady()) {
                return true;
            }
        }
        return false;
    }

    private String humanReadableDescription(boolean ready) {
        if (ready) {
            return "One or more nested triggers are ready";
        } else {
            return "None of the nested triggers are ready";
        }
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


    public List<Trigger> getTriggers() {
        return triggers;
    }

}
