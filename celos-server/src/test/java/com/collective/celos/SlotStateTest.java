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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Assert;
import org.junit.Test;

import java.time.ZonedDateTime;

public class SlotStateTest {
    
    private final SlotID id = new SlotID(new WorkflowID("foo"), ZonedDateTime.parse("2013-12-04T19:18Z"));
    
    @Test
    public void transitionsWorkAsExpected() {
        SlotState waiting = new SlotState(id, SlotState.Status.WAITING);
        SlotState ready = new SlotState(id, SlotState.Status.READY);
        Assert.assertEquals(ready, waiting.transitionToReady());
        SlotState waitTimeout = new SlotState(id, SlotState.Status.WAIT_TIMEOUT);
        Assert.assertEquals(waitTimeout, waiting.transitionToWaitTimeout());
        SlotState running = new SlotState(id, SlotState.Status.RUNNING, "external", 0);
        Assert.assertEquals(running, ready.transitionToRunning("external"));
        SlotState success = new SlotState(id, SlotState.Status.SUCCESS, "external", 0);
        Assert.assertEquals(success, running.transitionToSuccess());        
        SlotState failure = new SlotState(id, SlotState.Status.FAILURE, "external", 0);
        Assert.assertEquals(failure, running.transitionToFailure());        
        SlotState retry = new SlotState(id, SlotState.Status.WAITING, null, 1);
        Assert.assertEquals(retry, running.transitionToRetry());        
    }
    
    @Test(expected = IllegalStateException.class)
    public void rerunFailsAsExpectedForWaiting() {
        new SlotState(id, SlotState.Status.WAITING).transitionToRerun();
    }
    
    @Test(expected = IllegalStateException.class)
    public void rerunFailsAsExpectedForReady() {
        new SlotState(id, SlotState.Status.READY).transitionToRerun();
    }
    
    @Test(expected = IllegalStateException.class)
    public void rerunFailsAsExpectedForRunning() {
        new SlotState(id, SlotState.Status.RUNNING).transitionToRerun();
    }
    
    @Test
    public void rerunWorksAsExpectedForSuccess() {
        SlotState success = new SlotState(id, SlotState.Status.SUCCESS, "external-ID", 14);
        SlotState expected = new SlotState(id, SlotState.Status.WAITING);
        Assert.assertEquals(expected, success.transitionToRerun());        
    }
    
    @Test
    public void rerunWorksAsExpectedForWaitTimeout() {
        SlotState waitTimeout = new SlotState(id, SlotState.Status.WAIT_TIMEOUT, "external-ID", 14);
        SlotState expected = new SlotState(id, SlotState.Status.WAITING);
        Assert.assertEquals(expected, waitTimeout.transitionToRerun());        
    }

    @Test
    public void rerunWorksAsExpectedForFailure() {
        SlotState failure = new SlotState(id, SlotState.Status.FAILURE, "external-ID", 14);
        SlotState expected = new SlotState(id, SlotState.Status.WAITING);
        Assert.assertEquals(expected, failure.transitionToRerun());        
    }
    
    @Test
    public void slotStateGetZonedDateTimeWorks() {
        ZonedDateTime t = ZonedDateTime.parse("2013-11-26T13:00Z");
        SlotState slotState = new SlotState(new SlotID(new WorkflowID("foo"), t), SlotState.Status.READY);
        Assert.assertEquals(t, slotState.getScheduledTime());
    }
    
    @Test
    public void canRoundtripToJSON() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        SlotState state1 = new SlotState(id, SlotState.Status.WAITING);
        SlotState state2 = new SlotState(id, SlotState.Status.READY).transitionToRunning("foo-external-ID");
        String json1 = "{\"time\":\"2013-12-04T19:18Z\",\"status\":\"WAITING\",\"externalID\":null,\"retryCount\":0}";
        String json2 = "{\"time\":\"2013-12-04T19:18Z\",\"status\":\"RUNNING\",\"externalID\":\"foo-external-ID\",\"retryCount\":0}";

        Assert.assertEquals(json1, mapper.writeValueAsString(state1.toJSONNode()));
        Assert.assertEquals(json2, mapper.writeValueAsString(state2.toJSONNode()));
        Assert.assertEquals(state1, SlotState.fromJSONNode(id.getWorkflowID(), (ObjectNode) mapper.readTree(json1)));
        Assert.assertEquals(state2, SlotState.fromJSONNode(id.getWorkflowID(), (ObjectNode) mapper.readTree(json2)));
    }

}
