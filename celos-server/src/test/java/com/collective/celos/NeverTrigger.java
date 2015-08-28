package com.collective.celos;

import com.collective.celos.trigger.Trigger;
import com.collective.celos.trigger.TriggerStatusPOJO;

import java.util.Collections;

/**
 * Utility trigger for testing that never signals data availability.
 */
public class NeverTrigger extends Trigger {

    @Override
    public TriggerStatusPOJO makeStatusObject(Scheduler scheduler, ScheduledTime now, ScheduledTime scheduledTime) throws Exception {
        final boolean ready = false;
        final String description = this.humanReadableDescription(ready, scheduledTime);
        return new TriggerStatusPOJO(ready, description, Collections.<TriggerStatusPOJO>emptyList());
    }

    @Override
    public String humanReadableDescription(boolean ready, ScheduledTime scheduledTime) {
        return "NeverTrigger";
    }

}
