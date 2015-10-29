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
package com.collective.celos.servlet;

import com.collective.celos.*;
import com.collective.celos.SlotState.Status;
import com.collective.celos.ScheduledTime;
import org.junit.Assert;
import org.junit.Test;

public class RerunServletTest {

    @Test(expected = IllegalStateException.class)
    public void failsOnRunningSlot() throws Exception {
        failsOnWrongStatusTest(SlotState.Status.RUNNING);
    }

    @Test(expected = IllegalStateException.class)
    public void failsOnReadySlot() throws Exception {
        failsOnWrongStatusTest(SlotState.Status.READY);
    }

    @Test(expected = IllegalStateException.class)
    public void failsOnWaitingSlot() throws Exception {
        failsOnWrongStatusTest(SlotState.Status.WAITING);
    }

    private void failsOnWrongStatusTest(Status status) throws Exception {
        StateDatabaseConnection db = new MemoryStateDatabase().openConnection();
        SlotID id = new SlotID(new WorkflowID("foo"), new ScheduledTime("2014-02-08T20:00Z"));
        SlotState state = new SlotState(id, status);
        db.putSlotState(state);
        new RerunServlet().updateSlotToRerun(state, db);
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
        StateDatabaseConnection db = new MemoryStateDatabase().openConnection();
        SlotID id = new SlotID(new WorkflowID("foo"), new ScheduledTime("2014-02-08T20:00Z"));
        SlotState state = new SlotState(id, status);
        db.putSlotState(state);
        new RerunServlet().updateSlotToRerun(state, db);
        SlotState dbState = db.getSlotState(id);
        Assert.assertEquals(state.transitionToRerun(), dbState);
    }
    
}
