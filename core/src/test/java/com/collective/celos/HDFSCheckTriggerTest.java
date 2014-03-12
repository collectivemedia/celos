package com.collective.celos;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import com.collective.celos.exposed.ScheduledTime;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class HDFSCheckTriggerTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    
    @Test(expected = IllegalArgumentException.class)
    public void testNeedsPath() throws Exception {
        new HDFSCheckTrigger(Util.newObjectNode());
    }

    @Test
    public void testDirectoryExists() throws Exception {
        ObjectNode props = Util.newObjectNode();
        props.put(HDFSCheckTrigger.PATH_PROP, "/tmp");
        props.put(HDFSCheckTrigger.FS_PROP, "file:///");
        assertTrue(new HDFSCheckTrigger(props).isDataAvailable(new ScheduledTimeImpl("2013-11-22T15:00Z")));
    }

    @Test
    public void testDirectoryDoesNotExist() throws Exception {
        ObjectNode props = Util.newObjectNode();
        props.put(HDFSCheckTrigger.PATH_PROP, "/tmp-does-not-exist");
        props.put(HDFSCheckTrigger.FS_PROP, "file:///");
        assertFalse(new HDFSCheckTrigger(props).isDataAvailable(new ScheduledTimeImpl("2013-11-22T15:00Z")));
    }

    @Test
    public void testFileExists() throws Exception {
        String root = tempFolder.getRoot().getPath();
        
        File triggerFile = new File(root, "2013-11-22/1500/_READY");
        triggerFile.getParentFile().mkdirs();
        triggerFile.createNewFile();
       
        ObjectNode props = Util.newObjectNode();
        props.put(HDFSCheckTrigger.PATH_PROP, root + "/${year}-${month}-${day}/${hour}00/_READY");
        props.put(HDFSCheckTrigger.FS_PROP, "file:///");

        assertTrue(new HDFSCheckTrigger(props).isDataAvailable(new ScheduledTimeImpl("2013-11-22T15:00Z")));
    }

    @Test
    public void testFileDoesNotExist() throws Exception {
        String root = tempFolder.getRoot().getPath();
        
        File triggerFile = new File(root, "2013-11-22/1500/_READY");
        if (triggerFile.exists()) {
            triggerFile.delete(); // Make sure it doesn't exist
        }
       
        ObjectNode props = Util.newObjectNode();
        props.put(HDFSCheckTrigger.PATH_PROP, root + "/${year}-${month}-${day}/${hour}00/_READY");
        props.put(HDFSCheckTrigger.FS_PROP, "file:///");

        assertFalse(new HDFSCheckTrigger(props).isDataAvailable(new ScheduledTimeImpl("2013-11-22T15:00Z")));
    }
    
    /*
     * I was expecting to see an UnknownHostException here, but to my surprise
     * org.apache.hadoop.security.SecurityUtil ends up wrapping it in an
     * IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testIOExceptionIsPropagated() throws Exception {
        ObjectNode props = Util.newObjectNode();
        props.put(HDFSCheckTrigger.PATH_PROP, "/some/path");
        props.put(HDFSCheckTrigger.FS_PROP, "hdfs://no-such-host");
        new HDFSCheckTrigger(props).isDataAvailable(new ScheduledTimeImpl("2013-11-22T15:00Z"));
    }

}
