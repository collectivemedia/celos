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

import junit.framework.Assert;
import org.apache.commons.io.IOUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

public class FileSystemStateDatabaseConnectionTest extends AbstractStateDatabaseTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test(expected=IOException.class)
    public void directoryMustExist() throws IOException {
        File dir = getDatabaseDir();
        new FileSystemStateDatabase(dir);
    }

    @Test
    public void emptyDatabaseReturnsNull() throws Exception {
        StateDatabaseConnection db = getStateDatabase();
        Assert.assertNull(db.getSlotState(new SlotID(new WorkflowID("workflow-1"), new ScheduledTime("2013-12-02T13:37Z"))));
    }

    @Override
    public StateDatabaseConnection getStateDatabase() throws IOException {
        return new FileSystemStateDatabase(makeDatabaseDir()).openConnection();
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
        StateDatabaseConnection db = new FileSystemStateDatabase(getResourceDirNoTimestamp()).openConnection();
        for(SlotState state : getStates()) {
            Assert.assertEquals(state, db.getSlotState(state.getSlotID()));
        }
    }

    /**
     * Compare slot states returned by getStates against those under src/test/resources.
     */
    @Test
    public void canReadFromFileSystem2() throws Exception {
        StateDatabaseConnection db = new FileSystemStateDatabase(getResourceDir()).openConnection();
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
        StateDatabaseConnection db = getStateDatabase();
        for(SlotState state : getStates()) {
            db.putSlotState(state);
        }
        if (diff(getDatabaseDir(), getResourceDir())) {
            throw new AssertionError("Database differs from resource database.");
        }
    }

    @Test
    public void testPauseFileExistence() throws IOException {
        StateDatabaseConnection db = getStateDatabase();
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
