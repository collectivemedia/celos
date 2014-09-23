package com.collective.celos;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.collective.celos.trigger.CommandTrigger;
import org.junit.Assert;
import org.junit.Test;

import static org.mockito.Mockito.mock;

public class CommandTriggerTest {

    @Test(expected=NullPointerException.class)
    public void testRequiresCommand() throws Exception {
        new CommandTrigger(null);
    }

    @Test
    public void testUsesCommand() throws Exception {
        List<String> elts = makeConfig("shutdown", "-h", "now");
        Assert.assertEquals(Arrays.asList("shutdown", "-h", "now"), new CommandTrigger(elts).getRawCommandElements());
    }

    @Test
    public void testCheckSuccessExitValueProperly() throws Exception {
        List<String> elts = makeConfig("true");
        Scheduler scheduler = mock(Scheduler.class);
        Assert.assertTrue(new CommandTrigger(elts).isDataAvailable(scheduler, ScheduledTime.now(), ScheduledTime.now()));
    }
    
    @Test
    public void testChecksFailureExitValueProperly() throws Exception {
        List<String> elts = makeConfig("false");
        Scheduler scheduler = mock(Scheduler.class);
        Assert.assertFalse(new CommandTrigger(elts).isDataAvailable(scheduler, ScheduledTime.now(), ScheduledTime.now()));
    }

    @Test
    public void testSetsVariables() throws Exception {
        String time = "2014-03-24T09:25:13Z";
        ScheduledTime t = new ScheduledTime(time);
        List<String> elts = makeConfig("src/test/resources/com/collective/celos/shell-command-trigger-test-script.sh", 
                                       "${year}-${month}-${day}T${hour}:${minute}:${second}Z");
        Scheduler scheduler = mock(Scheduler.class);
        Assert.assertTrue(new CommandTrigger(elts).isDataAvailable(scheduler, ScheduledTime.now(), t));
    }
    
    private List<String> makeConfig(String... command) {
        List<String> elements = new LinkedList<>();
        for (int i = 0; i < command.length; i++) {
            elements.add(command[i]);
        }
        return elements;
    }

}
