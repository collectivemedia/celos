package com.collective.celos;

import static org.junit.Assert.*;

import java.util.Properties;

import org.junit.Test;

public class HDFSCheckTriggerTest {

    @Test(expected = NullPointerException.class)
    public void testNeedsPath() {
        Trigger trigger = new HDFSCheckTrigger();
        trigger.isDataAvailable(null, new Properties());
    }

    @Test
    public void testDrectoryExists() {
        Trigger trigger = new HDFSCheckTrigger();
        Properties props = new Properties();
        props.setProperty(HDFSCheckTrigger.PATH_KEY, "/tmp");
        assertTrue(trigger.isDataAvailable(new ScheduledTime("2013-11-22T15:00Z"), props));
    }

    @Test
    public void testDrectoryDoesNotExist() {
        Trigger trigger = new HDFSCheckTrigger();
        Properties props = new Properties();
        props.setProperty(HDFSCheckTrigger.PATH_KEY, "/tmp-does-not-exist");
        assertFalse(trigger.isDataAvailable(new ScheduledTime("2013-11-22T15:00Z"), props));
    }

}
