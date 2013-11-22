package com.collective.celos;

import java.util.Map;

/**
 * A trigger is called to determined data availability for a workflow.
 */
public interface Trigger {
    
    /**
     * Returns true if data is available for the given scheduled time, false if not.
     * 
     * The props come from the workflow configuration and are
     * specific to each trigger implementation.
     */
    public boolean isDataAvailable(ScheduledTime t, Map<String, String> props);

}
