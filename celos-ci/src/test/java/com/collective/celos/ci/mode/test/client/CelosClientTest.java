package com.collective.celos.ci.mode.test.client;

import com.collective.celos.*;
import com.collective.celos.server.CelosServer;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import org.apache.http.conn.HttpHostConnectException;
import org.junit.*;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Set;

/**
 * Created by akonopko on 22.12.14.
 */
public class CelosClientTest {

    public static final String WORKFLOWS_DIR = "workflows";
    public static final String DEFAULTS_DIR = "defaults";
    public static final String DB_DIR = "db";
    public static final int SLOTS_IN_CELOS_SERVER_SLIDING_WINDOW = SchedulerConfiguration.SLIDING_WINDOW_DAYS * 24;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private File workflowsDir;
    private File defaultsDir;
    private File dbDir;
    private CelosServer celosServer;

    @Before
    public void setup() throws IOException {
        File tmpDir = folder.newFolder();
        workflowsDir = new File(tmpDir, WORKFLOWS_DIR);
        defaultsDir = new File(tmpDir, DEFAULTS_DIR);
        dbDir = new File(tmpDir, DB_DIR);
        celosServer = new CelosServer();

        workflowsDir.mkdirs();
        defaultsDir.mkdirs();
        dbDir.mkdirs();
    }

    @After
    public void tearDown() throws Exception {
        celosServer.stopServer();
    }

    @Test(expected = HttpHostConnectException.class)
    public void testServerStops() throws Exception {
        CelosServer celosServer = new CelosServer();
        Integer port = celosServer.startServer(ImmutableMap.<String, String>of(), workflowsDir, defaultsDir, dbDir);

        CelosClient celosClient = new CelosClient("http://localhost:" + port);
        celosClient.getWorkflowList();
        celosServer.stopServer();
        
        celosClient.getWorkflowList();
    }


    @Test
    public void testGetWorkflowListEmpty() throws Exception {
        CelosServer celosServer = new CelosServer();
        Integer port = celosServer.startServer(ImmutableMap.<String, String>of(), workflowsDir, defaultsDir, dbDir);

        CelosClient celosClient = new CelosClient("http://localhost:" + port);
        Set<WorkflowID> workflowIDs = celosClient.getWorkflowList();
        Assert.assertTrue(workflowIDs.isEmpty());

        
    }

    @Test
    public void testGetWorkflowList() throws Exception {

        File src = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/client/wf-list").toURI());

        copyToWorkflowsDir(src);

        CelosServer celosServer = new CelosServer();
        Integer port = celosServer.startServer(ImmutableMap.<String, String>of(), workflowsDir, defaultsDir, dbDir);

        CelosClient celosClient = new CelosClient("http://localhost:" + port);

        Set<WorkflowID> workflowIDs = celosClient.getWorkflowList();
        Assert.assertEquals(
                Sets.newHashSet(workflowIDs),
                Sets.newHashSet(new WorkflowID("workflow-1"), new WorkflowID("workflow-2"), new WorkflowID("workflow-3"), new WorkflowID("workflow-4"))
        );
    }

    private void copyToWorkflowsDir(File src) throws IOException {
        workflowsDir.mkdirs();

        for (File chld : src.listFiles()) {
            Files.copy(chld.toPath(), new File(workflowsDir, chld.getName()).toPath());
        }
    }

    @Test(expected = IOException.class)
    public void testGetWorkflowStatusFailsNoId() throws Exception {
        CelosServer celosServer = new CelosServer();
        Integer port = celosServer.startServer(ImmutableMap.<String, String>of(), workflowsDir, defaultsDir, dbDir);

        CelosClient celosClient = new CelosClient("http://localhost:" + port);
        List<SlotState> workflowIDs = celosClient.getWorkflowStatus(new WorkflowID("noid"));
        Assert.assertTrue(workflowIDs.isEmpty());

        
    }

    @Test
    public void testGetWorkflowStatusWaiting() throws Exception {
        CelosServer celosServer = new CelosServer();

        File src = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/client/wf-list").toURI());

        copyToWorkflowsDir(src);

        Integer port = celosServer.startServer(ImmutableMap.<String, String>of(), workflowsDir, defaultsDir, dbDir);

        CelosClient celosClient = new CelosClient("http://localhost:" + port);
        List<SlotState> slotStates = celosClient.getWorkflowStatus(new WorkflowID("workflow-2"));
        Assert.assertEquals(slotStates.size(), SLOTS_IN_CELOS_SERVER_SLIDING_WINDOW);

        for (SlotState slotState : slotStates) {
            Assert.assertEquals(slotState.getStatus(), SlotState.Status.WAITING);
            Assert.assertEquals(slotState.getExternalID(), null);
            Assert.assertEquals(slotState.getRetryCount(), 0);
        }

        
    }

    @Test
    public void testGetWorkflowStatusTransitionToReady() throws Exception {
        CelosServer celosServer = new CelosServer();

        File src = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/client/wf-list").toURI());

        copyToWorkflowsDir(src);

        Integer port = celosServer.startServer(ImmutableMap.<String, String>of(), workflowsDir, defaultsDir, dbDir);

        CelosClient celosClient = new CelosClient("http://localhost:" + port);
        celosClient.iterateScheduler(ScheduledTime.now());
        List<SlotState> slotStates = celosClient.getWorkflowStatus(new WorkflowID("workflow-2"));
        Assert.assertEquals(slotStates.size(), SLOTS_IN_CELOS_SERVER_SLIDING_WINDOW);

        for (SlotState slotState : slotStates) {
            Assert.assertEquals(slotState.getStatus(), SlotState.Status.READY);
            Assert.assertEquals(slotState.getExternalID(), null);
            Assert.assertEquals(slotState.getRetryCount(), 0);
        }

        
    }

    @Test
    public void testGetWorkflowStatusTransitionToRunningFailsWrongOozieService() throws Exception {
        CelosServer celosServer = new CelosServer();

        File src = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/client/wf-list").toURI());

        copyToWorkflowsDir(src);

        Integer port = celosServer.startServer(ImmutableMap.<String, String>of(), workflowsDir, defaultsDir, dbDir);

        CelosClient celosClient = new CelosClient("http://localhost:" + port);
        celosClient.iterateScheduler(ScheduledTime.now());
        celosClient.iterateScheduler(ScheduledTime.now());
        List<SlotState> slotStates = celosClient.getWorkflowStatus(new WorkflowID("workflow-1"));
        Assert.assertEquals(slotStates.size(), SLOTS_IN_CELOS_SERVER_SLIDING_WINDOW);

        for (SlotState slotState : slotStates) {
            Assert.assertEquals(slotState.getStatus(), SlotState.Status.READY);
            Assert.assertNull(slotState.getExternalID());
            Assert.assertEquals(slotState.getRetryCount(), 0);
        }

        
    }

    @Test
    public void testGetWorkflowStatusTransitionToRunning() throws Exception {
        CelosServer celosServer = new CelosServer();

        File src = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/client/wf-list").toURI());

        copyToWorkflowsDir(src);

        Integer port = celosServer.startServer(ImmutableMap.<String, String>of(), workflowsDir, defaultsDir, dbDir);

        CelosClient celosClient = new CelosClient("http://localhost:" + port);
        celosClient.iterateScheduler(ScheduledTime.now());
        celosClient.iterateScheduler(ScheduledTime.now());
        List<SlotState> slotStates = celosClient.getWorkflowStatus(new WorkflowID("workflow-2"));
        Assert.assertEquals(slotStates.size(), SLOTS_IN_CELOS_SERVER_SLIDING_WINDOW);

        List<SlotState> slotStatesOther = slotStates.subList(1, slotStates.size() - 1);
        SlotState slotStateFst = slotStates.get(0);
        Assert.assertEquals(slotStateFst.getStatus(), SlotState.Status.RUNNING);
        Assert.assertNotNull(slotStateFst.getExternalID());
        Assert.assertEquals(slotStateFst.getRetryCount(), 0);

        for (SlotState slotState : slotStatesOther) {
            Assert.assertEquals(slotState.getStatus(), SlotState.Status.READY);
            Assert.assertNull(slotState.getExternalID());
            Assert.assertEquals(slotState.getRetryCount(), 0);
        }

        
    }

    @Test
    public void testGetWorkflowStatusTransitionToSuccess() throws Exception {
        

        File src = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/client/wf-list").toURI());

        copyToWorkflowsDir(src);

        Integer port = celosServer.startServer(ImmutableMap.<String, String>of(), workflowsDir, defaultsDir, dbDir);

        CelosClient celosClient = new CelosClient("http://localhost:" + port);
        celosClient.iterateScheduler(ScheduledTime.now());
        celosClient.iterateScheduler(ScheduledTime.now());
        celosClient.iterateScheduler(ScheduledTime.now());

        List<SlotState> slotStates = celosClient.getWorkflowStatus(new WorkflowID("workflow-2"));
        Assert.assertEquals(slotStates.size(), SLOTS_IN_CELOS_SERVER_SLIDING_WINDOW);

        SlotState slotStateFst = slotStates.get(0);
        Assert.assertEquals(slotStateFst.getStatus(), SlotState.Status.SUCCESS);
        Assert.assertNotNull(slotStateFst.getExternalID());
        Assert.assertEquals(slotStateFst.getRetryCount(), 0);

        slotStates = slotStates.subList(1, slotStates.size() - 1);
        for (SlotState slotState : slotStates) {
            Assert.assertEquals(slotState.getStatus(), SlotState.Status.READY);
            Assert.assertNull(slotState.getExternalID());
            Assert.assertEquals(slotState.getRetryCount(), 0);
        }

        
    }

    @Test
    public void testGetWorkflowStatusTransitionToSuccessRunning() throws Exception {
        CelosServer celosServer = new CelosServer();

        File src = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/client/wf-list").toURI());

        copyToWorkflowsDir(src);

        Integer port = celosServer.startServer(ImmutableMap.<String, String>of(), workflowsDir, defaultsDir, dbDir);

        CelosClient celosClient = new CelosClient("http://localhost:" + port);
        celosClient.iterateScheduler(ScheduledTime.now());
        celosClient.iterateScheduler(ScheduledTime.now());
        celosClient.iterateScheduler(ScheduledTime.now());
        celosClient.iterateScheduler(ScheduledTime.now());

        List<SlotState> slotStates = celosClient.getWorkflowStatus(new WorkflowID("workflow-2"));
        Assert.assertEquals(slotStates.size(), SLOTS_IN_CELOS_SERVER_SLIDING_WINDOW);

        SlotState slotStateFst = slotStates.get(0);
        Assert.assertEquals(slotStateFst.getStatus(), SlotState.Status.SUCCESS);
        Assert.assertNotNull(slotStateFst.getExternalID());
        Assert.assertEquals(slotStateFst.getRetryCount(), 0);

        SlotState slotStateSnd = slotStates.get(1);
        Assert.assertEquals(slotStateSnd.getStatus(), SlotState.Status.RUNNING);
        Assert.assertNotNull(slotStateSnd.getExternalID());
        Assert.assertEquals(slotStateSnd.getRetryCount(), 0);

        slotStates = slotStates.subList(2, slotStates.size() - 1);
        for (SlotState slotState : slotStates) {
            Assert.assertEquals(slotState.getStatus(), SlotState.Status.READY);
            Assert.assertNull(slotState.getExternalID());
            Assert.assertEquals(slotState.getRetryCount(), 0);
        }

        
    }


    @Test
    public void testGetWorkflowStatusTransitionToSuccessFailure() throws Exception {
        CelosServer celosServer = new CelosServer();

        File src = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/client/wf-list").toURI());

        copyToWorkflowsDir(src);

        Integer port = celosServer.startServer(ImmutableMap.<String, String>of(), workflowsDir, defaultsDir, dbDir);

        CelosClient celosClient = new CelosClient("http://localhost:" + port);
        celosClient.iterateScheduler(ScheduledTime.now());
        celosClient.iterateScheduler(ScheduledTime.now());
        celosClient.iterateScheduler(ScheduledTime.now());

        List<SlotState> slotStates = celosClient.getWorkflowStatus(new WorkflowID("workflow-3"));
        Assert.assertEquals(slotStates.size(), SLOTS_IN_CELOS_SERVER_SLIDING_WINDOW);

        SlotState slotStateFst = slotStates.get(0);
        Assert.assertEquals(slotStateFst.getStatus(), SlotState.Status.FAILURE);
        Assert.assertNotNull(slotStateFst.getExternalID());
        Assert.assertEquals(slotStateFst.getRetryCount(), 0);

        
    }

    @Test
    public void testGetWorkflowStatusInPast() throws Exception {
        CelosServer celosServer = new CelosServer();

        ScheduledTime timeInPast = new ScheduledTime("2000-12-01T00:00Z");

        File src = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/client/wf-list").toURI());

        copyToWorkflowsDir(src);

        Integer port = celosServer.startServer(ImmutableMap.<String, String>of(), workflowsDir, defaultsDir, dbDir);

        CelosClient celosClient = new CelosClient("http://localhost:" + port);
        List<SlotState> slotStates = celosClient.getWorkflowStatus(new WorkflowID("workflow-4"));
        Assert.assertEquals(slotStates.size(), SLOTS_IN_CELOS_SERVER_SLIDING_WINDOW);

        Assert.assertTrue(slotStates.get(0).getScheduledTime().getDateTime().isAfter(timeInPast.getDateTime().plusYears(1)));

        slotStates = celosClient.getWorkflowStatus(new WorkflowID("workflow-4"), timeInPast);
        Assert.assertEquals(slotStates.get(0).getScheduledTime(), timeInPast.minusDays(SchedulerConfiguration.SLIDING_WINDOW_DAYS));
        Assert.assertEquals(slotStates.get(slotStates.size() - 1).getScheduledTime(), timeInPast.minusHours(1));

        slotStates = celosClient.getWorkflowStatus(new WorkflowID("workflow-4"), timeInPast.plusSeconds(1));
        Assert.assertEquals(slotStates.get(slotStates.size() - 1).getScheduledTime(), timeInPast);

    }

    @Test
    public void testSchedulerIteratorPast() throws Exception {
        CelosServer celosServer = new CelosServer();

        ScheduledTime timeInPast = new ScheduledTime("2000-12-01T00:00Z");

        File src = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/client/wf-list").toURI());

        copyToWorkflowsDir(src);

        Integer port = celosServer.startServer(ImmutableMap.<String, String>of(), workflowsDir, defaultsDir, dbDir);

        CelosClient celosClient = new CelosClient("http://localhost:" + port);
        List<SlotState> slotStates = celosClient.getWorkflowStatus(new WorkflowID("workflow-4"));
        Assert.assertEquals(slotStates.size(), SLOTS_IN_CELOS_SERVER_SLIDING_WINDOW);

        celosClient.iterateScheduler(timeInPast);

        ScheduledTime inclusivePeriod = timeInPast.plusSeconds(1);
        slotStates = celosClient.getWorkflowStatus(new WorkflowID("workflow-4"), inclusivePeriod);

        for (SlotState slotState : slotStates) {
            Assert.assertEquals(slotState.getStatus(), SlotState.Status.WAITING);
            Assert.assertEquals(slotState.getExternalID(), null);
            Assert.assertEquals(slotState.getRetryCount(), 0);
        }

        celosClient.iterateScheduler(timeInPast);
        slotStates = celosClient.getWorkflowStatus(new WorkflowID("workflow-4"), inclusivePeriod);

        for (SlotState slotState : slotStates) {
            Assert.assertEquals(slotState.getStatus(), SlotState.Status.WAITING);
            Assert.assertEquals(slotState.getExternalID(), null);
            Assert.assertEquals(slotState.getRetryCount(), 0);
        }


        celosClient.iterateScheduler(inclusivePeriod);
        slotStates = celosClient.getWorkflowStatus(new WorkflowID("workflow-4"), inclusivePeriod);
        Assert.assertEquals(slotStates.get(slotStates.size()-1).getStatus(), SlotState.Status.READY);
        for (SlotState slotState : slotStates.subList(0, slotStates.size() - 2)) {
            Assert.assertEquals(slotState.getStatus(), SlotState.Status.WAITING);
            Assert.assertEquals(slotState.getExternalID(), null);
            Assert.assertEquals(slotState.getRetryCount(), 0);
        }

        
    }

    @Test
    public void testParseWorkflowIdsList() throws IOException {
        CelosClient client = new CelosClient(null);

        String str = "{\n" +
                "  \"ids\" : [ \"GC-export-profiles\", \"GrandCentral-01-harmony\"]" +
                "}";

        Set<WorkflowID> result = client.parseWorkflowIdsList(new ByteArrayInputStream(str.getBytes()));
        Assert.assertEquals(result, Sets.newHashSet(new WorkflowID("GrandCentral-01-harmony"), new WorkflowID("GC-export-profiles")));
    }

    @Test
    public void testParseWorkflowStatusesMap() throws IOException {
        CelosClient client = new CelosClient(null);

        String str = "{\n" +
                "  \"2014-10-27T14:00:00.000Z\" : {\n" +
                "    \"status\" : \"SUCCESS\",\n" +
                "    \"externalID\" : \"0029532-141007123109603-oozie-oozi-W\",\n" +
                "    \"retryCount\" : 0\n" +
                "  },\n" +
                "  \"2014-10-27T15:00:00.000Z\" : {\n" +
                "    \"status\" : \"FAILURE\",\n" +
                "    \"externalID\" : \"0029595-141007123109603-oozie-oozi-W\",\n" +
                "    \"retryCount\" : 2\n" +
                "  }" +
                "}";

        WorkflowID workflowID = new WorkflowID("123");
        List<SlotState> result = client.parseWorkflowStatusesMap(workflowID, new ByteArrayInputStream(str.getBytes()));
        junit.framework.Assert.assertEquals(result.size(), 2);

        ScheduledTime time1 = new ScheduledTime("2014-10-27T14:00:00.000Z");
        ScheduledTime time2 = new ScheduledTime("2014-10-27T15:00:00.000Z");

        SlotState val1 = new SlotState(new SlotID(workflowID, time1), SlotState.Status.SUCCESS, "0029532-141007123109603-oozie-oozi-W", 0);
        SlotState val2 = new SlotState(new SlotID(workflowID, time2), SlotState.Status.FAILURE, "0029595-141007123109603-oozie-oozi-W", 2);

        junit.framework.Assert.assertEquals(Sets.newHashSet(result), Sets.newHashSet(val1, val2));
    }


}
