package com.collective.celos;

import java.io.File;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class Main {

    private static final String CONFIG_PATH = "/etc/celos/workflows";
    private static final String DB_PATH = "/var/run/celos/db";

    public static void main(String[] args) throws Exception {
        WorkflowConfiguration config =
                new WorkflowConfigurationParser().parseConfiguration(new File(CONFIG_PATH));
        StateDatabase db = new FileSystemStateDatabase(new File(DB_PATH));
        int slidingWindowHours = 24 * 7;
        new Scheduler(config, db, slidingWindowHours).step(new ScheduledTime(DateTime.now(DateTimeZone.UTC)));
    }

}
