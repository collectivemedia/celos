package com.collective.celos.ci.mode.test.client;

import com.collective.celos.ScheduledTime;
import com.collective.celos.SlotID;
import com.collective.celos.SlotState;
import com.collective.celos.WorkflowID;
import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Set;

/**
 * Created by akonopko on 03.02.15.
 */
public class CelosClientTest {

    @Test (expected = IllegalArgumentException.class)
    public void testCelosClientFailsAdressNotValid() throws IOException {
        new CelosClient("stupid adress");
    }

    @Test (expected = UnknownHostException.class)
    public void testCelosClientFailsNoServer() throws IOException {
        CelosClient celosClient = new CelosClient("http://thereisnosuchhost:1234");
        celosClient.getWorkflowList();
    }


    @Test
    public void testParseWorkflowStatusesMap() throws IOException {

        String str = "{\n" +
                "  \"2014-10-27T14:00:00.000Z\" : {\n" +
                "    \"status\" : \"SUCCESS\",\n" +
                "    \"externalID\" : \"0029532-141007123109603-oozie-oozi-W\",\n" +
                "    \"retryCount\" : 0\n" +
                "  },\n" +
                "  \"2014-10-27T15:00:00.000Z\" : {\n" +
                "    \"status\" : \"FAILURE\",\n" +
                "    \"externalID\" : \"0029595-141007123109603-oozie-oozi-W\",\n" +
                "    \"retryCount\" : 2\n" +
                "  }" +
                "}";

        WorkflowID workflowID = new WorkflowID("123");
        List<SlotState> result = new CelosClient("localhost").parseWorkflowStatusesMap(workflowID, new ByteArrayInputStream(str.getBytes()));
        Assert.assertEquals(result.size(), 2);

        ScheduledTime time1 = new ScheduledTime("2014-10-27T14:00:00.000Z");
        ScheduledTime time2 = new ScheduledTime("2014-10-27T15:00:00.000Z");

        SlotState val1 = new SlotState(new SlotID(workflowID, time1), SlotState.Status.SUCCESS, "0029532-141007123109603-oozie-oozi-W", 0);
        SlotState val2 = new SlotState(new SlotID(workflowID, time2), SlotState.Status.FAILURE, "0029595-141007123109603-oozie-oozi-W", 2);

        Assert.assertEquals(Sets.newHashSet(result), Sets.newHashSet(val1, val2));
    }


    @Test
    public void testParseWorkflowIdsList() throws IOException {
        String str =
                "{\n" +
                "  \"ids\" : [ \"GC-export-profiles\", \"GrandCentral-01-harmony\"]" +
                "}";

        Set<WorkflowID> result = new CelosClient("localhost").parseWorkflowIdsList(new ByteArrayInputStream(str.getBytes()));
        Assert.assertEquals(result, Sets.newHashSet(new WorkflowID("GrandCentral-01-harmony"), new WorkflowID("GC-export-profiles")));
    }

}
