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

import com.collective.celos.server.CelosServer;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.*;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Set;

/**
 * Created by akonopko on 22.12.14.
 */
public class CelosClientServerTest {

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
    public void testGetWorkflowListEmpty() throws Exception {
        Set<WorkflowID> workflowIDs = celosClient.getWorkflowList();
        Assert.assertTrue(workflowIDs.isEmpty());
    }

    @Test
    public void testGetWorkflowListEmptyBeforeClearCache() throws Exception {
        Set<WorkflowID> workflowIDs = celosClient.getWorkflowList();
        Assert.assertTrue(workflowIDs.isEmpty());

        File src = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/client/wf-list").toURI());
        FileUtils.copyDirectory(src, workflowsDir);

        workflowIDs = celosClient.getWorkflowList();
        Assert.assertTrue(workflowIDs.isEmpty());

        celosClient.clearCache();

        workflowIDs = celosClient.getWorkflowList();
        Assert.assertEquals(
                Sets.newHashSet(workflowIDs),
                Sets.newHashSet(new WorkflowID("workflow-1"), new WorkflowID("workflow-2"), new WorkflowID("workflow-Iñtërnâtiônàlizætiøn"), new WorkflowID("workflow-4"))
        );
    }

    @Test(expected = HttpHostConnectException.class)
    public void testServerStops() throws Exception {
        celosClient.getWorkflowList();
        celosServer.stopServer();
        celosClient.getWorkflowList();
    }

    @Test(expected = IOException.class)
    public void testGetWorkflowStatusFailsNoId() throws Exception {
        celosClient.getWorkflowStatus(new WorkflowID("noid"));
    }

    @Test(expected = IOException.class)
    public void testGetWorkflowStatusFailsNoIdWithSomeData() throws Exception {
        File src = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/client/wf-list").toURI());
        FileUtils.copyDirectory(src, workflowsDir);

        celosClient.getWorkflowStatus(new WorkflowID("noid"));
    }

    @Test
    public void testGetWorkflowStatusWaiting() throws Exception {
        File src = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/client/wf-list").toURI());
        FileUtils.copyDirectory(src, workflowsDir);

        List<SlotState> slotStates = celosClient.getWorkflowStatus(new WorkflowID("workflow-2")).getSlotStates();
        Assert.assertEquals(slotStates.size(), SLOTS_IN_CELOS_SERVER_SLIDING_WINDOW);

        for (SlotState slotState : slotStates) {
            Assert.assertEquals(slotState.getStatus(), SlotState.Status.WAITING);
            Assert.assertEquals(slotState.getExternalID(), null);
            Assert.assertEquals(slotState.getRetryCount(), 0);
        }
    }


    @Test
    public void testGetWorkflowStatusCorrectOrder() throws Exception {
        File src = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/client/wf-list").toURI());
        FileUtils.copyDirectory(src, workflowsDir);

        List<SlotState> slotStates = celosClient.getWorkflowStatus(new WorkflowID("workflow-2")).getSlotStates();
        Assert.assertEquals(slotStates.size(), SLOTS_IN_CELOS_SERVER_SLIDING_WINDOW);

        for (int i = 0; i < slotStates.size() - 1; i++) {
            SlotState slotState = slotStates.get(i);
            SlotState nextSlotState = slotStates.get(i + 1);
            Assert.assertTrue(!slotState.getScheduledTime().getDateTime().isBefore(nextSlotState.getScheduledTime().getDateTime()));
        }
    }

    @Test
    public void testGetWorkflowStatusTransitionToReady() throws Exception {

        File src = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/client/wf-list").toURI());
        FileUtils.copyDirectory(src, workflowsDir);

        celosClient.iterateScheduler();
        List<SlotState> slotStates = celosClient.getWorkflowStatus(new WorkflowID("workflow-2")).getSlotStates();
        Assert.assertEquals(slotStates.size(), SLOTS_IN_CELOS_SERVER_SLIDING_WINDOW);

        for (SlotState slotState : slotStates) {
            Assert.assertEquals(slotState.getStatus(), SlotState.Status.READY);
            Assert.assertEquals(slotState.getExternalID(), null);
            Assert.assertEquals(slotState.getRetryCount(), 0);
        }
    }

    @Test
    public void testGetWorkflowStatusNoInfo() throws Exception {

        File src = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/client/wf-list").toURI());
        FileUtils.copyDirectory(src, workflowsDir);

        celosClient.iterateScheduler();
        WorkflowStatus status = celosClient.getWorkflowStatus(new WorkflowID("workflow-1"));
        Assert.assertNull(status.getInfo().getUrl());
        Assert.assertTrue(status.getInfo().getContacts().isEmpty());
    }

    @Test
    public void testGetWorkflowStatusWithInfo() throws Exception {

        File src = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/client/wf-list").toURI());
        FileUtils.copyDirectory(src, workflowsDir);

        celosClient.iterateScheduler();
        WorkflowStatus status = celosClient.getWorkflowStatus(new WorkflowID("workflow-2"));

        Assert.assertEquals(new URL("http://collective.com"), status.getInfo().getUrl());
        Assert.assertEquals(1, status.getInfo().getContacts().size());
        Assert.assertEquals("John Doe", status.getInfo().getContacts().get(0).getName());
        Assert.assertEquals(URI.create("john.doe@collective.com"), status.getInfo().getContacts().get(0).getEmail());
    }


    @Test
    public void testGetWorkflowStatusTransitionToRunningFailsWrongOozieService() throws Exception {

        File src = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/client/wf-list").toURI());
        FileUtils.copyDirectory(src, workflowsDir);

        celosClient.iterateScheduler();
        celosClient.iterateScheduler();
        List<SlotState> slotStates = celosClient.getWorkflowStatus(new WorkflowID("workflow-1")).getSlotStates();
        Assert.assertEquals(slotStates.size(), SLOTS_IN_CELOS_SERVER_SLIDING_WINDOW);

        for (SlotState slotState : slotStates) {
            Assert.assertEquals(slotState.getStatus(), SlotState.Status.READY);
            Assert.assertNull(slotState.getExternalID());
            Assert.assertEquals(slotState.getRetryCount(), 0);
        }
    }

    @Test
    public void testGetWorkflowStatusTransitionToRunning() throws Exception {

        File src = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/client/wf-list").toURI());
        FileUtils.copyDirectory(src, workflowsDir);

        celosClient.iterateScheduler();
        celosClient.iterateScheduler();
        List<SlotState> slotStates = celosClient.getWorkflowStatus(new WorkflowID("workflow-2")).getSlotStates();
        Assert.assertEquals(slotStates.size(), SLOTS_IN_CELOS_SERVER_SLIDING_WINDOW);

        List<SlotState> slotStatesOther = slotStates.subList(0, slotStates.size() - 1);
        SlotState slotStateLst = slotStates.get(slotStates.size() - 1);
        Assert.assertEquals(slotStateLst.getStatus(), SlotState.Status.RUNNING);
        Assert.assertNotNull(slotStateLst.getExternalID());
        Assert.assertEquals(slotStateLst.getRetryCount(), 0);

        for (SlotState slotState : slotStatesOther) {
            Assert.assertEquals(slotState.getStatus(), SlotState.Status.READY);
            Assert.assertNull(slotState.getExternalID());
            Assert.assertEquals(slotState.getRetryCount(), 0);
        }
    }

    @Test
    public void testGetWorkflowStatusTransitionToSuccess() throws Exception {

        File src = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/client/wf-list").toURI());
        FileUtils.copyDirectory(src, workflowsDir);

        celosClient.iterateScheduler();
        celosClient.iterateScheduler();
        celosClient.iterateScheduler();

        List<SlotState> slotStates = celosClient.getWorkflowStatus(new WorkflowID("workflow-2")).getSlotStates();
        Assert.assertEquals(slotStates.size(), SLOTS_IN_CELOS_SERVER_SLIDING_WINDOW);

        SlotState slotStateLst = slotStates.get(slotStates.size() - 1);
        Assert.assertEquals(slotStateLst.getStatus(), SlotState.Status.SUCCESS);
        Assert.assertNotNull(slotStateLst.getExternalID());
        Assert.assertEquals(slotStateLst.getRetryCount(), 0);

        slotStates = slotStates.subList(0, slotStates.size() - 1);
        for (SlotState slotState : slotStates) {
            Assert.assertEquals(slotState.getStatus(), SlotState.Status.READY);
            Assert.assertNull(slotState.getExternalID());
            Assert.assertEquals(slotState.getRetryCount(), 0);
        }
    }

    @Test
    public void testGetWorkflowStatusTransitionToSuccessRunning() throws Exception {

        File src = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/client/wf-list").toURI());
        FileUtils.copyDirectory(src, workflowsDir);

        celosClient.iterateScheduler();
        celosClient.iterateScheduler();
        celosClient.iterateScheduler();
        celosClient.iterateScheduler();

        List<SlotState> slotStates = celosClient.getWorkflowStatus(new WorkflowID("workflow-2")).getSlotStates();
        Assert.assertEquals(slotStates.size(), SLOTS_IN_CELOS_SERVER_SLIDING_WINDOW);

        SlotState slotStateLst = slotStates.get(slotStates.size() - 1);
        Assert.assertEquals(slotStateLst.getStatus(), SlotState.Status.SUCCESS);
        Assert.assertNotNull(slotStateLst.getExternalID());
        Assert.assertEquals(slotStateLst.getRetryCount(), 0);

        SlotState slotStatePreLst = slotStates.get(slotStates.size() - 2);
        Assert.assertEquals(slotStatePreLst.getStatus(), SlotState.Status.RUNNING);
        Assert.assertNotNull(slotStatePreLst.getExternalID());
        Assert.assertEquals(slotStatePreLst.getRetryCount(), 0);

        slotStates = slotStates.subList(0, slotStates.size() - 2);
        for (SlotState slotState : slotStates) {
            Assert.assertEquals(slotState.getStatus(), SlotState.Status.READY);
            Assert.assertNull(slotState.getExternalID());
            Assert.assertEquals(slotState.getRetryCount(), 0);
        }
    }


    @Test
    public void testGetWorkflowStatusTransitionToSuccessFailure() throws Exception {

        File src = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/client/wf-list").toURI());
        FileUtils.copyDirectory(src, workflowsDir);

        celosClient.iterateScheduler();
        celosClient.iterateScheduler();
        celosClient.iterateScheduler();

        List<SlotState> slotStates = celosClient.getWorkflowStatus(new WorkflowID("workflow-Iñtërnâtiônàlizætiøn")).getSlotStates();
        Assert.assertEquals(slotStates.size(), SLOTS_IN_CELOS_SERVER_SLIDING_WINDOW);

        SlotState slotStateFst = slotStates.get(slotStates.size() - 1);
        Assert.assertEquals(slotStateFst.getStatus(), SlotState.Status.FAILURE);
        Assert.assertNotNull(slotStateFst.getExternalID());
        Assert.assertEquals(slotStateFst.getRetryCount(), 0);
    }

    @Test
    public void testGetWorkflowStatusInPast() throws Exception {

        ScheduledTime timeInPast = new ScheduledTime("2000-12-01T00:00Z");
        File src = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/client/wf-list").toURI());
        FileUtils.copyDirectory(src, workflowsDir);

        List<SlotState> slotStates = celosClient.getWorkflowStatus(new WorkflowID("workflow-4")).getSlotStates();
        Assert.assertEquals(slotStates.size(), SLOTS_IN_CELOS_SERVER_SLIDING_WINDOW);
        Assert.assertTrue(slotStates.get(slotStates.size() - 1).getScheduledTime().getDateTime().isAfter(timeInPast.getDateTime().plusYears(1)));

        slotStates = celosClient.getWorkflowStatus(new WorkflowID("workflow-4"), timeInPast).getSlotStates();
        Assert.assertEquals(slotStates.get(slotStates.size() - 1).getScheduledTime(), timeInPast.minusDays(SchedulerConfiguration.SLIDING_WINDOW_DAYS));
        Assert.assertEquals(slotStates.get(0).getScheduledTime(), timeInPast.minusHours(1));

        slotStates = celosClient.getWorkflowStatus(new WorkflowID("workflow-4"), timeInPast.plusSeconds(1)).getSlotStates();
        Assert.assertEquals(slotStates.get(0).getScheduledTime(), timeInPast);
    }

    @Test
    public void testSchedulerIteratorPast() throws Exception {

        ScheduledTime timeInPast = new ScheduledTime("2000-12-01T00:00Z");
        File src = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/client/wf-list").toURI());
        FileUtils.copyDirectory(src, workflowsDir);

        List<SlotState> slotStates = celosClient.getWorkflowStatus(new WorkflowID("workflow-4")).getSlotStates();
        Assert.assertEquals(slotStates.size(), SLOTS_IN_CELOS_SERVER_SLIDING_WINDOW);

        celosClient.iterateScheduler(timeInPast);

        ScheduledTime inclusivePeriod = timeInPast.plusSeconds(1);
        slotStates = celosClient.getWorkflowStatus(new WorkflowID("workflow-4"), inclusivePeriod).getSlotStates();

        for (SlotState slotState : slotStates) {
            Assert.assertEquals(slotState.getStatus(), SlotState.Status.WAITING);
            Assert.assertEquals(slotState.getExternalID(), null);
            Assert.assertEquals(slotState.getRetryCount(), 0);
        }

        celosClient.iterateScheduler(timeInPast);
        slotStates = celosClient.getWorkflowStatus(new WorkflowID("workflow-4"), inclusivePeriod).getSlotStates();

        for (SlotState slotState : slotStates) {
            Assert.assertEquals(slotState.getStatus(), SlotState.Status.WAITING);
            Assert.assertEquals(slotState.getExternalID(), null);
            Assert.assertEquals(slotState.getRetryCount(), 0);
        }


        celosClient.iterateScheduler(inclusivePeriod);
        slotStates = celosClient.getWorkflowStatus(new WorkflowID("workflow-4"), inclusivePeriod).getSlotStates();
        Assert.assertEquals(slotStates.get(0).getStatus(), SlotState.Status.READY);
        for (SlotState slotState : slotStates.subList(1, slotStates.size())) {
            Assert.assertEquals(slotState.getStatus(), SlotState.Status.WAITING);
            Assert.assertEquals(slotState.getExternalID(), null);
            Assert.assertEquals(slotState.getRetryCount(), 0);
        }
    }

    @Test
    public void testServerRespondsToScheduler() throws Exception {
        celosClient.iterateScheduler();
    }

    @Test
    public void testServerRespondsToSchedulerPOST() throws IOException {
        HttpPost request = new HttpPost(celosClient.getAddress() + "/scheduler");
        HttpResponse response = new DefaultHttpClient().execute(request);
        Assert.assertEquals(response.getStatusLine().getStatusCode(), 200);
    }

    @Test
    public void testSchedulerNoRespondToGet() throws IOException {
        HttpGet request = new HttpGet(celosClient.getAddress() + "/scheduler");
        HttpResponse response = new DefaultHttpClient().execute(request);
        Assert.assertEquals(response.getStatusLine().getStatusCode(), 405);
        Assert.assertEquals(response.getStatusLine().getReasonPhrase(), "HTTP method GET is not supported by this URL");
    }

    @Test
    public void testGetWorkflowFailsWithNoIdParameter() throws Exception {
        File src = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/client/wf-list").toURI());
        FileUtils.copyDirectory(src, workflowsDir);

        HttpGet request = new HttpGet(celosClient.getAddress() + "/workflow");
        HttpResponse response = new DefaultHttpClient().execute(request);
        Assert.assertEquals(response.getStatusLine().getStatusCode(), 400);
        Assert.assertEquals(response.getStatusLine().getReasonPhrase(), "id parameter missing.");
    }

    @Test(expected = IOException.class)
    public void testCantGetStatusFromDBOnly() throws Exception {

        File src2 = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/server/slot-db-1").toURI());
        FileUtils.copyDirectory(src2, slotDbDir);

        celosClient.getWorkflowStatus(new WorkflowID("workflow-1"), new ScheduledTime("2013-12-02T20:00Z"));
    }


    @Test
    public void testCorrectWorkflowStatesFromDbWf1() throws Exception {
        File src = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/client/wf-list").toURI());
        FileUtils.copyDirectory(src, workflowsDir);

        File src2 = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/server/slot-db-1").toURI());
        FileUtils.copyDirectory(src2, slotDbDir);

        ScheduledTime timeStart = new ScheduledTime("2013-11-25T20:00:00.000Z");
        ScheduledTime waitingSlotsTimeEnd = new ScheduledTime("2013-12-02T19:00:00.000Z");

        ScheduledTime time = waitingSlotsTimeEnd;

        List<SlotState> expectedSlotStates = Lists.newArrayList();
        SlotState slotStateRunning = new SlotState(new SlotID(new WorkflowID("workflow-1"), time), SlotState.Status.RUNNING, "foo-bar", 0);
        time = time.minusHours(1);
        SlotState slotStateReady = new SlotState(new SlotID(new WorkflowID("workflow-1"), time), SlotState.Status.READY, null, 14);
        time = time.minusHours(1);

        expectedSlotStates.add(slotStateRunning);
        expectedSlotStates.add(slotStateReady);

        while (!time.getDateTime().isBefore(timeStart.getDateTime())) {
            SlotState slotState = new SlotState(new SlotID(new WorkflowID("workflow-1"), time), SlotState.Status.WAITING);
            expectedSlotStates.add(slotState);
            time = time.minusHours(1);
        }

        List<SlotState> resultSlotStates = celosClient.getWorkflowStatus(new WorkflowID("workflow-1"), new ScheduledTime("2013-12-02T20:00Z")).getSlotStates();

        Assert.assertEquals(expectedSlotStates, resultSlotStates);
    }


    @Test
    public void testCorrectWorkflowStatesFromDbWf1Only10Hours() throws Exception {
        File src = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/client/wf-list").toURI());
        FileUtils.copyDirectory(src, workflowsDir);

        File src2 = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/server/slot-db-1").toURI());
        FileUtils.copyDirectory(src2, slotDbDir);

        ScheduledTime timeStart = new ScheduledTime("2013-12-02T10:00:00.000Z");
        ScheduledTime waitingSlotsTimeEnd = new ScheduledTime("2013-12-02T19:00:00.000Z");

        ScheduledTime time = waitingSlotsTimeEnd;

        List<SlotState> expectedSlotStates = Lists.newArrayList();
        SlotState slotStateRunning = new SlotState(new SlotID(new WorkflowID("workflow-1"), time), SlotState.Status.RUNNING, "foo-bar", 0);
        time = time.minusHours(1);
        SlotState slotStateReady = new SlotState(new SlotID(new WorkflowID("workflow-1"), time), SlotState.Status.READY, null, 14);
        time = time.minusHours(1);

        expectedSlotStates.add(slotStateRunning);
        expectedSlotStates.add(slotStateReady);

        while (!time.getDateTime().isBefore(timeStart.getDateTime())) {
            SlotState slotState = new SlotState(new SlotID(new WorkflowID("workflow-1"), time), SlotState.Status.WAITING);
            expectedSlotStates.add(slotState);
            time = time.minusHours(1);
        }

        ScheduledTime reqTimeEnd = new ScheduledTime("2013-12-02T20:00Z");
        ScheduledTime reqTimeStart = new ScheduledTime("2013-12-02T10:00Z");
        List<SlotState> resultSlotStates = celosClient.getWorkflowStatus(new WorkflowID("workflow-1"), reqTimeStart, reqTimeEnd).getSlotStates();

        Assert.assertEquals(expectedSlotStates, resultSlotStates);
    }

    @Test
    public void testWorksfor5Days() throws Exception {
        File src = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/client/wf-list").toURI());
        FileUtils.copyDirectory(src, workflowsDir);

        ScheduledTime reqTimeEnd = new ScheduledTime("2013-12-07T20:00Z");
        ScheduledTime reqTimeStart = new ScheduledTime("2013-12-02T10:00Z");
        celosClient.getWorkflowStatus(new WorkflowID("workflow-1"), reqTimeStart, reqTimeEnd);
    }

    @Test(expected = IOException.class)
    public void testNotWorksfor25Days() throws Exception {
        File src = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/client/wf-list").toURI());
        FileUtils.copyDirectory(src, workflowsDir);

        ScheduledTime reqTimeEnd = new ScheduledTime("2013-12-27T20:00Z");
        ScheduledTime reqTimeStart = new ScheduledTime("2013-12-02T10:00Z");
        celosClient.getWorkflowStatus(new WorkflowID("workflow-1"), reqTimeStart, reqTimeEnd);
    }


    @Test
    public void testCorrectWorkflowStatesFromDbWf1StartTimeIsAfterEndTime() throws Exception {

        File src = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/client/wf-list").toURI());
        FileUtils.copyDirectory(src, workflowsDir);

        ScheduledTime reqTimeEnd = new ScheduledTime("2013-12-02T20:00Z");
        ScheduledTime reqTimeStart = new ScheduledTime("2013-12-02T21:00Z");
        WorkflowStatus workflowStatus = celosClient.getWorkflowStatus(new WorkflowID("workflow-1"), reqTimeStart, reqTimeEnd);
        Assert.assertEquals(workflowStatus.getSlotStates().size(), 0);
    }


    @Test
    public void testCorrectWorkflowStatesFromDbWf2() throws Exception {
        File src = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/client/wf-list").toURI());
        FileUtils.copyDirectory(src, workflowsDir);

        File src2 = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/server/slot-db-1").toURI());
        FileUtils.copyDirectory(src2, slotDbDir);

        ScheduledTime timeStart = new ScheduledTime("2013-11-25T20:00:00.000Z");
        ScheduledTime waitingSlotsTimeEnd = new ScheduledTime("2013-12-02T19:00:00.000Z");

        ScheduledTime time = waitingSlotsTimeEnd;

        List<SlotState> expectedSlotStates = Lists.newArrayList();
        SlotState slotStateRunning = new SlotState(new SlotID(new WorkflowID("workflow-2"), time), SlotState.Status.RUNNING, "quux", 2);
        time = time.minusHours(1);
        SlotState slotStateReady = new SlotState(new SlotID(new WorkflowID("workflow-2"), time), SlotState.Status.READY);
        time = time.minusHours(1);

        expectedSlotStates.add(slotStateRunning);
        expectedSlotStates.add(slotStateReady);

        while (!time.getDateTime().isBefore(timeStart.getDateTime())) {
            SlotState slotState = new SlotState(new SlotID(new WorkflowID("workflow-2"), time), SlotState.Status.WAITING);
            expectedSlotStates.add(slotState);
            time = time.minusHours(1);
        }

        List<SlotState> resultSlotStates = celosClient.getWorkflowStatus(new WorkflowID("workflow-2"), new ScheduledTime("2013-12-02T20:00Z")).getSlotStates();

        Assert.assertEquals(expectedSlotStates, resultSlotStates);
    }

    @Test
    public void testGetSlotState() throws Exception {

        File src = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/client/wf-list").toURI());
        FileUtils.copyDirectory(src, workflowsDir);

        File src2 = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/server/slot-db-1").toURI());
        FileUtils.copyDirectory(src2, slotDbDir);

        WorkflowID workflowID = new WorkflowID("workflow-1");

        ScheduledTime scheduledTime1 = new ScheduledTime("2013-12-02T19:00Z");
        ScheduledTime scheduledTime2 = new ScheduledTime("2013-12-02T18:00Z");

        SlotState slotState1 = celosClient.getSlotState(workflowID, scheduledTime1);
        SlotState slotState2 = celosClient.getSlotState(workflowID, scheduledTime2);

        SlotState expectedSlotState1 = new SlotState(new SlotID(workflowID, scheduledTime1), SlotState.Status.RUNNING, "foo-bar", 0);
        SlotState expectedSlotState2 = new SlotState(new SlotID(workflowID, scheduledTime2), SlotState.Status.READY, null, 14);

        Assert.assertEquals(expectedSlotState1, slotState1);
        Assert.assertEquals(expectedSlotState2, slotState2);
    }

    @Test(expected = IOException.class)
    public void testGetSlotStateNotFound() throws Exception {

        File src = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/client/wf-list").toURI());
        FileUtils.copyDirectory(src, workflowsDir);

        File src2 = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/server/slot-db-1").toURI());
        FileUtils.copyDirectory(src2, slotDbDir);

        WorkflowID workflowID = new WorkflowID("workflow-1");
        ScheduledTime scheduledTime1 = new ScheduledTime("2000-12-02T19:00Z");

        celosClient.getSlotState(workflowID, scheduledTime1);
    }

    @Test(expected = IOException.class)
    public void testGetSlotStateNotFoundNotExistingWF() throws Exception {

        File src = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/client/wf-list").toURI());
        FileUtils.copyDirectory(src, workflowsDir);

        File src2 = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/server/slot-db-1").toURI());
        FileUtils.copyDirectory(src2, slotDbDir);

        WorkflowID workflowID = new WorkflowID("workflow-doesnt-exist");
        ScheduledTime scheduledTime1 = new ScheduledTime("2000-12-02T19:00Z");

        celosClient.getSlotState(workflowID, scheduledTime1);
    }

    @Test
    public void testRerunSlot() throws Exception {

        File src = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/client/wf-list").toURI());
        FileUtils.copyDirectory(src, workflowsDir);

        celosClient.iterateScheduler();
        celosClient.iterateScheduler();
        celosClient.iterateScheduler();

        WorkflowID workflowID = new WorkflowID("workflow-Iñtërnâtiônàlizætiøn");

        List<SlotState> slotStates = celosClient.getWorkflowStatus(workflowID).getSlotStates();
        Assert.assertEquals(slotStates.size(), SLOTS_IN_CELOS_SERVER_SLIDING_WINDOW);

        SlotState slotStateLst = slotStates.get(slotStates.size() - 1);
        Assert.assertEquals(slotStateLst.getStatus(), SlotState.Status.FAILURE);
        ScheduledTime scheduledTime = slotStateLst.getScheduledTime();

        celosClient.rerunSlot(workflowID, scheduledTime);
        SlotState slotStateUpdated = celosClient.getSlotState(workflowID, scheduledTime);

        Assert.assertEquals(slotStateUpdated.getStatus(), SlotState.Status.WAITING);
    }

    @Test
    public void testRerunSlotWorksNoError() throws Exception {

        File src = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/client/wf-list").toURI());
        FileUtils.copyDirectory(src, workflowsDir);

        WorkflowID workflowID = new WorkflowID("workflow-2");
        celosClient.rerunSlot(workflowID, new ScheduledTime("2013-11-20T12:00Z"));
    }

    @Test(expected = IOException.class)
    public void testRerunSlotFailsNoWorkflow() throws Exception {

        File src = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/client/wf-list").toURI());
        FileUtils.copyDirectory(src, workflowsDir);

        WorkflowID workflowID = new WorkflowID("unknown-workflow");
        celosClient.rerunSlot(workflowID, ScheduledTime.now());
    }

    @Test(expected = IOException.class)
    public void testRerunSlotFailsNoSlot() throws Exception {

        File src = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/client/wf-list").toURI());
        FileUtils.copyDirectory(src, workflowsDir);

        WorkflowID workflowID = new WorkflowID("workflow-2");
        celosClient.rerunSlot(workflowID, new ScheduledTime("2013-11-20T12:34Z"));
    }

    @Test
    public void testRerunTransitive() throws Exception {
        File src = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/rerun-servlet").toURI());
        FileUtils.copyDirectory(src, workflowsDir);

        WorkflowID workflowID = new WorkflowID("workflow-3");

        WorkflowStatus workflowStatus = celosClient.getWorkflowStatus(workflowID);
        List<SlotState> slotStates = workflowStatus.getSlotStates();
        ScheduledTime queryTime = slotStates.get(0).getScheduledTime();
        ScheduledTime slotTime = slotStates.get(slotStates.size() - 1).getScheduledTime();

        celosClient.iterateScheduler(queryTime);
        celosClient.iterateScheduler(queryTime);
        celosClient.iterateScheduler(queryTime);
        celosClient.iterateScheduler(queryTime);
        celosClient.iterateScheduler(queryTime);
        celosClient.iterateScheduler(queryTime);
        celosClient.iterateScheduler(queryTime);
        celosClient.iterateScheduler(queryTime);
        celosClient.iterateScheduler(queryTime);
        celosClient.iterateScheduler(queryTime);
        celosClient.iterateScheduler(queryTime);
        celosClient.iterateScheduler(queryTime);
        celosClient.iterateScheduler(queryTime);
        celosClient.iterateScheduler(queryTime);

        Assert.assertEquals(celosClient.getSlotState(new WorkflowID("workflow-1"), slotTime).getStatus(), SlotState.Status.SUCCESS);
        Assert.assertEquals(celosClient.getSlotState(new WorkflowID("workflow-2"), slotTime).getStatus(), SlotState.Status.SUCCESS);
        Assert.assertEquals(celosClient.getSlotState(workflowID, slotTime).getStatus(), SlotState.Status.SUCCESS);
        Assert.assertEquals(celosClient.getSlotState(new WorkflowID("workflow-4"), slotTime).getStatus(), SlotState.Status.SUCCESS);
        Assert.assertEquals(celosClient.getSlotState(new WorkflowID("workflow-5"), slotTime).getStatus(), SlotState.Status.SUCCESS);

        celosClient.rerunSlot(workflowID, slotTime, false, false);

        Assert.assertEquals(celosClient.getSlotState(new WorkflowID("workflow-1"), slotTime).getStatus(), SlotState.Status.SUCCESS);
        Assert.assertEquals(celosClient.getSlotState(new WorkflowID("workflow-2"), slotTime).getStatus(), SlotState.Status.SUCCESS);
        Assert.assertEquals(celosClient.getSlotState(workflowID, slotTime).getStatus(), SlotState.Status.WAITING);
        Assert.assertEquals(celosClient.getSlotState(new WorkflowID("workflow-4"), slotTime).getStatus(), SlotState.Status.SUCCESS);
        Assert.assertEquals(celosClient.getSlotState(new WorkflowID("workflow-5"), slotTime).getStatus(), SlotState.Status.SUCCESS);

        celosClient.rerunSlot(workflowID, slotTime, true, false);

        Assert.assertEquals(celosClient.getSlotState(new WorkflowID("workflow-1"), slotTime).getStatus(), SlotState.Status.WAITING);
        Assert.assertEquals(celosClient.getSlotState(new WorkflowID("workflow-2"), slotTime).getStatus(), SlotState.Status.WAITING);
        Assert.assertEquals(celosClient.getSlotState(workflowID, slotTime).getStatus(), SlotState.Status.WAITING);
        Assert.assertEquals(celosClient.getSlotState(new WorkflowID("workflow-4"), slotTime).getStatus(), SlotState.Status.SUCCESS);
        Assert.assertEquals(celosClient.getSlotState(new WorkflowID("workflow-5"), slotTime).getStatus(), SlotState.Status.SUCCESS);

        celosClient.rerunSlot(workflowID, slotTime, false, true);

        Assert.assertEquals(celosClient.getSlotState(new WorkflowID("workflow-1"), slotTime).getStatus(), SlotState.Status.WAITING);
        Assert.assertEquals(celosClient.getSlotState(new WorkflowID("workflow-2"), slotTime).getStatus(), SlotState.Status.WAITING);
        Assert.assertEquals(celosClient.getSlotState(workflowID, slotTime).getStatus(), SlotState.Status.WAITING);
        Assert.assertEquals(celosClient.getSlotState(new WorkflowID("workflow-4"), slotTime).getStatus(), SlotState.Status.WAITING);
        Assert.assertEquals(celosClient.getSlotState(new WorkflowID("workflow-5"), slotTime).getStatus(), SlotState.Status.WAITING);

    }

    @Test
    public void testPauseWorkflow() throws Exception {
        File src = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/client/wf-list").toURI());
        FileUtils.copyDirectory(src, workflowsDir);

        WorkflowID workflowID = new WorkflowID("workflow-2");

        WorkflowStatus workflowStatus = celosClient.getWorkflowStatus(workflowID);
        List<SlotState> slotStates = workflowStatus.getSlotStates();
        SlotState slotStateLast = slotStates.get(slotStates.size() - 1);
        Assert.assertEquals(slotStateLast.getStatus(), SlotState.Status.WAITING);
        Assert.assertFalse(workflowStatus.isPaused());

        celosClient.setWorkflowPaused(workflowID, true);

        celosClient.iterateScheduler();
        celosClient.iterateScheduler();

        WorkflowStatus workflowStatus2 = celosClient.getWorkflowStatus(workflowID);
        List<SlotState> slotStates2 = workflowStatus2.getSlotStates();
        SlotState slotStateLast2 = workflowStatus2.getSlotStates().get(slotStates2.size() - 1);
        Assert.assertEquals(slotStateLast2.getStatus(), SlotState.Status.WAITING);
        Assert.assertTrue(workflowStatus2.isPaused());

        celosClient.setWorkflowPaused(workflowID, false);

        celosClient.iterateScheduler();
        celosClient.iterateScheduler();

        WorkflowStatus workflowStatus3 = celosClient.getWorkflowStatus(workflowID);
        List<SlotState> slotStates3 = workflowStatus3.getSlotStates();
        SlotState slotStateLast3 = workflowStatus3.getSlotStates().get(slotStates3.size() - 1);
        Assert.assertEquals(slotStateLast3.getStatus(), SlotState.Status.RUNNING);
        Assert.assertFalse(workflowStatus3.isPaused());
    }

    @Test
    public void testKill() throws Exception {
        File src = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/client/wf-list").toURI());
        FileUtils.copyDirectory(src, workflowsDir);

        ScheduledTime endTime = ScheduledTime.now();

        WorkflowID workflowID = new WorkflowID("workflow-2");

        WorkflowStatus workflowStatus = celosClient.getWorkflowStatus(workflowID, endTime);
        List<SlotState> slotStates = workflowStatus.getSlotStates();
        SlotState slotStateLast = workflowStatus.getSlotStates().get(slotStates.size() - 1);
        Assert.assertEquals(slotStateLast.getStatus(), SlotState.Status.WAITING);

        celosClient.kill(workflowID, slotStateLast.getScheduledTime());

        WorkflowStatus workflowStatus2 = celosClient.getWorkflowStatus(workflowID, endTime);
        List<SlotState> slotStates2 = workflowStatus2.getSlotStates();
        SlotState slotStateLast2 = workflowStatus2.getSlotStates().get(slotStates2.size() - 1);
        Assert.assertEquals(slotStateLast2.getStatus(), SlotState.Status.KILLED);

        celosClient.rerunSlot(workflowID, slotStateLast.getScheduledTime());

        celosClient.iterateScheduler();
        celosClient.iterateScheduler();

        WorkflowStatus workflowStatus3 = celosClient.getWorkflowStatus(workflowID, endTime);
        List<SlotState> slotStates3 = workflowStatus3.getSlotStates();
        SlotState slotStateLast3 = workflowStatus3.getSlotStates().get(slotStates3.size() - 1);
        Assert.assertEquals(slotStateLast3.getStatus(), SlotState.Status.RUNNING);

        celosClient.kill(workflowID, slotStateLast3.getScheduledTime());

        WorkflowStatus workflowStatus4 = celosClient.getWorkflowStatus(workflowID, endTime);
        List<SlotState> slotStates4 = workflowStatus4.getSlotStates();
        SlotState slotStateLast4 = workflowStatus4.getSlotStates().get(slotStates4.size() - 1);
        Assert.assertEquals(slotStateLast4.getStatus(), SlotState.Status.KILLED);
    }


    @Test(expected = IOException.class)
    public void testDoesntKillSuccess() throws Exception {
        File src = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/client/wf-list").toURI());
        FileUtils.copyDirectory(src, workflowsDir);

        ScheduledTime endTime = ScheduledTime.now();

        WorkflowID workflowID = new WorkflowID("workflow-2");

        celosClient.iterateScheduler();
        celosClient.iterateScheduler();
        celosClient.iterateScheduler();

        WorkflowStatus workflowStatus = celosClient.getWorkflowStatus(workflowID, endTime);
        List<SlotState> slotStates = workflowStatus.getSlotStates();
        SlotState slotStateLast = workflowStatus.getSlotStates().get(slotStates.size() - 1);
        Assert.assertEquals(slotStateLast.getStatus(), SlotState.Status.SUCCESS);

        celosClient.kill(workflowID, slotStateLast.getScheduledTime());
    }

}
