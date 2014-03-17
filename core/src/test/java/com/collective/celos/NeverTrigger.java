package com.collective.celos;

import com.collective.celos.api.ScheduledTime;
import com.collective.celos.api.Trigger;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Utility trigger for testing that never signals data availability.
 */
public class NeverTrigger implements Trigger {

    public NeverTrigger(ObjectNode ignored) {
    }
    
    @Override
    public boolean isDataAvailable(ScheduledTime now, ScheduledTime t) throws Exception {
        return false;
    }

}
