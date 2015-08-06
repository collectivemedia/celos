package com.collective.celos;

public class HourlySchedule extends CronSchedule {

    public HourlySchedule() {
        super("0 0 * * * ?");
    }
    
}
