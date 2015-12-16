/*
 * Copyright 2015 Collective, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.collective.celos;

import java.io.IOException;
import java.util.*;

import com.collective.celos.database.StateDatabaseConnection;
import com.google.common.collect.Sets;
import junit.framework.Assert;

import org.joda.time.Interval;
import org.junit.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

public abstract class AbstractStateDatabaseTest {

    public abstract StateDatabaseConnection getStateDatabaseConnection() throws IOException;

    private final Comparator<SlotState> comparator = (o1, o2) -> o1.getScheduledTime().compareTo(o2.getScheduledTime());

    @Test
    public void getAndPutWorks() throws Exception {
        StateDatabaseConnection db = getStateDatabaseConnection();
        SlotID slotID = new SlotID(new WorkflowID("foo"), new ScheduledTime("2013-11-27T14:50Z"));
        Assert.assertEquals(null, db.getSlotState(slotID));
        SlotState state = new SlotState(slotID, SlotState.Status.READY);
        db.putSlotState(state);
        Assert.assertEquals(state, db.getSlotState(slotID));
        Assert.assertEquals(null, db.getSlotState(new SlotID(new WorkflowID("bar"), new ScheduledTime("2013-11-27T14:50Z"))));
    }

    @Test
    public void getAndPutWorksWithExternalID() throws Exception {
        StateDatabaseConnection db = getStateDatabaseConnection();
        SlotID slotID = new SlotID(new WorkflowID("foo"), new ScheduledTime("2013-11-27T14:50Z"));
        Assert.assertEquals(null, db.getSlotState(slotID));
        String externalId = "externalId";
        SlotState state = new SlotState(slotID, SlotState.Status.READY, externalId, 5);
        db.putSlotState(state);
        SlotState newSlotState = db.getSlotState(slotID);
        Assert.assertEquals(state, newSlotState);
        Assert.assertEquals(newSlotState.getExternalID(), externalId);
        Assert.assertEquals(newSlotState.getRetryCount(), 5);
    }

    @Test
    public void getAndPutWorksWithExternalIDForPeriod() throws Exception {
        StateDatabaseConnection db = getStateDatabaseConnection();
        WorkflowID workflowID = new WorkflowID("foo");
        ScheduledTime startTime = new ScheduledTime("2013-11-27T14:50Z");
        ScheduledTime midTime = new ScheduledTime("2013-11-27T15:50Z");
        ScheduledTime endTime = new ScheduledTime("2013-11-27T16:50Z");

        SlotID slotID1 = new SlotID(workflowID, startTime);
        SlotID slotID2 = new SlotID(workflowID, midTime);
        SlotID slotID3 = new SlotID(workflowID, endTime);

        SlotState state1 = new SlotState(slotID1, SlotState.Status.READY, "externalId1", 1);
        SlotState state2 = new SlotState(slotID2, SlotState.Status.READY, "externalId2", 2);
        SlotState state3 = new SlotState(slotID3, SlotState.Status.READY, "externalId3", 3);

        db.putSlotState(state1);
        db.putSlotState(state2);
        db.putSlotState(state3);

        Map<SlotID, SlotState> expected = ImmutableMap.of(slotID1, state1, slotID2, state2, slotID3, state3);
        Map<SlotID, SlotState> anotherExternalId = ImmutableMap.of(slotID1, new SlotState(slotID1, SlotState.Status.READY, "!!!", 1), slotID2, state2, slotID3, state3);

        Map<SlotID, SlotState> slotStates1 = db.getSlotStates(workflowID, Arrays.asList(startTime, midTime, endTime));
        Map<SlotID, SlotState> slotStates2 = db.getSlotStates(workflowID, startTime, endTime.plusSeconds(1));

        Assert.assertEquals(expected, slotStates1);
        Assert.assertEquals(expected, slotStates2);
        Assert.assertNotSame(anotherExternalId, slotStates2);

    }


    @Test
    public void testGetSlotStatesForPeriod() throws Exception {
        StateDatabaseConnection db = getStateDatabaseConnection();
        for (SlotState state : getStatesForSeveralDays()) {
            db.putSlotState(state);
        }
        WorkflowID wf1 = new WorkflowID("workflow-1");
        ScheduledTime time0 = new ScheduledTime("2013-12-02T10:00Z");
        ScheduledTime time1 = new ScheduledTime("2013-12-02T20:00Z");
        ScheduledTime time2 = new ScheduledTime("2013-12-03T00:00Z");
        ScheduledTime time3 = new ScheduledTime("2013-12-03T20:00Z");
        ScheduledTime time4 = new ScheduledTime("2013-12-04T01:00Z");

        List<SlotState> slotStates0 = new ArrayList<>(db.getSlotStates(wf1, time0, time2).values());
        Collections.sort(slotStates0, comparator);
        Assert.assertEquals(slotStates0.size(), 7);
        assertSlotStatesOrder(slotStates0);

        List<SlotState> slotStates1 = new ArrayList<>(db.getSlotStates(wf1, time1, time2).values());
        Collections.sort(slotStates1, comparator);
        Assert.assertEquals(slotStates1.size(), 4);
        assertSlotStatesOrder(slotStates1);

        List<SlotState> slotStates2 = new ArrayList<>(db.getSlotStates(wf1, time1, time3).values());
        Collections.sort(slotStates2, comparator);
        Assert.assertEquals(slotStates2.size(), 24);
        assertSlotStatesOrder(slotStates2);

        List<SlotState> slotStates4 = new ArrayList<>(db.getSlotStates(wf1, time1, time1).values());
        Collections.sort(slotStates4, comparator);
        Assert.assertEquals(slotStates4.size(), 0);

        List<SlotState> slotStates5 = new ArrayList<>(db.getSlotStates(wf1, time1, time1.plusSeconds(1)).values());
        Collections.sort(slotStates5, comparator);
        Assert.assertEquals(slotStates5.size(), 1);
        Assert.assertEquals(slotStates5.get(0).getScheduledTime(), time1);

        List<SlotState> slotStates6 = new ArrayList<>(db.getSlotStates(wf1, time4, time4.plusMonths(1)).values());
        Collections.sort(slotStates6, comparator);
        Assert.assertEquals(slotStates6.size(), 1);
        Assert.assertEquals(slotStates6.get(0).getScheduledTime(), time4);
    }

    @Test
    public void testGetSlotStatesForParticularTimes() throws Exception {
        StateDatabaseConnection db = getStateDatabaseConnection();
        for (SlotState state : getStatesForSeveralDays()) {
            db.putSlotState(state);
        }
        WorkflowID wf1 = new WorkflowID("workflow-1");
        List<ScheduledTime> times = Lists.newArrayList(new ScheduledTime("2013-12-02T17:00Z"), new ScheduledTime("2013-12-02T19:00Z"), new ScheduledTime("2013-12-03T00:00Z"));

        List<SlotState> slotStates = new ArrayList<>(db.getSlotStates(wf1, times).values());
        Collections.sort(slotStates, comparator);

        Assert.assertEquals(slotStates.size(), 3);
        Assert.assertEquals(slotStates.get(0).getStatus(), SlotState.Status.WAITING);
        Assert.assertEquals(slotStates.get(0).getSlotID(), new SlotID(wf1, new ScheduledTime("2013-12-02T17:00Z")));

        Assert.assertEquals(slotStates.get(1).getStatus(), SlotState.Status.RUNNING);
        Assert.assertEquals(slotStates.get(1).getSlotID(), new SlotID(wf1, new ScheduledTime("2013-12-02T19:00Z")));

        Assert.assertEquals(slotStates.get(2).getStatus(), SlotState.Status.SUCCESS);
        Assert.assertEquals(slotStates.get(2).getSlotID(), new SlotID(wf1, new ScheduledTime("2013-12-03T00:00Z")));
    }

    @Test
    public void testGetSlotStatesForPeriodEmptyList() throws Exception {
        StateDatabaseConnection db = getStateDatabaseConnection();
        Map<SlotID, SlotState> slotStates = db.getSlotStates(new WorkflowID("id"), new ScheduledTime("2013-12-03T00:01Z"), new ScheduledTime("2013-12-03T00:00Z"));
        Assert.assertEquals(slotStates.size(), 0);
    }

    @Test
    public void testRerunExpiration() throws Exception {
        StateDatabaseConnection db = getStateDatabaseConnection();
        WorkflowID wf1 = new WorkflowID("foo");
        WorkflowID wf2 = new WorkflowID("bar");
        ScheduledTime currentTime = new ScheduledTime("2013-12-02T15:00Z");
        Assert.assertEquals(new TreeSet<>(), db.getTimesMarkedForRerun(wf1, currentTime));
        Assert.assertEquals(new TreeSet<>(), db.getTimesMarkedForRerun(wf2, currentTime));
        ScheduledTime rerunTime1 = new ScheduledTime("2011-12-02T13:00Z");
        ScheduledTime rerunTime2 = new ScheduledTime("2011-12-02T14:00Z");
        SlotID wf1slot1 = new SlotID(wf1, rerunTime1);
        SlotID wf1slot2 = new SlotID(wf1, rerunTime2);
        SlotID wf2slot1 = new SlotID(wf2, rerunTime1);
        db.markSlotForRerun(wf1slot1, currentTime);
        db.markSlotForRerun(wf1slot2, currentTime);
        db.markSlotForRerun(wf2slot1, currentTime);
        Assert.assertEquals(new TreeSet<>(ImmutableSet.of(rerunTime1, rerunTime2)), db.getTimesMarkedForRerun(wf1, currentTime));
        Assert.assertEquals(new TreeSet<>(ImmutableSet.of(rerunTime1)), db.getTimesMarkedForRerun(wf2, currentTime));
        // Now call wf1 with much later current time and make sure files got expired after first call
        ScheduledTime laterCurrentTime = currentTime.plusYears(1);
        Assert.assertEquals(new TreeSet<>(ImmutableSet.of(rerunTime1, rerunTime2)), db.getTimesMarkedForRerun(wf1, laterCurrentTime));
        Assert.assertEquals(new TreeSet<>(), db.getTimesMarkedForRerun(wf1, laterCurrentTime));
        // wf2 still in there
        Assert.assertEquals(new TreeSet<>(ImmutableSet.of(rerunTime1)), db.getTimesMarkedForRerun(wf2, currentTime));
        // Now call wf2 with much later current time and make sure files got expired after first call
        Assert.assertEquals(new TreeSet<>(ImmutableSet.of(rerunTime1)), db.getTimesMarkedForRerun(wf2, laterCurrentTime));
        Assert.assertEquals(new TreeSet<>(), db.getTimesMarkedForRerun(wf2, laterCurrentTime));
    }

    @Test
    public void testPause() throws Exception {
        StateDatabaseConnection db = getStateDatabaseConnection();
        WorkflowID workflowID = new WorkflowID("wf1");

        Assert.assertFalse(db.isPaused(workflowID));

        db.setPaused(workflowID, true);
        Assert.assertTrue(db.isPaused(workflowID));

        db.setPaused(workflowID, false);
        Assert.assertFalse(db.isPaused(workflowID));
    }

    @Test
    public void testRegisters() throws Exception {
        StateDatabaseConnection db = getStateDatabaseConnection();
        BucketID bucket1 = new BucketID("foo-bucket-Iñtërnâtiônàlizætiøn");
        BucketID bucket2 = new BucketID("another-bucket-Iñtërnâtiônàlizætiøn");
        RegisterKey key1 = new RegisterKey("bar-key-Iñtërnâtiônàlizætiøn");
        RegisterKey key2 = new RegisterKey("quux-key-Iñtërnâtiônàlizætiøn");
        
        Assert.assertEquals(ImmutableMap.of().entrySet(), db.getAllRegisters(bucket1));
        Assert.assertEquals(ImmutableMap.of().entrySet(), db.getAllRegisters(bucket2));
        Assert.assertNull(db.getRegister(bucket1, key1));
        Assert.assertNull(db.getRegister(bucket2, key2));
        
        ObjectNode value1 = Util.MAPPER.createObjectNode();
        value1.put("foo", "Iñtërnâtiônàlizætiøn");

        ObjectNode value3 = Util.MAPPER.createObjectNode();
        value3.put("bar", "Iñtërnâtiônàlizætiøn");

        ObjectNode value2 = Util.MAPPER.createObjectNode();
        value2.put("bar", "Internatiolization");
        db.putRegister(bucket1, key1, value1);
        Assert.assertEquals(value1, db.getRegister(bucket1, key1));
        Assert.assertEquals(ImmutableMap.of(key1, value1).entrySet(), db.getAllRegisters(bucket1));
        Assert.assertNull(db.getRegister(bucket2, key2));
        Assert.assertEquals(ImmutableMap.of().entrySet(), db.getAllRegisters(bucket2));

        db.putRegister(bucket2, key2, value3);
        Assert.assertEquals(value1, db.getRegister(bucket1, key1));
        Assert.assertEquals(ImmutableMap.of(key1, value1).entrySet(), db.getAllRegisters(bucket1));
        Assert.assertEquals(value3, db.getRegister(bucket2, key2));
        Assert.assertEquals(ImmutableMap.of(key2, value3).entrySet(), db.getAllRegisters(bucket2));

        db.putRegister(bucket2, key2, value2);
        Assert.assertEquals(value1, db.getRegister(bucket1, key1));
        Assert.assertEquals(ImmutableMap.of(key1, value1).entrySet(), db.getAllRegisters(bucket1));
        Assert.assertEquals(value2, db.getRegister(bucket2, key2));
        Assert.assertEquals(ImmutableMap.of(key2, value2).entrySet(), db.getAllRegisters(bucket2));

        db.deleteRegister(bucket1, key1);
        Assert.assertNull(db.getRegister(bucket1, key1));
        Assert.assertEquals(ImmutableMap.of().entrySet(), db.getAllRegisters(bucket1));
        Assert.assertEquals(value2, db.getRegister(bucket2, key2));
        Assert.assertEquals(ImmutableMap.of(key2, value2).entrySet(), db.getAllRegisters(bucket2));
        
        db.deleteRegister(bucket2, key2);
        Assert.assertNull(db.getRegister(bucket1, key1));
        Assert.assertEquals(ImmutableMap.of().entrySet(), db.getAllRegisters(bucket1));
        Assert.assertNull(db.getRegister(bucket2, key2));
        Assert.assertEquals(ImmutableMap.of().entrySet(), db.getAllRegisters(bucket2));

        db.putRegister(bucket2, key2, value2);
        Assert.assertNull(db.getRegister(bucket1, key1));
        Assert.assertEquals(ImmutableMap.of().entrySet(), db.getAllRegisters(bucket1));
        Assert.assertEquals(value2, db.getRegister(bucket2, key2));
        Assert.assertEquals(ImmutableMap.of(key2, value2).entrySet(), db.getAllRegisters(bucket2));

        db.putRegister(bucket2, key2, value3);
        Assert.assertNull(db.getRegister(bucket1, key1));
        Assert.assertEquals(ImmutableMap.of().entrySet(), db.getAllRegisters(bucket1));
        Assert.assertEquals(value3, db.getRegister(bucket2, key2));
        Assert.assertEquals(ImmutableMap.of(key2, value3).entrySet(), db.getAllRegisters(bucket2));

        db.deleteRegister(bucket2, key2);
        Assert.assertNull(db.getRegister(bucket1, key1));
        Assert.assertEquals(ImmutableMap.of().entrySet(), db.getAllRegisters(bucket1));
        Assert.assertNull(db.getRegister(bucket2, key2));
        Assert.assertEquals(ImmutableMap.of().entrySet(), db.getAllRegisters(bucket2));
    }

    @Test
    public void testRegisterKeys() throws Exception {
        StateDatabaseConnection db = getStateDatabaseConnection();
        BucketID bucket1 = new BucketID("foo-bucket-Iñtërnâtiônàlizætiøn");
        BucketID bucket2 = new BucketID("another-bucket-Iñtërnâtiônàlizætiøn");
        RegisterKey key1 = new RegisterKey("bar-key-Iñtërnâtiônàlizætiøn");
        RegisterKey key2 = new RegisterKey("quux-key-Iñtërnâtiônàlizætiøn");
        RegisterKey key3 = new RegisterKey("quux2-key-Iñtërnâtiônàlizætiøn");
        String key1Prefix = "bar";
        String key23Prefix = "quux";
        String prefixDoesntExist = "zxc";

        ObjectNode value1 = Util.MAPPER.createObjectNode();
        value1.put("foo", "Iñtërnâtiônàlizætiøn");

        ObjectNode value3 = Util.MAPPER.createObjectNode();
        value3.put("bar", "Iñtërnâtiônàlizætiøn");

        ObjectNode value2 = Util.MAPPER.createObjectNode();
        value2.put("bar", "Internatiolization");
        db.putRegister(bucket1, key1, value1);
        db.putRegister(bucket1, key2, value2);
        db.putRegister(bucket1, key3, value3);
        db.putRegister(bucket2, key1, value1);
        db.putRegister(bucket2, key2, value2);

        Assert.assertEquals(Sets.newHashSet(key1, key2, key3), db.getRegisterKeys(bucket1, null));
        Assert.assertEquals(Sets.newHashSet(key1, key2), db.getRegisterKeys(bucket2, null));
        Assert.assertEquals(Sets.newHashSet(key2, key3), db.getRegisterKeys(bucket1, key23Prefix));
        Assert.assertEquals(Sets.newHashSet(key1), db.getRegisterKeys(bucket1, key1Prefix));
        Assert.assertEquals(Sets.newHashSet(key2), db.getRegisterKeys(bucket2, key23Prefix));
        Assert.assertEquals(Sets.newHashSet(key1), db.getRegisterKeys(bucket2, key1Prefix));
        Assert.assertEquals(Collections.emptySet(), db.getRegisterKeys(bucket1, prefixDoesntExist));
        Assert.assertEquals(Collections.emptySet(), db.getRegisterKeys(bucket2, prefixDoesntExist));

        db.deleteRegistersWithPrefix(bucket1, key23Prefix);
        Assert.assertEquals(Sets.newHashSet(key1), db.getRegisterKeys(bucket1, null));
        Assert.assertEquals(Sets.newHashSet(key1, key2), db.getRegisterKeys(bucket2, null));

    }

    private void assertSlotStatesOrder(List<SlotState> slotStates) {
        SlotState slotState = slotStates.get(0);
        for (int i = 1; i < slotStates.size(); i++) {
            SlotState nextSlot = slotStates.get(i);
            Interval interval = new Interval(slotState.getScheduledTime().getDateTime(), nextSlot.getScheduledTime().getDateTime());
            Assert.assertEquals(interval.toDuration().getStandardSeconds(), 60 * 60);
            slotState = nextSlot;
        }
    }

    private Set<SlotState> getStatesForSeveralDays() {
        Set<SlotState> states = new HashSet<SlotState>();
        WorkflowID wf1 = new WorkflowID("workflow-1");

        states.add(new SlotState(new SlotID(wf1, new ScheduledTime("2013-12-02T17:00Z")), SlotState.Status.WAITING));
        states.add(new SlotState(new SlotID(wf1, new ScheduledTime("2013-12-02T18:00Z")),
                SlotState.Status.READY));
        states.add(new SlotState(new SlotID(wf1, new ScheduledTime("2013-12-02T19:00Z")),
                SlotState.Status.READY).transitionToRunning("foo-bar"));
        states.add(new SlotState(new SlotID(wf1, new ScheduledTime("2013-12-02T20:00Z")),
                SlotState.Status.SUCCESS));
        states.add(new SlotState(new SlotID(wf1, new ScheduledTime("2013-12-02T21:00Z")),
                SlotState.Status.SUCCESS));
        states.add(new SlotState(new SlotID(wf1, new ScheduledTime("2013-12-02T22:00Z")),
                SlotState.Status.SUCCESS));
        states.add(new SlotState(new SlotID(wf1, new ScheduledTime("2013-12-02T23:00Z")),
                SlotState.Status.SUCCESS));
        states.add(new SlotState(new SlotID(wf1, new ScheduledTime("2013-12-03T00:00Z")),
                SlotState.Status.SUCCESS));
        states.add(new SlotState(new SlotID(wf1, new ScheduledTime("2013-12-03T01:00Z")),
                SlotState.Status.SUCCESS));
        states.add(new SlotState(new SlotID(wf1, new ScheduledTime("2013-12-03T02:00Z")),
                SlotState.Status.SUCCESS));
        states.add(new SlotState(new SlotID(wf1, new ScheduledTime("2013-12-03T03:00Z")),
                SlotState.Status.SUCCESS));
        states.add(new SlotState(new SlotID(wf1, new ScheduledTime("2013-12-03T04:00Z")),
                SlotState.Status.SUCCESS));
        states.add(new SlotState(new SlotID(wf1, new ScheduledTime("2013-12-03T05:00Z")),
                SlotState.Status.SUCCESS));
        states.add(new SlotState(new SlotID(wf1, new ScheduledTime("2013-12-03T06:00Z")),
                SlotState.Status.SUCCESS));
        states.add(new SlotState(new SlotID(wf1, new ScheduledTime("2013-12-03T07:00Z")),
                SlotState.Status.SUCCESS));
        states.add(new SlotState(new SlotID(wf1, new ScheduledTime("2013-12-03T08:00Z")),
                SlotState.Status.SUCCESS));
        states.add(new SlotState(new SlotID(wf1, new ScheduledTime("2013-12-03T09:00Z")),
                SlotState.Status.SUCCESS));
        states.add(new SlotState(new SlotID(wf1, new ScheduledTime("2013-12-03T10:00Z")),
                SlotState.Status.SUCCESS));
        states.add(new SlotState(new SlotID(wf1, new ScheduledTime("2013-12-03T11:00Z")),
                SlotState.Status.SUCCESS));
        states.add(new SlotState(new SlotID(wf1, new ScheduledTime("2013-12-03T12:00Z")),
                SlotState.Status.SUCCESS));
        states.add(new SlotState(new SlotID(wf1, new ScheduledTime("2013-12-03T13:00Z")),
                SlotState.Status.SUCCESS));
        states.add(new SlotState(new SlotID(wf1, new ScheduledTime("2013-12-03T14:00Z")),
                SlotState.Status.SUCCESS));
        states.add(new SlotState(new SlotID(wf1, new ScheduledTime("2013-12-03T15:00Z")),
                SlotState.Status.SUCCESS));
        states.add(new SlotState(new SlotID(wf1, new ScheduledTime("2013-12-03T16:00Z")),
                SlotState.Status.SUCCESS));
        states.add(new SlotState(new SlotID(wf1, new ScheduledTime("2013-12-03T17:00Z")),
                SlotState.Status.SUCCESS));
        states.add(new SlotState(new SlotID(wf1, new ScheduledTime("2013-12-03T18:00Z")),
                SlotState.Status.SUCCESS));
        states.add(new SlotState(new SlotID(wf1, new ScheduledTime("2013-12-03T19:00Z")),
                SlotState.Status.SUCCESS));
        states.add(new SlotState(new SlotID(wf1, new ScheduledTime("2013-12-03T20:00Z")),
                SlotState.Status.SUCCESS));
        states.add(new SlotState(new SlotID(wf1, new ScheduledTime("2013-12-03T21:00Z")),
                SlotState.Status.SUCCESS));
        states.add(new SlotState(new SlotID(wf1, new ScheduledTime("2013-12-03T22:00Z")),
                SlotState.Status.SUCCESS));
        states.add(new SlotState(new SlotID(wf1, new ScheduledTime("2013-12-03T23:00Z")),
                SlotState.Status.SUCCESS));
        states.add(new SlotState(new SlotID(wf1, new ScheduledTime("2013-12-04T00:00Z")),
                SlotState.Status.SUCCESS));
        states.add(new SlotState(new SlotID(wf1, new ScheduledTime("2013-12-04T01:00Z")),
                SlotState.Status.SUCCESS));

        return states;
    }


}
