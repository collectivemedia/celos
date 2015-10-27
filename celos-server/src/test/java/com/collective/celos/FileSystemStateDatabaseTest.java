/*
 * Copyright 2015 Collective, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.collective.celos;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.Lists;
import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.joda.time.Instant;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.collect.ImmutableSet;

public class FileSystemStateDatabaseTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test(expected=IOException.class)
    public void directoryMustExist() throws IOException {
        File dir = getDatabaseDir();
        new FileSystemStateDatabase(dir);
    }

    @Test
    public void emptyDatabaseReturnsNull() throws Exception {
        StateDatabase db = new FileSystemStateDatabase(makeDatabaseDir());
        Assert.assertNull(db.getSlotState(new SlotID(new WorkflowID("workflow-1"), new ScheduledTime("2013-12-02T13:37Z"))));
    }

    @Test
    public void testRerunExpiration() throws Exception {
        WorkflowID wf1 = new WorkflowID("foo");
        WorkflowID wf2 = new WorkflowID("bar");
        StateDatabase db = new FileSystemStateDatabase(makeDatabaseDir());
        Assert.assertEquals(new TreeSet<>(), db.getTimesMarkedForRerun(wf1, new ScheduledTime("2013-12-02T15:00Z")));
        Assert.assertEquals(new TreeSet<>(), db.getTimesMarkedForRerun(wf2, new ScheduledTime("2013-12-02T15:00Z")));
        ScheduledTime time1 = new ScheduledTime("2013-12-02T13:00Z");
        ScheduledTime time2 = new ScheduledTime("2013-12-02T14:00Z");
        SlotID wf1slot1 = new SlotID(wf1, time1);
        SlotID wf1slot2 = new SlotID(wf1, time2);
        SlotID wf2slot1 = new SlotID(wf2, time1);
        db.markSlotForRerun(wf1slot1, time1);
        db.markSlotForRerun(wf1slot2, time2);
        db.markSlotForRerun(wf2slot1, time1);
        Assert.assertEquals(new TreeSet<>(ImmutableSet.of(time1, time2)), db.getTimesMarkedForRerun(wf1, new ScheduledTime("2013-12-02T15:00Z")));
        Assert.assertEquals(new TreeSet<>(ImmutableSet.of(time1)), db.getTimesMarkedForRerun(wf2, new ScheduledTime("2013-12-02T15:00Z")));
        // Now call wf1 with much later current time and make sure files got expired after first call
        Assert.assertEquals(new TreeSet<>(ImmutableSet.of(time1, time2)), db.getTimesMarkedForRerun(wf1, new ScheduledTime("2015-12-02T15:00Z")));
        Assert.assertEquals(new TreeSet<>(), db.getTimesMarkedForRerun(wf1, new ScheduledTime("2015-12-02T15:00Z")));
        // wf2 still in there
        Assert.assertEquals(new TreeSet<>(ImmutableSet.of(time1)), db.getTimesMarkedForRerun(wf2, new ScheduledTime("2013-12-02T15:00Z")));
        // Now call wf2 with much later current time and make sure files got expired after first call
        Assert.assertEquals(new TreeSet<>(ImmutableSet.of(time1)), db.getTimesMarkedForRerun(wf2, new ScheduledTime("2015-12-02T15:00Z")));
        Assert.assertEquals(new TreeSet<>(), db.getTimesMarkedForRerun(wf2, new ScheduledTime("2015-12-02T15:00Z")));
    }

    private File makeDatabaseDir() {
        File dir = getDatabaseDir();
        dir.mkdir();
        return dir;
    }

    private File getDatabaseDir() {
        return new File(tempFolder.getRoot(), "db");
    }

    /**
     * Compare slot states returned by getStates against those under src/test/resources.
     */
    @Test
    public void canReadFromFileSystem1() throws Exception {
        StateDatabase db = new FileSystemStateDatabase(getResourceDirNoTimestamp());
        for(SlotState state : getStates()) {
            Assert.assertEquals(state, db.getSlotState(state.getSlotID()));
        }
    }

    /**
     * Compare slot states returned by getStates against those under src/test/resources.
     */
    @Test
    public void canReadFromFileSystem2() throws Exception {
        StateDatabase db = new FileSystemStateDatabase(getResourceDir());
        for(SlotState state : getStates()) {
            Assert.assertEquals(state, db.getSlotState(state.getSlotID()));
        }
    }

    /**
     * Write slot states returned by getStates to file system and diff
     * the temporary dir against the src/test/resources one.
     */
    @Test
    public void canWriteToFileSystem() throws Exception {
        StateDatabase db = new FileSystemStateDatabase(makeDatabaseDir());
        for(SlotState state : getStates()) {
            db.putSlotState(state);
        }
        if (diff(getDatabaseDir(), getResourceDir())) {
            throw new AssertionError("Database differs from resource database.");
        }
    }

    @Test
    public void testPause() throws IOException {
        StateDatabase db = new FileSystemStateDatabase(makeDatabaseDir());
        WorkflowID workflowID = new WorkflowID("wf1");

        File pauseFile = new File(getDatabaseDir(), "paused/wf1");

        Assert.assertFalse(db.isPaused(workflowID));
        Assert.assertFalse(pauseFile.exists());

        db.setPaused(workflowID, true);

        Assert.assertTrue(pauseFile.exists());
        Assert.assertTrue(db.isPaused(workflowID));

        db.setPaused(workflowID, false);
        Assert.assertFalse(db.isPaused(workflowID));
        Assert.assertFalse(pauseFile.exists());
    }

    @Test
    public void testGetSlotStatesForPeriod() throws Exception {
        StateDatabase db = new FileSystemStateDatabase(makeDatabaseDir());
        for(SlotState state : getStatesForSeveralDays()) {
            db.putSlotState(state);
        }
        WorkflowID wf1 = new WorkflowID("workflow-1");
        ScheduledTime time0 = new ScheduledTime("2013-12-02T10:00Z");
        ScheduledTime time1 = new ScheduledTime("2013-12-02T20:00Z");
        ScheduledTime time2 = new ScheduledTime("2013-12-03T00:00Z");
        ScheduledTime time3 = new ScheduledTime("2013-12-03T20:00Z");
        ScheduledTime time4 = new ScheduledTime("2013-12-04T01:00Z");

        List<SlotState> slotStates0 = db.getSlotStates(wf1, time0, time2);
        Assert.assertEquals(slotStates0.size(), 7);
        assertSlotStatesOrder(slotStates0);

        List<SlotState> slotStates1 = db.getSlotStates(wf1, time1, time2);
        Assert.assertEquals(slotStates1.size(), 4);
        assertSlotStatesOrder(slotStates1);

        List<SlotState> slotStates2 = db.getSlotStates(wf1, time1, time3);
        Assert.assertEquals(slotStates2.size(), 24);
        assertSlotStatesOrder(slotStates2);

        List<SlotState> slotStates4 = db.getSlotStates(wf1, time1, time1);
        Assert.assertEquals(slotStates4.size(), 0);

        List<SlotState> slotStates5 = db.getSlotStates(wf1, time1, time1.plusSeconds(1));
        Assert.assertEquals(slotStates5.size(), 1);
        Assert.assertEquals(slotStates5.get(0).getScheduledTime(), time1);

        List<SlotState> slotStates6 = db.getSlotStates(wf1, time4, time4.plusMonths(1));
        Assert.assertEquals(slotStates6.size(), 1);
        Assert.assertEquals(slotStates6.get(0).getScheduledTime(), time4);
    }

    @Test
    public void testGetSlotStatesForParticularTimes() throws Exception {
        StateDatabase db = new FileSystemStateDatabase(makeDatabaseDir());
        for(SlotState state : getStatesForSeveralDays()) {
            db.putSlotState(state);
        }
        WorkflowID wf1 = new WorkflowID("workflow-1");
        List<ScheduledTime> times = Lists.newArrayList(new ScheduledTime("2013-12-02T17:00Z"), new ScheduledTime("2013-12-02T19:00Z"), new ScheduledTime("2013-12-03T00:00Z"));

        List<SlotState> slotStates = db.getSlotStates(wf1, times);

        Assert.assertEquals(slotStates.size(), 3);
        Assert.assertEquals(slotStates.get(0).getStatus(), SlotState.Status.WAITING);
        Assert.assertEquals(slotStates.get(0).getSlotID(), new SlotID(wf1, new ScheduledTime("2013-12-02T17:00Z")));

        Assert.assertEquals(slotStates.get(1).getStatus(), SlotState.Status.RUNNING);
        Assert.assertEquals(slotStates.get(1).getSlotID(), new SlotID(wf1, new ScheduledTime("2013-12-02T19:00Z")));

        Assert.assertEquals(slotStates.get(2).getStatus(), SlotState.Status.SUCCESS);
        Assert.assertEquals(slotStates.get(2).getSlotID(), new SlotID(wf1, new ScheduledTime("2013-12-03T00:00Z")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetSlotStatesForPeriodValidates() throws Exception {
        StateDatabase db = new FileSystemStateDatabase(makeDatabaseDir());
        db.getSlotStates(new WorkflowID("id"), new ScheduledTime("2013-12-03T00:01Z"), new ScheduledTime("2013-12-03T00:00Z"));
    }

    private void assertSlotStatesOrder(List<SlotState> slotStates) {
        SlotState slotState = slotStates.get(0);
        for (int i=1; i < slotStates.size(); i++) {
            SlotState nextSlot = slotStates.get(i);
            Interval interval = new Interval(slotState.getScheduledTime().getDateTime(), nextSlot.getScheduledTime().getDateTime());
            Assert.assertEquals(interval.toDuration().getStandardSeconds(), 60 * 60);
            slotState = nextSlot;
        }
    }


    /**
     * Returns true if diff reports a difference between the two files/dirs.
     */
    private boolean diff(File a, File b) throws Exception {
        Process diff = new ProcessBuilder("diff", "-r", a.getAbsolutePath(), b.getAbsolutePath()).start();
        int exitValue = diff.waitFor();
        if (exitValue != 0) {
            String diffOut = IOUtils.toString(diff.getInputStream(), "UTF-8");
            System.err.print(diffOut);
        }
        return (exitValue != 0);
    }

    /**
     * Returns dir of prepared database in src/test/resources (old format without timestamp)
     */
    private File getResourceDirNoTimestamp() throws URISyntaxException {
        String path = "com/collective/celos/state-database-test/db-1-old";
        return new File(Thread.currentThread().getContextClassLoader().getResource(path).toURI());
    }

    /**
     * Returns dir of prepared database in src/test/resources
     */
    private File getResourceDir() throws URISyntaxException {
        String path = "com/collective/celos/state-database-test/db-1";
        return new File(Thread.currentThread().getContextClassLoader().getResource(path).toURI());
    }

    /**
     * Create a bunch of slot states that mirror those under
     * src/test/resources/com/collective/celos/state-database-test/db-1
     */
    private Set<SlotState> getStates() {
        Set<SlotState> states = new HashSet<SlotState>();
        WorkflowID wf1 = new WorkflowID("workflow-1");
        WorkflowID wf2 = new WorkflowID("workflow-2");

        states.add(new SlotState(new SlotID(wf1, new ScheduledTime("2013-12-02T17:00Z")),
                SlotState.Status.WAITING));
        states.add(new SlotState(new SlotID(wf1, new ScheduledTime("2013-12-02T18:00Z")),
                SlotState.Status.READY, null, 14));
        states.add(new SlotState(new SlotID(wf1, new ScheduledTime("2013-12-02T19:00Z")),
                SlotState.Status.READY).transitionToRunning("foo-bar"));

        states.add(new SlotState(new SlotID(wf2, new ScheduledTime("2013-12-02T17:00Z")),
                SlotState.Status.WAITING));
        states.add(new SlotState(new SlotID(wf2, new ScheduledTime("2013-12-02T18:00Z")),
                SlotState.Status.READY));
        states.add(new SlotState(new SlotID(wf2, new ScheduledTime("2013-12-02T19:00Z")),
                SlotState.Status.READY, null, 2).transitionToRunning("quux"));

        return states;
    }

    private Set<SlotState> getStatesForSeveralDays() {
        Set<SlotState> states = new HashSet<SlotState>();
        WorkflowID wf1 = new WorkflowID("workflow-1");

        states.add(new SlotState(new SlotID(wf1, new ScheduledTime("2013-12-02T17:00Z")),
                SlotState.Status.WAITING));
        states.add(new SlotState(new SlotID(wf1, new ScheduledTime("2013-12-02T18:00Z")),
                SlotState.Status.READY, null, 14));
        states.add(new SlotState(new SlotID(wf1, new ScheduledTime("2013-12-02T19:00Z")),
                SlotState.Status.READY).transitionToRunning("foo-bar"));
        states.add(new SlotState(new SlotID(wf1, new ScheduledTime("2013-12-02T20:00Z")),
                SlotState.Status.SUCCESS));
        states.add(new SlotState(new SlotID(wf1, new ScheduledTime("2013-12-02T21:00Z")),
                SlotState.Status.SUCCESS));
        states.add(new SlotState(new SlotID(wf1, new ScheduledTime("2013-12-02T22:00Z")),
                SlotState.Status.SUCCESS));
        states.add(new SlotState(new SlotID(wf1, new ScheduledTime("2013-12-02T23:00Z")),
                SlotState.Status.SUCCESS));
        states.add(new SlotState(new SlotID(wf1, new ScheduledTime("2013-12-03T00:00Z")),
                SlotState.Status.SUCCESS));
        states.add(new SlotState(new SlotID(wf1, new ScheduledTime("2013-12-03T01:00Z")),
                SlotState.Status.SUCCESS));
        states.add(new SlotState(new SlotID(wf1, new ScheduledTime("2013-12-03T02:00Z")),
                SlotState.Status.SUCCESS));
        states.add(new SlotState(new SlotID(wf1, new ScheduledTime("2013-12-03T03:00Z")),
                SlotState.Status.SUCCESS));
        states.add(new SlotState(new SlotID(wf1, new ScheduledTime("2013-12-03T04:00Z")),
                SlotState.Status.SUCCESS));
        states.add(new SlotState(new SlotID(wf1, new ScheduledTime("2013-12-03T05:00Z")),
                SlotState.Status.SUCCESS));
        states.add(new SlotState(new SlotID(wf1, new ScheduledTime("2013-12-03T06:00Z")),
                SlotState.Status.SUCCESS));
        states.add(new SlotState(new SlotID(wf1, new ScheduledTime("2013-12-03T07:00Z")),
                SlotState.Status.SUCCESS));
        states.add(new SlotState(new SlotID(wf1, new ScheduledTime("2013-12-03T08:00Z")),
                SlotState.Status.SUCCESS));
        states.add(new SlotState(new SlotID(wf1, new ScheduledTime("2013-12-03T09:00Z")),
                SlotState.Status.SUCCESS));
        states.add(new SlotState(new SlotID(wf1, new ScheduledTime("2013-12-03T10:00Z")),
                SlotState.Status.SUCCESS));
        states.add(new SlotState(new SlotID(wf1, new ScheduledTime("2013-12-03T11:00Z")),
                SlotState.Status.SUCCESS));
        states.add(new SlotState(new SlotID(wf1, new ScheduledTime("2013-12-03T12:00Z")),
                SlotState.Status.SUCCESS));
        states.add(new SlotState(new SlotID(wf1, new ScheduledTime("2013-12-03T13:00Z")),
                SlotState.Status.SUCCESS));
        states.add(new SlotState(new SlotID(wf1, new ScheduledTime("2013-12-03T14:00Z")),
                SlotState.Status.SUCCESS));
        states.add(new SlotState(new SlotID(wf1, new ScheduledTime("2013-12-03T15:00Z")),
                SlotState.Status.SUCCESS));
        states.add(new SlotState(new SlotID(wf1, new ScheduledTime("2013-12-03T16:00Z")),
                SlotState.Status.SUCCESS));
        states.add(new SlotState(new SlotID(wf1, new ScheduledTime("2013-12-03T17:00Z")),
                SlotState.Status.SUCCESS));
        states.add(new SlotState(new SlotID(wf1, new ScheduledTime("2013-12-03T18:00Z")),
                SlotState.Status.SUCCESS));
        states.add(new SlotState(new SlotID(wf1, new ScheduledTime("2013-12-03T19:00Z")),
                SlotState.Status.SUCCESS));
        states.add(new SlotState(new SlotID(wf1, new ScheduledTime("2013-12-03T20:00Z")),
                SlotState.Status.SUCCESS));
        states.add(new SlotState(new SlotID(wf1, new ScheduledTime("2013-12-03T21:00Z")),
                SlotState.Status.SUCCESS));
        states.add(new SlotState(new SlotID(wf1, new ScheduledTime("2013-12-03T22:00Z")),
                SlotState.Status.SUCCESS));
        states.add(new SlotState(new SlotID(wf1, new ScheduledTime("2013-12-03T23:00Z")),
                SlotState.Status.SUCCESS));
        states.add(new SlotState(new SlotID(wf1, new ScheduledTime("2013-12-04T00:00Z")),
                SlotState.Status.SUCCESS));
        states.add(new SlotState(new SlotID(wf1, new ScheduledTime("2013-12-04T01:00Z")),
                SlotState.Status.SUCCESS));


        return states;
    }

}
