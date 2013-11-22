package com.collective.celos;

import java.util.Map;

/**
 * Trivial trigger that always signals data availability,
 * for use when a workflow doesn't have any data dependencies
 * and simply needs to run at every scheduled time. 
 */
public class TrivialTrigger implements Trigger {

    public boolean isDataAvailable(ScheduledTime t, Map<String, String> unusedProps) {
        return true;
    }

}
