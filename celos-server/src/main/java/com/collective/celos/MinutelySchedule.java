package com.collective.celos;

/**
 * Simple schedule for - ahem - minutely execution.
 */
public class MinutelySchedule extends CronSchedule {

    public MinutelySchedule() {
        super("0 * * * * ?");
    }
    
}
