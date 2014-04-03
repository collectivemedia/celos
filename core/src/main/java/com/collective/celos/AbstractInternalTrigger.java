package com.collective.celos;

public abstract class AbstractInternalTrigger implements InternalTrigger {

    @Override
    public boolean isDataAvailable(ScheduledTime now, ScheduledTime t) throws Exception {
        throw new RuntimeException("BUG: called isDataAvailable(ScheduledTime,ScheduledTime) on internal trigger");
    }

}
