package com.collective.celos;

import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class TrivialSchedulingStrategyTest {

    @Test
    public void trivialSchedulingWorksForEmptyStates() {
        SchedulingStrategy strategy = makeTrivialSchedulingStrategy();
        List<SlotState> states = new LinkedList<SlotState>();
        List<SlotState> slots = strategy.getSchedulingCandidates(states);
        Assert.assertEquals(slots.size(), 0);
    }

    @Test
    public void trivialSchedulingWorks() {
        SchedulingStrategy strategy = makeTrivialSchedulingStrategy();
        WorkflowID workflow = new WorkflowID("foo");
        List<SlotState> states = new LinkedList<SlotState>();
        
        SlotID slot1 = new SlotID(workflow, new ScheduledTime("2013-11-18T20:00Z"));
        SlotState state1 = new SlotState(slot1, SlotState.Status.READY);
        states.add(state1);
        
        SlotID slot2 = new SlotID(workflow, new ScheduledTime("2013-11-18T21:00Z"));
        SlotState state2 = new SlotState(slot2, SlotState.Status.WAITING);
        states.add(state2);
        
        SlotID slot3 = new SlotID(workflow, new ScheduledTime("2013-11-18T22:00Z"));
        SlotState state3 = new SlotState(slot3, SlotState.Status.RUNNING);
        states.add(state3);
        
        SlotID slot4 = new SlotID(workflow, new ScheduledTime("2013-11-18T23:00Z"));
        SlotState state4 = new SlotState(slot4, SlotState.Status.READY);
        states.add(state4);
        
        SlotID slot5 = new SlotID(workflow, new ScheduledTime("2013-11-19T00:00Z"));
        SlotState state5 = new SlotState(slot5, SlotState.Status.READY);
        states.add(state5);
        
        List<SlotState> expectedSlots = new LinkedList<SlotState>();
        expectedSlots.add(state1);
        expectedSlots.add(state4);
        expectedSlots.add(state5);

        List<SlotState> slots = strategy.getSchedulingCandidates(states);        
        Assert.assertEquals(expectedSlots, slots);
    }

    private TrivialSchedulingStrategy makeTrivialSchedulingStrategy() {
        return new TrivialSchedulingStrategy();
    }

}