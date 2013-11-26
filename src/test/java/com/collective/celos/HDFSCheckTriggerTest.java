package com.collective.celos;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Properties;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class HDFSCheckTriggerTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    
    @Test(expected = IllegalArgumentException.class)
    public void testNeedsPath() throws Exception {
        new HDFSCheckTrigger(new Properties());
    }

    @Test
    public void testDirectoryExists() throws Exception {
        Properties props = new Properties();
        props.setProperty(HDFSCheckTrigger.PATH_PROP, "/tmp");
        assertTrue(new HDFSCheckTrigger(props).isDataAvailable(new ScheduledTime("2013-11-22T15:00Z")));
    }

    @Test
    public void testDirectoryDoesNotExist() throws Exception {
        Properties props = new Properties();
        props.setProperty(HDFSCheckTrigger.PATH_PROP, "/tmp-does-not-exist");
        assertFalse(new HDFSCheckTrigger(props).isDataAvailable(new ScheduledTime("2013-11-22T15:00Z")));
    }

    @Test
    public void testFileExists() throws Exception {
        String root = tempFolder.getRoot().getPath();
        
        File triggerFile = new File(root, "2013-11-22/1500/_READY");
        triggerFile.getParentFile().mkdirs();
        triggerFile.createNewFile();
       
        Properties props = new Properties();
        props.setProperty(HDFSCheckTrigger.PATH_PROP, root + "/${year}-${month}-${day}/${hour}00/_READY");
        
        assertTrue(new HDFSCheckTrigger(props).isDataAvailable(new ScheduledTime("2013-11-22T15:00Z")));
    }

    @Test
    public void testFileDoesNotExist() throws Exception {
        String root = tempFolder.getRoot().getPath();
        
        File triggerFile = new File(root, "2013-11-22/1500/_READY");
        if (triggerFile.exists()) {
            triggerFile.delete(); // Make sure it doesn't exist
        }
       
        Properties props = new Properties();
        props.setProperty(HDFSCheckTrigger.PATH_PROP, root + "/${year}-${month}-${day}/${hour}00/_READY");
        
        assertFalse(new HDFSCheckTrigger(props).isDataAvailable(new ScheduledTime("2013-11-22T15:00Z")));
    }
    
    /*
     * I was expecting to see an UnknownHostException here, but to my surprise
     * org.apache.hadoop.security.SecurityUtil ends up wrapping it in an
     * IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testIOExceptionIsPropagated() throws Exception {
        Properties props = new Properties();
        props.setProperty(HDFSCheckTrigger.PATH_PROP, "hdfs://no-such-host/some/path");
        new HDFSCheckTrigger(props).isDataAvailable(new ScheduledTime("2013-11-22T15:00Z"));
    }

}
