package com.collective.celos;

import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Check in HDFS for a data dependency.
 */
public class HDFSCheckTrigger implements Trigger {

    public static final String PATH_PROP = "celos.hdfs.path";
    public static final String FS_PROP = "celos.hdfs.fs";

    private final ScheduledTimeFormatter formatter = new ScheduledTimeFormatter();
    private final FileSystem fs;
    private final String rawPathString;
    private final String fsString;

    public HDFSCheckTrigger(ObjectNode properties) throws Exception {
        this.rawPathString = Util.getStringProperty(properties, PATH_PROP);
        this.fsString = Util.getStringProperty(properties, FS_PROP);
        this.fs = FileSystem.get(new URI(getFsString()), new Configuration());
    }
    
    @Override
    public boolean isDataAvailable(ScheduledTime now, ScheduledTime t) throws Exception {
        Path path = new Path(formatter.replaceTimeTokens(rawPathString, t));
        return fs.exists(path);
    }

    public String getFsString() {
        return fsString;
    }

    public String getRawPathString() {
        return rawPathString;
    }

}
