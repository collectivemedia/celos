package com.collective.celos;

import com.collective.celos.api.ScheduledTime;
import com.collective.celos.api.Trigger;

/**
 * Extension of the Trigger interface for triggers that need access to Scheduler internals.
 * 
 * The normal isDataAvailable(ScheduledTime now, ScheduledTime t) of an internal trigger is NOT called.
 * (Yeah, not really elegant but changing all Trigger code and tests is not possible now.)
 * 
 * Instead the scheduler calls the isDataAvailable(Scheduler s, ScheduledTime now, ScheduledTime t) method
 * and passes itself as first argument.
 */
public interface InternalTrigger extends Trigger {

    public boolean isDataAvailable(Scheduler s, ScheduledTime now, ScheduledTime t) throws Exception;
    
}
