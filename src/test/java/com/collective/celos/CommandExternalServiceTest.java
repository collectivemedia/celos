package com.collective.celos;

import junit.framework.Assert;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CommandExternalServiceTest {

    ObjectMapper mapper = new ObjectMapper();
    
    @Rule
    public TemporaryFolder databaseDir = new TemporaryFolder();

    @Test
    public void canRunSuccessfulCommand() throws Exception {
        runCommandWithExpectedStatus("echo \"foo\"", CommandExternalService.SUCCESS);
    }

    @Test
    public void canRunFailingCommand() throws Exception {
        runCommandWithExpectedStatus("false", CommandExternalService.FAILURE);
    }
    
    @Test
    public void canRunLongRunningSuccessfulCommand() throws Exception {
        runCommandWithExpectedStatus("sleep 1", CommandExternalService.SUCCESS);
    }

    @Test
    public void canRunLongRunningFailingCommand() throws Exception {
        runCommandWithExpectedStatus("sleep 1; false", CommandExternalService.FAILURE);
    }
    
    @Test
    public void canRunGarbageCommand() throws Exception {
        runCommandWithExpectedStatus("saddkja s7asd9sa", CommandExternalService.FAILURE);
    }

    private void runCommandWithExpectedStatus(String command, ExternalStatus status) throws Exception {
        ExternalService srv = new CommandExternalService(command, "src/main/bash/celos-wrapper", getTestDatabaseDir());
        
        SlotID slotID = new SlotID(new WorkflowID("foo"), ScheduledTime.now());
        String extID = srv.submit(slotID);
        srv.start(slotID, extID);
        
        // Give wrappers enough time to create PID file
        Thread.sleep(100);
        
        int millis = 0;
        int sleepMillis = 50;

        while(srv.getStatus(slotID, extID).isRunning()) {
            if (millis > 5000) {
                throw new RuntimeException("External command " + command + " timed out.");
            }
            Thread.sleep(sleepMillis);
            millis += sleepMillis;
        }
        Assert.assertEquals(status, srv.getStatus(slotID, extID));
    }

    private String getTestDatabaseDir() {
        try {
            return databaseDir.newFolder("db").toString();
        } catch (Exception e) {
            return null;
        }

    }
    
}
