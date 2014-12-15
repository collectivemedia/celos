package com.collective.celos.ci.mode.test.client;

import com.collective.celos.ScheduledTime;
import com.collective.celos.SlotID;
import com.collective.celos.SlotState;
import com.collective.celos.WorkflowID;
import com.google.common.collect.Sets;
import junit.framework.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by akonopko on 10.12.14.
 */
public class CelosClientTest {

    @Test
    public void testParseWorkflowIdsList() throws IOException {
        CelosClient client = new CelosClient(null);

        String str = "{\n" +
                "  \"ids\" : [ \"GC-export-profiles\", \"GrandCentral-01-harmony\"]" +
                "}";

        List<WorkflowID> result = client.parseWorkflowIdsList(new ByteArrayInputStream(str.getBytes()));
        Assert.assertEquals(result.size(), 2);
        Assert.assertEquals(result.get(0), new WorkflowID("GC-export-profiles"));
        Assert.assertEquals(result.get(1), new WorkflowID("GrandCentral-01-harmony"));
    }

    @Test
    public void testParseWorkflowStatusesMap() throws IOException {
        CelosClient client = new CelosClient(null);

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
        List<SlotState> result = client.parseWorkflowStatusesMap(workflowID, new ByteArrayInputStream(str.getBytes()));
        Assert.assertEquals(result.size(), 2);

        ScheduledTime time1 = new ScheduledTime("2014-10-27T14:00:00.000Z");
        ScheduledTime time2 = new ScheduledTime("2014-10-27T15:00:00.000Z");

        SlotState val1 = new SlotState(new SlotID(workflowID, time1), SlotState.Status.SUCCESS, "0029532-141007123109603-oozie-oozi-W", 0);
        SlotState val2 = new SlotState(new SlotID(workflowID, time2), SlotState.Status.FAILURE, "0029595-141007123109603-oozie-oozi-W", 2);

        Assert.assertEquals(Sets.newHashSet(result), Sets.newHashSet(val1, val2));
    }
}
