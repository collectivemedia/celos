package com.collective.celos;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ShellCommandTriggerTest {

    private final ObjectMapper mapper = new ObjectMapper();
    
    @Test(expected=IllegalArgumentException.class)
    public void testRequiresCommand() throws Exception {
        new CommandTrigger(mapper.createObjectNode());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testCommandMustBeArray() throws Exception {
        ObjectNode node = mapper.createObjectNode();
        node.put(CommandTrigger.COMMAND_PROP, "foo");
        new CommandTrigger(node);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testOnlyStringsAllowedInArray() throws Exception {
        ArrayNode array = mapper.createArrayNode();
        array.add(12);
        array.add("-h");
        array.add("now");
        ObjectNode node = mapper.createObjectNode();
        node.put(CommandTrigger.COMMAND_PROP, array);
        new CommandTrigger(node);
    }
    
    @Test
    public void testUsesCommand() throws Exception {
        ObjectNode node = makeConfig("shutdown", "-h", "now");
        Assert.assertEquals(Arrays.asList("shutdown", "-h", "now"), new CommandTrigger(node).getRawCommandElements());
    }

    @Test
    public void testCheckSuccessExitValueProperly() throws Exception {
        ObjectNode node = makeConfig("true");
        Assert.assertTrue(new CommandTrigger(node).isDataAvailable(ScheduledTime.now(), ScheduledTime.now()));
    }
    
    @Test
    public void testChecksFailureExitValueProperly() throws Exception {
        ObjectNode node = makeConfig("false");
        Assert.assertFalse(new CommandTrigger(node).isDataAvailable(ScheduledTime.now(), ScheduledTime.now()));
    }

    @Test
    public void testSetsVariables() throws Exception {
        String time = "2014-03-24T09:25:13Z";
        ScheduledTime t = new ScheduledTime(time);
        ObjectNode node = makeConfig("src/test/resources/com/collective/celos/shell-command-trigger-test-script.sh", 
                                     "${year}-${month}-${day}T${hour}:${minute}:${second}Z");
        Assert.assertTrue(new CommandTrigger(node).isDataAvailable(ScheduledTime.now(), t));
    }
    
    private ObjectNode makeConfig(String... command) {
        ObjectNode node = mapper.createObjectNode();
        ArrayNode array = mapper.createArrayNode();
        for (String s : command) { array.add(s); }
        node.put(CommandTrigger.COMMAND_PROP, array);
        return node;
    }

}
