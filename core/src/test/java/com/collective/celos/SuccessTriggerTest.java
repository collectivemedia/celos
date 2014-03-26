package com.collective.celos;

import com.collective.celos.api.ScheduledTime;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class SuccessTriggerTest {

    private final ObjectMapper mapper = new ObjectMapper();
    
    @Test(expected=IllegalArgumentException.class)
    public void testRequiresWorkflow() throws Exception {
        new SuccessTrigger(mapper.createObjectNode());
    }

    @Test
    public void testSucessfullyCreates() throws Exception {
        ObjectNode node = mapper.createObjectNode();
        node.put(SuccessTrigger.COMMAND_PROP, "foo");
        new SuccessTrigger(node);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testWrongArgument() throws Exception {
        ArrayNode array = mapper.createArrayNode();
        array.add(12);
        array.add("-h");
        array.add("now");
        ObjectNode node = mapper.createObjectNode();
        node.put(SuccessTrigger.COMMAND_PROP, array);
        new SuccessTrigger(node);
    }
    
    @Test
    public void testUsesWorkflowProvided() throws Exception {
        ObjectNode node = mapper.createObjectNode();
        node.put(SuccessTrigger.COMMAND_PROP, "foo");
        Assert.assertEquals("foo", new SuccessTrigger(node).getTriggerWorkflow());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFailIsDataAvailableInternalTrigger() throws Exception {
        ObjectNode node = mapper.createObjectNode();
        node.put(SuccessTrigger.COMMAND_PROP, "foo");

        SuccessTrigger trigger = new SuccessTrigger(node);
        trigger.isDataAvailable(new ScheduledTime(DateTime.now()), new ScheduledTime(DateTime.now()));
    }

    @Test
    public void testIsDataAvailableSuccess() throws Exception {
        ObjectNode node = mapper.createObjectNode();
        node.put(SuccessTrigger.COMMAND_PROP, "foo");

        SuccessTrigger trigger = new SuccessTrigger(node);

        Scheduler s = Mockito.mock(Scheduler.class);
        MemoryStateDatabase msd = new MemoryStateDatabase();

        ScheduledTime stNow = new ScheduledTime(DateTime.now(DateTimeZone.UTC));

        msd.putSlotState(new SlotState(new SlotID(new WorkflowID("foo"), stNow), SlotState.Status.SUCCESS));

        Mockito.when(s.getStateDatabase()).thenReturn(msd);

        Assert.assertTrue(trigger.isDataAvailable(s, stNow, stNow));
    }

    @Test
    public void testIsDataAvailableWait() throws Exception {
        ObjectNode node = mapper.createObjectNode();
        node.put(SuccessTrigger.COMMAND_PROP, "foo");

        SuccessTrigger trigger = new SuccessTrigger(node);

        Scheduler s = Mockito.mock(Scheduler.class);
        MemoryStateDatabase msd = new MemoryStateDatabase();

        ScheduledTime stNow = new ScheduledTime(DateTime.now(DateTimeZone.UTC));

        msd.putSlotState(new SlotState(new SlotID(new WorkflowID("foo"), stNow), SlotState.Status.WAITING));

        Mockito.when(s.getStateDatabase()).thenReturn(msd);

        Assert.assertFalse(trigger.isDataAvailable(s, stNow, stNow));
    }

    @Test
    public void testIsDataAvailableNoData() throws Exception {

        ObjectNode node = mapper.createObjectNode();
        node.put(SuccessTrigger.COMMAND_PROP, "foo");

        SuccessTrigger trigger = new SuccessTrigger(node);

        Scheduler s = Mockito.mock(Scheduler.class);
        MemoryStateDatabase msd = new MemoryStateDatabase();

        ScheduledTime stNow = new ScheduledTime(DateTime.now(DateTimeZone.UTC));

        msd.putSlotState(new SlotState(new SlotID(new WorkflowID("foo2"), stNow), SlotState.Status.SUCCESS));

        Mockito.when(s.getStateDatabase()).thenReturn(msd);

        Assert.assertFalse(trigger.isDataAvailable(s, stNow, stNow));
    }



}
