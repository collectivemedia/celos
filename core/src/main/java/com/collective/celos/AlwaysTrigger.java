package com.collective.celos;

import com.collective.celos.api.ScheduledTime;
import com.collective.celos.api.Trigger;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Trivial trigger that always signals data availability,
 * for use when a workflow doesn't have any data dependencies
 * and simply needs to run at every scheduled time. 
 */
public class AlwaysTrigger implements Trigger {

    public AlwaysTrigger(ObjectNode ignored) {    
    }
    
    @Override
    public boolean isDataAvailable(ScheduledTime t) {
        return true;
    }

}
