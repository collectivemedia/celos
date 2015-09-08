package com.collective.celos.server;

import com.collective.celos.*;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.net.URI;
import java.util.List;

/**
 * Created by akonopko on 31.08.15.
 */
public class AutoScheduleTest {

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
    private int port;

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
        this.port = celosServer.startServer(ImmutableMap.<String, String>of(), workflowsDir, defaultsDir, slotDbDir);
        this.celosClient = new CelosClient(URI.create("http://localhost:" + port));
    }


    @Test
    public void testAutoSchedule() throws Exception {

        File src = new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/client/wf-list").toURI());
        FileUtils.copyDirectory(src, workflowsDir);

        Main.setupAutoschedule(port, 1);

        Assert.assertTrue(autoScheduleWorked());
    }

    private boolean autoScheduleWorked() throws Exception {
        long timeStart = System.currentTimeMillis();

        while (System.currentTimeMillis() - timeStart < 60 * Constants.SECOND_MS) {
            List<SlotState> slotStates = celosClient.getWorkflowStatus(new WorkflowID("workflow-2")).getSlotStates();

            boolean testPassed = true;

//          Test conditions were copied from CelosClientServerTest.testGetWorkflowStatusTransitionToSuccess
            testPassed &= slotStates.size() == SLOTS_IN_CELOS_SERVER_SLIDING_WINDOW;

            SlotState slotStateLast = slotStates.get(slotStates.size() - 1);
            testPassed &= slotStateLast.getStatus().equals(SlotState.Status.SUCCESS);
            testPassed &= slotStateLast.getExternalID() != null;
            testPassed &= slotStateLast.getRetryCount() == 0;

            if (testPassed) {
                return testPassed;
            }
        }
        return false;
    }

}
