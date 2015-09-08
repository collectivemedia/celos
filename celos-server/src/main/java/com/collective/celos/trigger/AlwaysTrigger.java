package com.collective.celos.trigger;

import com.collective.celos.ScheduledTime;
import com.collective.celos.Scheduler;

/**
 * Trivial trigger that always signals data availability,
 * for use when a workflow doesn't have any data dependencies
 * and simply needs to run at every scheduled time. 
 */
public class AlwaysTrigger extends Trigger {

    public AlwaysTrigger() {    
    }

    @Override
    public TriggerStatus getTriggerStatus(Scheduler scheduler, ScheduledTime now, ScheduledTime scheduledTime) throws Exception {
        return makeTriggerStatus(true, "Always ready");
    }

}
