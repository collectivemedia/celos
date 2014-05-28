package com.collective.celos;

/**
 * Utility trigger for testing that never signals data availability.
 */
public class NeverTrigger extends Trigger {

    public NeverTrigger() {
    }
    
    @Override
    public boolean isDataAvailable(ScheduledTime now, ScheduledTime t) throws Exception {
        return false;
    }

}
