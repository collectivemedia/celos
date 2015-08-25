package com.collective.celos;

import com.collective.celos.trigger.HDFSCheckTrigger;
import com.collective.celos.trigger.OffsetTrigger;
import com.collective.celos.trigger.Trigger;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class OffsetTriggerTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private Scheduler scheduler;

    @Before
    public void prepare() throws IOException {
        String root = tempFolder.getRoot().getPath();

        File triggerFile = new File(root, "2013-11-22/1500/_READY");
        triggerFile.getParentFile().mkdirs();
        triggerFile.createNewFile();

        scheduler = mock(Scheduler.class);

    }

    @Test
    public void testTriggerAddsSomething() throws Exception {
        Trigger dependent = new HDFSCheckTrigger(tempFolder.getRoot().getPath() + "/${year}-${month}-${day}/${hour}00/_READY",  "file:///");

        Trigger offsetTriggerPlus = new OffsetTrigger(60*60, dependent);
        assertTrue(offsetTriggerPlus.isDataAvailable(scheduler, ScheduledTime.now(), new ScheduledTime("2013-11-22T14:00Z")));

    }

    @Test
    public void testTriggerDoesNothing() throws Exception {
        Trigger dependent = new HDFSCheckTrigger(tempFolder.getRoot().getPath() + "/${year}-${month}-${day}/${hour}00/_READY",  "file:///");

        Trigger offsetTriggerPlus = new OffsetTrigger(0, dependent);
        assertTrue(offsetTriggerPlus.isDataAvailable(scheduler, ScheduledTime.now(), new ScheduledTime("2013-11-22T15:00Z")));

    }

    @Test
    public void testTriggerSubsSomething() throws Exception {
        Trigger dependent = new HDFSCheckTrigger(tempFolder.getRoot().getPath() + "/${year}-${month}-${day}/${hour}00/_READY",  "file:///");

        Trigger offsetTriggerMinus = new OffsetTrigger(-60*60, dependent);
        assertTrue(offsetTriggerMinus.isDataAvailable(scheduler, ScheduledTime.now(), new ScheduledTime("2013-11-22T16:00Z")));

    }


}
