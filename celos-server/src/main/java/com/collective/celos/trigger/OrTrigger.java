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

    private boolean checkTrigger(List<TriggerStatusPOJO> subStatuses) throws Exception {
        for (TriggerStatusPOJO status : subStatuses) {
            if (status.isReady()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String humanReadableDescription(boolean ready, ScheduledTime scheduledTime) {
        return "OR";
    }

    @Override
    public TriggerStatusPOJO makeStatusObject(Scheduler scheduler, ScheduledTime now, ScheduledTime scheduledTime) throws Exception {
        final List<TriggerStatusPOJO> subStatuses = new ArrayList<>();
        for (Trigger trigger : triggers) {
            subStatuses.add(trigger.makeStatusObject(scheduler, now, scheduledTime));
        }
        boolean ready = this.checkTrigger(subStatuses);
        final String description = this.humanReadableDescription(ready, scheduledTime);
        return new TriggerStatusPOJO(ready, description, subStatuses);
    }


    public List<Trigger> getTriggers() {
        return triggers;
    }

}
