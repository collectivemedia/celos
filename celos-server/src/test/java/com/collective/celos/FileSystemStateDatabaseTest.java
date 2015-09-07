package com.collective.celos;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
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
    public void emptyDatabaseReturnsEmptyRerunSet() throws Exception {
        StateDatabase db = new FileSystemStateDatabase(makeDatabaseDir());
        Assert.assertEquals(new TreeSet<>(), db.getTimesMarkedForRerun(new WorkflowID("workflow-1"), new ScheduledTime("2013-12-02T13:37Z")));
    }

    @Test
    public void testRerun() throws Exception {
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
    
}
