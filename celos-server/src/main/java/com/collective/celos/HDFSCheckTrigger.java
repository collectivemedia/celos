package com.collective.celos;

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
            Configuration conf = new Configuration();
            //required due http://stackoverflow.com/questions/17265002/hadoop-no-filesystem-for-scheme-file
            conf.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
            conf.set("fs.file.impl", org.apache.hadoop.fs.LocalFileSystem.class.getName());

            this.fs = FileSystem.get(new URI(fsString), conf);
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
