package com.collective.celos;

import java.io.File;

public class DefaultConfiguration {

    private static final String CONFIG_PATH = "/etc/celos/workflows";
    private static final String DB_PATH = "/var/lib/celos/db";

    public static Scheduler makeDefaultScheduler() throws Exception {
        File configFile = new File(CONFIG_PATH);
        WorkflowConfiguration config =
                new WorkflowConfigurationParser().parseConfiguration(configFile);
        StateDatabase db = new FileSystemStateDatabase(new File(DB_PATH));
        int slidingWindowHours = 24 * 7;
        return new Scheduler(config, db, slidingWindowHours);
    }

}
