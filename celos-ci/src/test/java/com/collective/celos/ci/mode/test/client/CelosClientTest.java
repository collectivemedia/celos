package com.collective.celos.ci.mode.test.client;

import com.collective.celos.ScheduledTime;
import com.collective.celos.SlotState;
import com.collective.celos.WorkflowID;
import com.collective.celos.ci.mode.test.client.CelosClient;
import com.collective.celos.ci.mode.test.client.ExternalWorkflowStatus;
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

        Map<ScheduledTime, ExternalWorkflowStatus> result = client.parseWorkflowStatusesMap(new ByteArrayInputStream(str.getBytes()));
        Assert.assertEquals(result.size(), 2);

        ScheduledTime key1 = new ScheduledTime("2014-10-27T14:00:00.000Z");
        ScheduledTime key2 = new ScheduledTime("2014-10-27T15:00:00.000Z");
        Assert.assertEquals(result.keySet(), Sets.newHashSet(key1, key2));

        ExternalWorkflowStatus val1 = new ExternalWorkflowStatus(SlotState.Status.SUCCESS, "0029532-141007123109603-oozie-oozi-W", 0);
        ExternalWorkflowStatus val2 = new ExternalWorkflowStatus(SlotState.Status.FAILURE, "0029595-141007123109603-oozie-oozi-W", 2);

        Assert.assertEquals(result.get(key1), val1);
        Assert.assertEquals(result.get(key2), val2);
    }
}
