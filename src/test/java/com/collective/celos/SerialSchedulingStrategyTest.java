package com.collective.celos;

import static com.collective.celos.SlotState.Status.FAILURE;
import static com.collective.celos.SlotState.Status.READY;
import static com.collective.celos.SlotState.Status.RUNNING;
import static com.collective.celos.SlotState.Status.SUCCESS;
import static com.collective.celos.SlotState.Status.TIMEOUT;
import static com.collective.celos.SlotState.Status.WAITING;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SerialSchedulingStrategyTest {

    private WorkflowID workflow;
    private SlotID slot1;
    private SlotID slot2;
    private SlotID slot3;
    private SlotID slot4;
    private SlotID slot5;

    @Before
    public void setUp() {
        workflow = new WorkflowID("foo");
        slot1 = new SlotID(workflow, new ScheduledTime(
                "2013-11-18T20:00Z"));
        slot2 = new SlotID(workflow, new ScheduledTime(
                "2013-11-18T21:00Z"));
        slot3 = new SlotID(workflow, new ScheduledTime(
                "2013-11-18T22:00Z"));
        slot4 = new SlotID(workflow, new ScheduledTime(
                "2013-11-18T23:00Z"));
        slot5 = new SlotID(workflow, new ScheduledTime(
                "2013-11-19T00:00Z"));
    }

    @Test
    public void testHasNoSlots() {
        List<SlotState> input = Arrays
                .asList(new SlotState[] {});
        List<SlotID> expected = Arrays.asList(new SlotID[] {});

        SchedulingStrategy strategy = new SerialSchedulingStrategy();
        Assert.assertEquals(expected, strategy.getSchedulingCandidates(input));
    }

    @Test
    public void testHasRunningSlot() {
        List<SlotState> input = Arrays
                .asList(new SlotState[] {
                        new SlotState(slot1, FAILURE),
                        new SlotState(slot1, READY),
                        new SlotState(slot2, RUNNING),
                        new SlotState(slot3, SUCCESS),
                        new SlotState(slot4, TIMEOUT),
                        new SlotState(slot5, WAITING), });
        List<SlotID> expected = Arrays.asList(new SlotID[] {});

        SchedulingStrategy strategy = new SerialSchedulingStrategy();
        Assert.assertEquals(expected, strategy.getSchedulingCandidates(input));
    }

    @Test
    public void testHasReadySlot() {
        List<SlotState> input = Arrays
                .asList(new SlotState[] {
                        new SlotState(slot1, FAILURE),
                        new SlotState(slot1, READY),
                        new SlotState(slot3, SUCCESS),
                        new SlotState(slot4, TIMEOUT),
                        new SlotState(slot5, WAITING), });
        List<SlotID> expected = Arrays.asList(new SlotID[] { slot1 });

        SchedulingStrategy strategy = new SerialSchedulingStrategy();
        Assert.assertEquals(expected, strategy.getSchedulingCandidates(input));
    }

    @Test
    public void testHasMultipleReadySlots() {
        List<SlotState> input = Arrays
                .asList(new SlotState[] {
                        new SlotState(slot1, FAILURE),
                        new SlotState(slot1, READY),
                        new SlotState(slot3, SUCCESS),
                        new SlotState(slot4, READY),
                        new SlotState(slot5, READY), });
        List<SlotID> expected = Arrays.asList(new SlotID[] { slot1 });

        SchedulingStrategy strategy = new SerialSchedulingStrategy();
        Assert.assertEquals(expected, strategy.getSchedulingCandidates(input));
    }

    @Test
    public void testHasNoReadySlots() {
        List<SlotState> input = Arrays
                .asList(new SlotState[] {
                        new SlotState(slot1, FAILURE),
                        new SlotState(slot1, TIMEOUT),
                        new SlotState(slot3, SUCCESS),
                        new SlotState(slot4, WAITING),
                        new SlotState(slot5, SUCCESS), });
        List<SlotID> expected = Arrays.asList(new SlotID[] {});

        SchedulingStrategy strategy = new SerialSchedulingStrategy();
        Assert.assertEquals(expected, strategy.getSchedulingCandidates(input));
    }

}
