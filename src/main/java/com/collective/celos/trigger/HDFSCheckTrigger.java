package com.collective.celos.trigger;

import com.collective.celos.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Check in HDFS for a data dependency.
 */
public class HDFSCheckTrigger implements Trigger {

    private static final Map<String, FileSystem> cachedFSs = new HashMap<>();
    private final ScheduledTimeFormatter formatter = new ScheduledTimeFormatter();

    private static final Logger LOGGER = Logger.getLogger(HDFSCheckTrigger.class);
    private final FileSystem fs;
    private final String rawPathString;
    private final String fsString;

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
        Path path = new Path(formatter.replaceTimeTokens(getRawPathString(), t));
        LOGGER.info("Checking HDFS path: " + path);
        return getFs().exists(path);
    }


    public String getRawPathString() {
        return rawPathString;
    }

    public String getFsString() {
        return fsString;
    }

    public FileSystem getFs() {
        return fs;
    }
}
