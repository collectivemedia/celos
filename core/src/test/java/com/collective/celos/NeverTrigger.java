package com.collective.celos;

import com.collective.celos.exposed.ScheduledTime;
import com.collective.celos.exposed.Trigger;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Utility trigger for testing that never signals data availability.
 */
public class NeverTrigger implements Trigger {

    public NeverTrigger(ObjectNode ignored) {
    }
    
    @Override
    public boolean isDataAvailable(ScheduledTime t) throws Exception {
        return false;
    }

}
