package com.collective.celos.servlet;

import com.collective.celos.*;
import com.collective.celos.SlotState.Status;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

public class RerunServletTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test(expected = IllegalStateException.class)
    public void failsOnRunningSlot() throws Exception {
        failsOnWrongStatusTest(SlotState.Status.RUNNING);
    }

    @Test(expected = IllegalStateException.class)
    public void failsOnReadySlot() throws Exception {
        failsOnWrongStatusTest(SlotState.Status.READY);
    }

    @Test
    public void succeedsOnWaitingSlot() throws Exception {
        succeedsOnRightStatusTest(SlotState.Status.WAITING);
    }

    private void failsOnWrongStatusTest(Status status) throws Exception {
        StateDatabase db = new MemoryStateDatabase();
        final ScheduledTime current = new ScheduledTime("2014-02-08T20:00Z");
        final WorkflowID workflowID = new WorkflowID("foo");
        SlotID id = new SlotID(workflowID, current);
        SlotState state = new SlotState(id, status);
        db.putSlotState(state);
        db.updateSlotToRerun(id, current);
    }

    @Test
    public void succeedsOnSuccessSlot() throws Exception {
        succeedsOnRightStatusTest(SlotState.Status.SUCCESS);
    }

    @Test
    public void succeedsOnFailureSlot() throws Exception {
        succeedsOnRightStatusTest(SlotState.Status.FAILURE);
    }

    private void succeedsOnRightStatusTest(Status status) throws Exception {
        StateDatabase db = new MemoryStateDatabase();
        final ScheduledTime time = new ScheduledTime("2014-02-08T20:00Z");
        SlotID id = new SlotID(new WorkflowID("foo"), time);
        SlotState state = new SlotState(id, status);
        db.putSlotState(state);
        db.updateSlotToRerun(id, time);
        SlotState dbState = db.getSlotState(id);
        Assert.assertEquals(state.transitionToRerun(), dbState);
    }


    @Test
    public void testRerunStateJSON1() throws Exception {
        final RerunState rerunState0 = RerunState.fromTime(
                new WorkflowID("ololo"),
                new ScheduledTime("2013-11-11T12:12:00.000Z"),
                new ScheduledTime("2013-11-11T12:14:00.000Z"));
        final String json = MAPPER.writeValueAsString(rerunState0.toJsonNode());
        final RerunState rerunState1 = RerunState.fromJsonNode(MAPPER.readTree(json));
        Assert.assertEquals(rerunState1, rerunState0);
    }

    @Test
    public void testRerunStateJSON2() throws Exception {
        final String json0 = "{\"workflowId\":\"ololo\",\"scheduledTime\":\"2013-11-11T12:12:00.000Z\",\"rerunTime\":\"2013-11-11T12:14:00.000Z\"}";
        final RerunState rerunState = RerunState.fromJsonNode(MAPPER.readTree(json0));
        final String json1 = MAPPER.writeValueAsString(rerunState.toJsonNode());
        Assert.assertEquals(json0, json1);
    }



}
