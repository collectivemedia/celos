package com.collective.celos;

import com.collective.celos.trigger.Trigger;

/**
 * Utility trigger for testing that never signals data availability.
 */
public class NeverTrigger implements Trigger {

    public NeverTrigger() {
    }
    
    @Override
    public boolean isDataAvailable(Scheduler scheduler, ScheduledTime now, ScheduledTime t) throws Exception {
        return false;
    }

}
