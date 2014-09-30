package com.collective.celos.trigger;

import com.collective.celos.ScheduledTime;
import com.collective.celos.Scheduler;

/**
 * Trivial trigger that always signals data availability,
 * for use when a workflow doesn't have any data dependencies
 * and simply needs to run at every scheduled time. 
 */
public class AlwaysTrigger implements Trigger {

    public AlwaysTrigger() {    
    }
    
    @Override
    public boolean isDataAvailable(Scheduler s, ScheduledTime now, ScheduledTime t) {
        return true;
    }

}
