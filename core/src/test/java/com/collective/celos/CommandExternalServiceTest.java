package com.collective.celos;

import junit.framework.Assert;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.collective.celos.api.ScheduledTime;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

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
        ObjectNode props = mapper.createObjectNode();
        props.put(CommandExternalService.COMMAND_PROP, command);
        props.put(CommandExternalService.DATABASE_DIR_PROP, getTestDatabaseDir());
        props.put(CommandExternalService.WRAPPER_COMMAND_PROP, "src/main/bash/celos-wrapper");
        ExternalService srv = new CommandExternalService(props);
        
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
        return databaseDir.newFolder("db").toString();
    }
    
}
