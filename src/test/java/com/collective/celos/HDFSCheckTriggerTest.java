package com.collective.celos;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class HDFSCheckTriggerTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    
    @Test(expected = NullPointerException.class)
    public void testNeedsPath() {
        Trigger trigger = new HDFSCheckTrigger();
        trigger.isDataAvailable(null, new Properties());
    }

    @Test
    public void testDirectoryExists() {
        Trigger trigger = new HDFSCheckTrigger();
        Properties props = new Properties();
        props.setProperty(HDFSCheckTrigger.PATH_KEY, "/tmp");
        assertTrue(trigger.isDataAvailable(new ScheduledTime("2013-11-22T15:00Z"), props));
    }

    @Test
    public void testDirectoryDoesNotExist() {
        Trigger trigger = new HDFSCheckTrigger();
        Properties props = new Properties();
        props.setProperty(HDFSCheckTrigger.PATH_KEY, "/tmp-does-not-exist");
        assertFalse(trigger.isDataAvailable(new ScheduledTime("2013-11-22T15:00Z"), props));
    }

    @Test
    public void testFileExists() throws IOException {
        String root = tempFolder.getRoot().getPath();
        
        File triggerFile = new File(root, "2013-11-22/1500/_READY");
        triggerFile.getParentFile().mkdirs();
        triggerFile.createNewFile();
       
        Trigger trigger = new HDFSCheckTrigger();
        Properties props = new Properties();
        props.setProperty(HDFSCheckTrigger.PATH_KEY, root + "/${year}-${month}-${day}/${hour}00/_READY");
        
        assertTrue(trigger.isDataAvailable(new ScheduledTime("2013-11-22T15:00Z"), props));
    }

    @Test
    public void testFileDoesNotExist() {
        String root = tempFolder.getRoot().getPath();
        
        File triggerFile = new File(root, "2013-11-22/1500/_READY");
        if (triggerFile.exists()) {
            triggerFile.delete(); // Make sure it doesn't exist
        }
       
        Trigger trigger = new HDFSCheckTrigger();
        Properties props = new Properties();
        props.setProperty(HDFSCheckTrigger.PATH_KEY, root + "/${year}-${month}-${day}/${hour}00/_READY");
        
        assertFalse(trigger.isDataAvailable(new ScheduledTime("2013-11-22T15:00Z"), props));
    }

}
