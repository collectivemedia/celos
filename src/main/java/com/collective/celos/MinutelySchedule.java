package com.collective.celos;

public class MinutelySchedule extends CronSchedule {

    public MinutelySchedule() {
        super("0 * * * * ?");
    }
    
}
