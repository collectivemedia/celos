package com.collective.celos;

import java.util.Collections;
import java.util.Set;

/**
 * Trivial trigger that always signals data availability,
 * for use when a workflow doesn't have any data dependencies
 * and simply needs to run at every scheduled time. 
 */
public class AlwaysTrigger extends Trigger {

    public AlwaysTrigger() {    
    }
    
    @Override
    public boolean isDataAvailable(ScheduledTime now, ScheduledTime t) {
        return true;
    }

}
