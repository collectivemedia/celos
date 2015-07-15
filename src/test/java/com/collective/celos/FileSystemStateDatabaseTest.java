package com.collective.celos;

import junit.framework.Assert;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FileSystemStateDatabaseTest {

    StateDatabase defaultDatabase;

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Before
    public void setUp() {
        try {
            File dir = getDatabaseDir();
            Files.createDirectory(dir.toPath());
            File rerun = getRerunDatabaseDir();
            Files.createDirectory(rerun.toPath());
            defaultDatabase = new FileSystemStateDatabase(dir, rerun);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test(expected=AssertionError.class)
    public void directoryStateMustExist() throws IOException {
        File dir = getDatabaseDir();
        File rerun = getRerunDatabaseDir();
        Files.deleteIfExists(rerun.toPath());
        new FileSystemStateDatabase(dir, rerun);
    }

    @Test(expected=AssertionError.class)
    public void directorySetUp() throws IOException {
        File dir = getDatabaseDir();
        File rerun = getRerunDatabaseDir();
        Files.deleteIfExists(dir.toPath());
        new FileSystemStateDatabase(dir, rerun);
    }


    @Test
    public void directoryRerunMustExist() throws IOException {
        Assert.assertTrue(Files.isDirectory(getDatabaseDir().toPath()));
        Assert.assertTrue(Files.isDirectory(getRerunDatabaseDir().toPath()));
    }

    @Test
    public void emptyDatabaseReturnsNull() throws Exception {
        final SlotID slot = new SlotID(new WorkflowID("workflow-1"), new ScheduledTime("2013-12-02T13:37Z"));
        Assert.assertNull(defaultDatabase.getSlotState(slot));
    }

    private File getDatabaseDir() {
        return new File(tempFolder.getRoot(), "db");
    }

    private File getRerunDatabaseDir() {
        return new File(tempFolder.getRoot(), "rerun");
    }

    /**
     * Compare slot states returned by getStates against those under src/test/resources.
     */
    @Test
    public void canReadFromFileSystem1() throws Exception {
        StateDatabase db = new FileSystemStateDatabase(getResourceDirNoTimestamp(), getRerunDatabaseDir());
        for(SlotState state : getStates()) {
            Assert.assertEquals(state, db.getSlotState(state.getSlotID()));
        }
    }

    /**
     * Compare slot states returned by getStates against those under src/test/resources.
     */
    @Test
    public void canReadFromFileSystem2() throws Exception {
        StateDatabase db = new FileSystemStateDatabase(getResourceDir(), getRerunDatabaseDir());
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
        for(SlotState state : getStates()) {
            defaultDatabase.putSlotState(state);
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
        Set<SlotState> states = new HashSet<>();
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

    /**
     * Write slot states returned by getStates to file system and diff
     * the temporary dir against the src/test/resources one.
     */
    @Test
    public void testGetSortedListIDs() throws Exception {
        StateDatabase db = new FileSystemStateDatabase(getRerunResourcesDir(), getRerunDatabaseDir());

        ScheduledTime current = ScheduledTime.now();
        final List<SlotID> initIDs = new ArrayList<>();
        for(SlotID state : getRerunStates()) {
            initIDs.add(state);
            final boolean b = db.updateSlotToRerun(state, current);
            assert b;
        }
        WorkflowID wf1 = new WorkflowID("wf1");
        WorkflowID wf2 = new WorkflowID("wf0");
        final List<SlotID> slotIDs = new ArrayList<>();
        slotIDs.addAll(db.getSlotIDs(wf1, current));
        slotIDs.addAll(db.getSlotIDs(wf2, current));
        org.junit.Assert.assertArrayEquals(initIDs.toArray(), slotIDs.toArray());
    }


    @Test
    public void rerunDbPutStateFormat() throws Exception {
        ScheduledTime current = ScheduledTime.now();
        final String wfString = "workflow-0";
        WorkflowID wf = new WorkflowID(wfString);
        final String days = "2013-12-02";
        final String time = "18:00:00.000Z";
        final SlotID id = new SlotID(wf, new ScheduledTime(days + "T" + time));
        defaultDatabase.putSlotState(new SlotState(id, SlotState.Status.FAILURE));
        updateSlotToRerunAndCheck(id, current);
        final SlotState state = defaultDatabase.getSlotState(id);
        Assert.assertEquals(SlotState.Status.WAITING, state.getStatus());
        final Path wfPath = getRerunDatabaseDir().toPath().resolve(wfString);
        final boolean b = Files.isRegularFile(wfPath.resolve(days).resolve(time));
        Assert.assertTrue(b);
    }


    @Test
    public void emptyRerunDb() throws Exception {
        ScheduledTime current = ScheduledTime.now();
        WorkflowID wf = new WorkflowID("workflow-0");
        final List<SlotID> slotIDs2 = defaultDatabase.getSlotIDs(wf, current);
        org.junit.Assert.assertEquals(0, slotIDs2.size());
    }

    private SlotID makeSlotAndPutToDatabase(WorkflowID wf, ScheduledTime time) throws Exception {
        final SlotID id = new SlotID(wf, time);
        defaultDatabase.putSlotState(new SlotState(id, SlotState.Status.SUCCESS));
        return id;
    }


    private void updateSlotToRerunAndCheck(SlotID id, ScheduledTime current) throws Exception {
        final boolean b = defaultDatabase.updateSlotToRerun(id, current);
        assert b;
    }

    @Test
    public void dbEmptyDirsCleaner() throws Exception {
        final File databaseDir = getRerunDatabaseDir();
        WorkflowID wf = new WorkflowID("workflow-0");
        ScheduledTime gcTime = new ScheduledTime("2013-12-02T15:00Z");
        ScheduledTime current = gcTime.plusDays(RerunState.GC_PERIOD_DAYS);
        final SlotID id1 = makeSlotAndPutToDatabase(wf, gcTime);
        updateSlotToRerunAndCheck(id1, id1.getScheduledTime());
        final SlotID id2 = makeSlotAndPutToDatabase(wf, gcTime.plusDays(1));
        updateSlotToRerunAndCheck(id2, id2.getScheduledTime());
        Path dayDir = databaseDir.toPath().resolve("workflow-0").resolve("2013-12-02");

        org.junit.Assert.assertTrue(Files.isDirectory(dayDir));
        // get all ids
        final List<SlotID> slotIDs1 = defaultDatabase.getSlotIDs(wf, gcTime);
        org.junit.Assert.assertEquals(2, slotIDs1.size());
        // clean db
        final List<SlotID> slotIDs2 = defaultDatabase.getSlotIDs(wf, current);
        org.junit.Assert.assertEquals(1, slotIDs2.size());
        // means that we do not collect empty day dirs
        org.junit.Assert.assertFalse(Files.isDirectory(dayDir));
        // no exception when create dir again
        defaultDatabase.putSlotState(new SlotState(id1, SlotState.Status.FAILURE));
        updateSlotToRerunAndCheck(id1, id1.getScheduledTime());
        org.junit.Assert.assertTrue(Files.isDirectory(dayDir));
    }

    @Test
    public void testCleanGC() throws Exception {
        ScheduledTime current = ScheduledTime.now();
        WorkflowID wf = new WorkflowID("workflow-0");
        ScheduledTime gcTime = current.minusDays(RerunState.GC_PERIOD_DAYS);
        final SlotID id1 = makeSlotAndPutToDatabase(wf, gcTime.minusSeconds(1));
        final SlotID id2 = makeSlotAndPutToDatabase(wf, gcTime); // will be deleted
        final SlotID id3 = makeSlotAndPutToDatabase(wf, gcTime.plusSeconds(1));
        final SlotID id4 = makeSlotAndPutToDatabase(wf, gcTime.plusSeconds(2));
        final SlotID id5 = makeSlotAndPutToDatabase(wf, current);
        // put all 4 ids into db
        updateSlotToRerunAndCheck(id1, id1.getScheduledTime());
        updateSlotToRerunAndCheck(id2, id2.getScheduledTime());
        updateSlotToRerunAndCheck(id3, current);
        defaultDatabase.putSlotState(new SlotState(id3, SlotState.Status.FAILURE));
        updateSlotToRerunAndCheck(id3, current);
        defaultDatabase.putSlotState(new SlotState(id3, SlotState.Status.FAILURE));
        updateSlotToRerunAndCheck(id3, id3.getScheduledTime());
        updateSlotToRerunAndCheck(id4, id4.getScheduledTime());
        updateSlotToRerunAndCheck(id5, id5.getScheduledTime());

        // all ids shouldn't be not collected
        final List<SlotID> slotIDs1 = defaultDatabase.getSlotIDs(wf, gcTime);
        System.out.println("slotIDs =");
        System.out.println(slotIDs1);
        org.junit.Assert.assertEquals(5, slotIDs1.size());
        final List<SlotID> slotIDs2 = defaultDatabase.getSlotIDs(wf, current);
        org.junit.Assert.assertEquals(3, slotIDs2.size());
    }


    /**
     * Returns dir of prepared database in src/test/resources
     */
    private File getRerunResourcesDir() throws URISyntaxException {
        String path = "com/collective/celos/state-database-test/rerun-db";
        return new File(Thread.currentThread().getContextClassLoader().getResource(path).toURI());
    }

    /**
     * Create a bunch of slot states that mirror those under
     * src/test/resources/com/collective/celos/state-database-test/rerun-db/
     */
    private List<SlotID> getRerunStates() {
        List<SlotID> states = new ArrayList<>();
        WorkflowID wf1 = new WorkflowID("wf1");
        WorkflowID wf2 = new WorkflowID("wf0");

        states.add(new SlotID(wf1, new ScheduledTime("2013-12-02T15:00Z")));
        states.add(new SlotID(wf1, new ScheduledTime("2013-12-02T16:00Z")));
        states.add(new SlotID(wf2, new ScheduledTime("2013-12-02T17:00Z")));
        states.add(new SlotID(wf2, new ScheduledTime("2013-12-02T18:00Z")));
        states.add(new SlotID(wf2, new ScheduledTime("2013-12-02T19:00Z")));

        return states;
    }






}
