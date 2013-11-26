package com.collective.celos;

import java.net.URI;
import java.util.Properties;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

/*
 * Check in HDFS for a data dependency.
 */
public class HDFSCheckTrigger implements Trigger {

    public static final String PATH_KEY = "path";

    /*
     * This object knows how to replace the date/time tokens in the path.
     */
    private ScheduledTimeFormatter formatter = new ScheduledTimeFormatter();

    @Override
    public boolean isDataAvailable(ScheduledTime t, Properties props) throws Exception {
        String rawPathString = props.getProperty(PATH_KEY);
        Util.requireNonNull(rawPathString);

        String cookedPathString = formatter.replaceTimeTokens(
                rawPathString, t);

        /*
         * We use unqualified paths for our triggers (that is, they don't
         * specify the scheme of "hdfs"). 
         * 
         * For reasons I don't fully understand, this code treats these
         * paths slightly differently depending on some run-time setting:
         * 
         * 1) In unit tests on a dev box and oj01, an unqualified path is
         * treated as a local file.  That is, "fs" is an instance of
         * LocalFileSystem.
         * 
         * 2) When run using "java" or "hadoop jar" on a dev box, the same
         * is true.
         * 
         * 3) But, when run using "java" or "hadoop jar" on oj01, fs is in
         * instance of DistributedFileSystem.
         * 
         * So, unit test are written to set up and test the local file
         * system.  Celos itself can also be run locally, accessing the
         * local file system.  But when run on oj01 (the likely deployment
         * platform) this code will access paths from HDFS.
         * 
         * NOTE: this is the approach that Sibyl has been using since it
         * was first deployed.
         */
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(URI.create(cookedPathString), conf);
        
        Path path = new Path(cookedPathString);
        boolean result = fs.exists(path);
        
        return result;
    }

}
