package com.collective.celos;

import java.io.IOException;
import java.util.Properties;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class HDFSCheckTrigger implements Trigger {

    // TODO: move this to somewhere that other code can access them?
    public static final String PATH_KEY = "path";

    /*
     * This object knows how to replace the date/time tokens in the path.
     */
    private TokenReplacer tokenReplacer = new TokenReplacer();

    @Override
    public boolean isDataAvailable(ScheduledTime t, Properties props) {
        String rawPathString = props.getProperty(PATH_KEY);
        Util.requireNonNull(rawPathString);

        String cookedPathString = tokenReplacer.replaceTimeTokens(
                rawPathString, t);

        try {
            Path path = new Path(cookedPathString);
            Configuration conf = new Configuration();
            FileSystem fs = path.getFileSystem(conf);
            System.out.println("fs=" + fs);
            boolean result = fs.exists(path);
            return result;
        } catch (IOException e) {
            // TODO: Log exception, or throw?
            return false;
        }
    }

}
