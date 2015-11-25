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
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.collective.celos.server.CelosServer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Created by akonopko on 22.12.14.
 */
public class BackupServletTest {

    public static final String WORKFLOWS_DIR = "workflows";
    public static final String DEFAULTS_DIR = "defaults";
    public static final String DB_DIR = "db";
    public static final int SLOTS_IN_CELOS_SERVER_SLIDING_WINDOW = SchedulerConfiguration.SLIDING_WINDOW_DAYS * 24;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private File workflowsDir1;
    private File slotDbDir1;
    private CelosServer celosServer1;
    private CelosClient celosClient1;

    private File workflowsDir2;
    private File slotDbDir2;
    private CelosServer celosServer2;
    private CelosClient celosClient2;

    @Before
    public void setup() throws Exception {
        File tmpDir1 = folder.newFolder();
        this.workflowsDir1 = new File(tmpDir1, WORKFLOWS_DIR);
        File defaultsDir = new File(tmpDir1, DEFAULTS_DIR);
        this.slotDbDir1 = new File(tmpDir1, DB_DIR);
        this.workflowsDir1.mkdirs();
        defaultsDir.mkdirs();
        this.slotDbDir1.mkdirs();
        this.celosServer1 = new CelosServer();
        int port = celosServer1.startServer(ImmutableMap.<String, String>of(), workflowsDir1, defaultsDir, slotDbDir1);
        this.celosClient1 = new CelosClient(URI.create("http://localhost:" + port));

        File tmpDir2 = folder.newFolder();
        this.workflowsDir2 = new File(tmpDir2, WORKFLOWS_DIR);
        File defaultsDir2 = new File(tmpDir2, DEFAULTS_DIR);
        this.slotDbDir2 = new File(tmpDir2, DB_DIR);
        this.workflowsDir2.mkdirs();
        defaultsDir2.mkdirs();
        this.slotDbDir2.mkdirs();
        this.celosServer2 = new CelosServer();
        int port2 = celosServer2.startServer(ImmutableMap.<String, String>of(), workflowsDir2, defaultsDir2, slotDbDir2);
        this.celosClient2 = new CelosClient(URI.create("http://localhost:" + port2));
    }

    @After
    public void tearDown() throws Exception {
        celosServer1.stopServer();
        celosServer2.stopServer();
    }

    @Test
    public void testBackupServlet() throws Exception {

        File srcWorkflow = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/client/wf-list/workflow-1.js").toURI());
        FileUtils.copyFileToDirectory(srcWorkflow, workflowsDir1);
        FileUtils.copyFileToDirectory(srcWorkflow, workflowsDir2);

        File srcSlots = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/server/slot-db-1/state/workflow-1/2013-12-02").toURI());
        ScheduledTimeFormatter formatter = new ScheduledTimeFormatter();
        ScheduledTime yesterday = ScheduledTime.now().minusDays(1);
        String dateStr = formatter.formatDatestamp(yesterday);

        String workflowId = "workflow-1";

        File destDir = new File(slotDbDir1, "state/" + workflowId + "/" + dateStr);
        FileUtils.copyDirectory(srcSlots, destDir);

        WorkflowID workflowID = new WorkflowID(workflowId);

        ScheduledTime scheduledTime1 = new ScheduledTime(dateStr + "T19:00Z");
        ScheduledTime scheduledTime2 = new ScheduledTime(dateStr + "T18:00Z");

        SlotState existsSlot1 = new SlotState(new SlotID(workflowID, scheduledTime1), SlotState.Status.RUNNING, "foo-bar", 0);
        SlotState existsSlot2 = new SlotState(new SlotID(workflowID, scheduledTime2), SlotState.Status.READY, null, 14);
        celosClient1.setWorkflowPaused(workflowID, true);

        Assert.assertEquals(existsSlot1, celosClient1.getSlotState(workflowID, scheduledTime1));
        Assert.assertEquals(existsSlot2, celosClient1.getSlotState(workflowID, scheduledTime2));
        Assert.assertTrue(celosClient1.getWorkflowStatus(workflowID).isPaused());

        boolean fails = false;
        try {
            Assert.assertEquals(existsSlot1, celosClient2.getSlotState(workflowID, scheduledTime1));
        } catch (IOException e) {
            fails = true;
        }
        Assert.assertTrue(fails);

        Assert.assertFalse(celosClient2.getWorkflowStatus(workflowID).isPaused());

        DefaultHttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(celosClient1.getAddress().toString() + "/backup?start=" + yesterday.minusDays(7));
        HttpResponse responseGet = client.execute(httpGet);

        HttpPost httpPost = new HttpPost(celosClient2.getAddress().toString() + "/backup");
        httpPost.setEntity(new StringEntity(IOUtils.toString(responseGet.getEntity().getContent())));
        client.execute(httpPost);

        Assert.assertEquals(existsSlot1, celosClient2.getSlotState(workflowID, scheduledTime1));
        Assert.assertEquals(existsSlot2, celosClient2.getSlotState(workflowID, scheduledTime2));
        Assert.assertTrue(celosClient2.getWorkflowStatus(workflowID).isPaused());

    }

}
