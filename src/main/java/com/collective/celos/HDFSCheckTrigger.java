package com.collective.celos;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;

/**
 * Check in HDFS for a data dependency.
 */
public class HDFSCheckTrigger implements Trigger {

    private final ScheduledTimeFormatter formatter = new ScheduledTimeFormatter();
    private final FileSystem fs;
    private final String rawPathString;
    private final String fsString;

    private static final Map<String, FileSystem> cachedFSs = new HashMap<>();
    
    private static final Logger LOGGER = Logger.getLogger(HDFSCheckTrigger.class);
    
    public HDFSCheckTrigger(String rawPathString, String fsString) throws Exception {
        this.rawPathString = Util.requireNonNull(rawPathString);
        this.fsString = Util.requireNonNull(fsString);

        if (cachedFSs.containsKey(fsString)) {
            this.fs = cachedFSs.get(fsString);
        } else {
            this.fs = FileSystem.get(new URI(fsString), new Configuration());
            cachedFSs.put(fsString, this.fs);
        }
    }

    @Override
    public boolean isDataAvailable(Scheduler scheduler, ScheduledTime now, ScheduledTime t) throws Exception {
        Path path = new Path(formatter.replaceTimeTokens(rawPathString, t));
        LOGGER.info("Checking HDFS path: " + path);
        return fs.exists(path);
    }

    public String getFsString() {
        return fsString;
    }

    public String getRawPathString() {
        return rawPathString;
    }

}
