package com.collective.celos;

import java.net.URI;
import java.util.Properties;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

/**
 * Check in HDFS for a data dependency.
 */
public class HDFSCheckTrigger implements Trigger {

    public static final String PATH_PROP = "celos.hdfs.path";
    public static final String FS_PROP = "celos.hdfs.fs";

    private final ScheduledTimeFormatter formatter = new ScheduledTimeFormatter();
    private final FileSystem fs;
    private final String rawPathString;

    public HDFSCheckTrigger(Properties properties) throws Exception {
        this.rawPathString = properties.getProperty(PATH_PROP);
        if (this.rawPathString == null) {
            throw new IllegalArgumentException(PATH_PROP + " property not set.");
        }
        String fsString = properties.getProperty(FS_PROP);
        if (fsString == null) {
            throw new IllegalArgumentException(FS_PROP + " property not set.");
        }
        this.fs = FileSystem.get(new URI(fsString), new Configuration());
    }
    
    @Override
    public boolean isDataAvailable(ScheduledTime t) throws Exception {
        Path path = new Path(formatter.replaceTimeTokens(rawPathString, t));
        return fs.exists(path);
    }

}
