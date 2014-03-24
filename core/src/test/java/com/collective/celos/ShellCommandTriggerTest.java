package com.collective.celos;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import com.collective.celos.api.ScheduledTime;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ShellCommandTriggerTest {

    private final ObjectMapper mapper = new ObjectMapper();
    
    @Test(expected=IllegalArgumentException.class)
    public void testRequiresCommand() throws Exception {
        new ShellCommandTrigger(mapper.createObjectNode());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testCommandMustBeArray() throws Exception {
        ObjectNode node = mapper.createObjectNode();
        node.put(ShellCommandTrigger.COMMAND_PROP, "foo");
        new ShellCommandTrigger(node);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testOnlyStringsAllowedInArray() throws Exception {
        ArrayNode array = mapper.createArrayNode();
        array.add(12);
        array.add("-h");
        array.add("now");
        ObjectNode node = mapper.createObjectNode();
        node.put(ShellCommandTrigger.COMMAND_PROP, array);
        new ShellCommandTrigger(node);
    }
    
    @Test
    public void testUsesCommand() throws Exception {
        ObjectNode node = makeConfig("shutdown", "-h", "now");
        Assert.assertEquals(Arrays.asList("shutdown", "-h", "now"), new ShellCommandTrigger(node).getRawCommandElements());
    }

    @Test
    public void testCheckSuccessExitValueProperly() throws Exception {
        ObjectNode node = makeConfig("true");
        Assert.assertTrue(new ShellCommandTrigger(node).isDataAvailable(ScheduledTime.now(), ScheduledTime.now()));
    }
    
    @Test
    public void testChecksFailureExitValueProperly() throws Exception {
        ObjectNode node = makeConfig("false");
        Assert.assertFalse(new ShellCommandTrigger(node).isDataAvailable(ScheduledTime.now(), ScheduledTime.now()));
    }

    @Test
    public void testSetsVariables() throws Exception {
        String time = "2014-03-24T09:25:13Z";
        ScheduledTime t = new ScheduledTime(time);
        ObjectNode node = makeConfig("src/test/resources/com/collective/celos/shell-command-trigger-test-script.sh", 
                                     "${year}-${month}-${day}T${hour}:${minute}:${second}Z");
        Assert.assertTrue(new ShellCommandTrigger(node).isDataAvailable(ScheduledTime.now(), t));
    }
    
    private ObjectNode makeConfig(String... command) {
        ObjectNode node = mapper.createObjectNode();
        ArrayNode array = mapper.createArrayNode();
        for (String s : command) { array.add(s); }
        node.put(ShellCommandTrigger.COMMAND_PROP, array);
        return node;
    }

}
