package com.collective.celos;

/**
 * Utility trigger for testing that never signals data availability.
 */
public class NeverTrigger implements Trigger {

    @Override
    public boolean isDataAvailable(ScheduledTime t) throws Exception {
        return false;
    }

}
