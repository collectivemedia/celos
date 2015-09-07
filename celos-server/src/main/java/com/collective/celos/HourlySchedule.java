package com.collective.celos;

/**
 * Simple schedule for hourly execution.
 */
public class HourlySchedule extends CronSchedule {

    public HourlySchedule() {
        super("0 0 * * * ?");
    }
    
}
