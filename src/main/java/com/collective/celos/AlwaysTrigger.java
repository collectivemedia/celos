package com.collective.celos;

import java.util.Properties;

/**
 * Trivial trigger that always signals data availability,
 * for use when a workflow doesn't have any data dependencies
 * and simply needs to run at every scheduled time. 
 */
public class AlwaysTrigger implements Trigger {

    AlwaysTrigger() {    
    }

    public AlwaysTrigger(Properties ignored) {    
    }
    
    public boolean isDataAvailable(ScheduledTime t) {
        return true;
    }

}
