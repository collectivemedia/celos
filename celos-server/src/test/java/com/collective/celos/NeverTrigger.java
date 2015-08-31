package com.collective.celos;

import com.collective.celos.trigger.Trigger;
import com.collective.celos.trigger.TriggerStatus;

import java.util.Collections;

/**
 * Utility trigger for testing that never signals data availability.
 */
public class NeverTrigger extends Trigger {

    @Override
    public TriggerStatus getTriggerStatus(Scheduler scheduler, ScheduledTime now, ScheduledTime scheduledTime) throws Exception {
        final boolean ready = false;
        final String description = "NeverTrigger";
        return new TriggerStatus(ready, description, Collections.<TriggerStatus>emptyList());
    }

}
