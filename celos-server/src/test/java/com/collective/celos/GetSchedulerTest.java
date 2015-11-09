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
import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.collective.celos.server.CelosServer;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

public class GetSchedulerTest {

    public static final String WORKFLOWS_DIR = "workflows";
    public static final String DEFAULTS_DIR = "defaults";
    public static final String DB_DIR = "db";
    public static final int SLOTS_IN_CELOS_SERVER_SLIDING_WINDOW = SchedulerConfiguration.SLIDING_WINDOW_DAYS * 24;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private File workflowsDir;
    private File slotDbDir;
    private CelosServer celosServer;
    private CelosClient celosClient;

    @Before
    public void setup() throws Exception {
        File tmpDir = folder.newFolder();
        this.workflowsDir = new File(tmpDir, WORKFLOWS_DIR);
        File defaultsDir = new File(tmpDir, DEFAULTS_DIR);
        this.slotDbDir = new File(tmpDir, DB_DIR);
        this.workflowsDir.mkdirs();
        defaultsDir.mkdirs();
        this.slotDbDir.mkdirs();

        this.celosServer = new CelosServer();
        int port = celosServer.startServer(ImmutableMap.<String, String>of(), workflowsDir, defaultsDir, slotDbDir);
        this.celosClient = new CelosClient(URI.create("http://localhost:" + port));
    }

    @After
    public void tearDown() throws Exception {
        celosServer.stopServer();
    }

    @Test
    public void testGetScheduler() throws Exception {
        Set<WorkflowID> workflowIDs = celosClient.getWorkflowList();
        Assert.assertTrue(workflowIDs.isEmpty());

        File src = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/client/wf-list").toURI());
        FileUtils.copyDirectory(src, workflowsDir);

        workflowIDs = celosClient.getWorkflowList();
        Assert.assertTrue(workflowIDs.isEmpty());

        celosClient.clearCache();

        HashSet<WorkflowID> expectedResult = Sets.newHashSet(new WorkflowID("workflow-1"), new WorkflowID("workflow-2"), new WorkflowID("workflow-Iñtërnâtiônàlizætiøn"), new WorkflowID("workflow-4"));
        workflowIDs = celosClient.getWorkflowList();
        Assert.assertEquals(Sets.newHashSet(workflowIDs), expectedResult);
        
        Scheduler scheduler = Util.requireNonNull(celosServer.getScheduler());
        Set<WorkflowID> schedWfIds = scheduler.getWorkflowConfiguration().getWorkflows().stream().map(x -> x.getID()).collect(Collectors.toSet());

        Assert.assertEquals(schedWfIds, expectedResult);
    }

}
