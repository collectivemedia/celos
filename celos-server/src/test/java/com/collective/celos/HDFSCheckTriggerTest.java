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

import com.collective.celos.database.StateDatabaseConnection;
import com.collective.celos.trigger.HDFSCheckTrigger;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class HDFSCheckTriggerTest {

    private StateDatabaseConnection connection = mock(StateDatabaseConnection.class);

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    
    @Test(expected = NullPointerException.class)
    public void testNeedsFs() throws Exception {
        new HDFSCheckTrigger("/foo", null);
    }
    
    @Test(expected = NullPointerException.class)
    public void testNeedsPath() throws Exception {
        new HDFSCheckTrigger(null, "hdfs://foo");
    }
    
    @Test(expected = NullPointerException.class)
    public void testNeedsFsAndPath() throws Exception {
        new HDFSCheckTrigger(null, null);
    }

    @Test
    public void testDirectoryExists() throws Exception {
        assertTrue(new HDFSCheckTrigger("/tmp", "file:///").isDataAvailable(connection, ScheduledTime.now(), new ScheduledTime("2013-11-22T15:00Z")));
    }

    @Test
    public void testDirectoryDoesNotExist() throws Exception {
        assertFalse(new HDFSCheckTrigger("/tmp-does-not-exist", "file:///").isDataAvailable(connection, ScheduledTime.now(), new ScheduledTime("2013-11-22T15:00Z")));
    }

    @Test
    public void testFileExists() throws Exception {
        String root = tempFolder.getRoot().getPath();
        
        File triggerFile = new File(root, "2013-11-22/1500/_READY");
        triggerFile.getParentFile().mkdirs();
        triggerFile.createNewFile();

        assertTrue(new HDFSCheckTrigger(root + "/${year}-${month}-${day}/${hour}00/_READY",  "file:///").isDataAvailable(connection, ScheduledTime.now(), new ScheduledTime("2013-11-22T15:00Z")));
    }

    @Test
    public void testFileDoesNotExist() throws Exception {
        String root = tempFolder.getRoot().getPath();
        
        File triggerFile = new File(root, "2013-11-22/1500/_READY");
        if (triggerFile.exists()) {
            triggerFile.delete(); // Make sure it doesn't exist
        }

        assertFalse(new HDFSCheckTrigger(root + "/${year}-${month}-${day}/${hour}00/_READY", "file:///").isDataAvailable(connection, ScheduledTime.now(), new ScheduledTime("2013-11-22T15:00Z")));
    }
    
    /*
     * I was expecting to see an UnknownHostException here, but to my surprise
     * org.apache.hadoop.security.SecurityUtil ends up wrapping it in an
     * IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testIOExceptionIsPropagated() throws Exception {
        new HDFSCheckTrigger("/some/path", "hdfs://no-such-host").isDataAvailable(connection, ScheduledTime.now(), new ScheduledTime("2013-11-22T15:00Z"));
    }

}
