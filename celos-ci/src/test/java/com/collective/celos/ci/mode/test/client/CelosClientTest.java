package com.collective.celos.ci.mode.test.client;

import com.collective.celos.ScheduledTime;
import com.collective.celos.SlotID;
import com.collective.celos.SlotState;
import com.collective.celos.WorkflowID;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Comparator;
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
    public void testCelosClientFailsNoServer() throws Exception {
        CelosClient celosClient = new CelosClient("http://thereisnosuchhost:1234");
        celosClient.getWorkflowList();
    }


    @Test
    public void testParseWorkflowStatusesMap() throws IOException {

        String str =
                "{ \"workflows\": [\n" +
                "    {\n" +
                "    \"id\": \"id1\",\n" +
                "    \"info\": { \"url\": \"http://myurl\", \"contacts\": [ { \"name\": \"John Doe\", \"email\": \"John.Doe@Gmail.Com\"} ] },\n" +
                "    \"slots\": [\n" +
                "      {\n" +
                "        \"time\" : \"2014-10-27T14:00:00.000Z\",\n" +
                "        \"status\" : \"SUCCESS\",\n" +
                "        \"externalID\" : \"0029532-141007123109603-oozie-oozi-W\",\n" +
                "        \"retryCount\" : 0\n" +
                "      },\n" +
                "      {\n" +
                "        \"time\" : \"2014-10-27T15:00:00.000Z\",\n" +
                "        \"status\" : \"FAILURE\",\n" +
                "        \"externalID\" : \"0029595-141007123109603-oozie-oozi-W\",\n" +
                "        \"retryCount\" : 2\n" +
                "       }" +
                "       ]" +
                "    }," +
                "    {\n" +
                "    \"id\": \"id2\",\n" +
                "    \"info\": { \"url\": \"http://myurl\", \"contacts\": [ { \"name\": \"John Doe\", \"email\": \"John.Doe@Gmail.Com\"} ] },\n" +
                "    \"slots\": [\n" +
                "      {\n" +
                "        \"time\" : \"2014-10-27T14:00:00.000Z\",\n" +
                "        \"status\" : \"SUCCESS\",\n" +
                "        \"externalID\" : \"0029532-141007123109603-oozie-oozi-W\",\n" +
                "        \"retryCount\" : 0\n" +
                "      },\n" +
                "      {\n" +
                "        \"time\" : \"2014-10-27T15:00:00.000Z\",\n" +
                "        \"status\" : \"FAILURE\",\n" +
                "        \"externalID\" : \"0029595-141007123109603-oozie-oozi-W\",\n" +
                "        \"retryCount\" : 2\n" +
                "       }" +
                "       ]" +
                "    }" +
                "   ]" +
                "}";
        WorkflowID workflowID1 = new WorkflowID("id1");
        WorkflowID workflowID2 = new WorkflowID("id2");
        Set<WorkflowStatus> result = new CelosClient("localhost").parseWorkflowStatusesMap(new ByteArrayInputStream(str.getBytes()));
        Assert.assertEquals(result.size(), 2);

        ScheduledTime time1 = new ScheduledTime("2014-10-27T14:00:00.000Z");
        ScheduledTime time2 = new ScheduledTime("2014-10-27T15:00:00.000Z");

        SlotState val1 = new SlotState(new SlotID(workflowID1, time1), SlotState.Status.SUCCESS, "0029532-141007123109603-oozie-oozi-W", 0);
        SlotState val2 = new SlotState(new SlotID(workflowID1, time2), SlotState.Status.FAILURE, "0029595-141007123109603-oozie-oozi-W", 2);

        SlotState val3 = new SlotState(new SlotID(workflowID2, time1), SlotState.Status.SUCCESS, "0029532-141007123109603-oozie-oozi-W", 0);
        SlotState val4 = new SlotState(new SlotID(workflowID2, time2), SlotState.Status.FAILURE, "0029595-141007123109603-oozie-oozi-W", 2);


        List<WorkflowStatus> resultAsList = Lists.newArrayList(result);

        resultAsList.sort(new Comparator<WorkflowStatus>() {
            @Override
            public int compare(WorkflowStatus o1, WorkflowStatus o2) {
                return o1.getId().toString().compareTo(o2.getId().toString());
            }
        });

        List<Set<SlotState>> expectedSlots = Lists.newArrayList();
        expectedSlots.add(Sets.newHashSet(val1, val2));
        expectedSlots.add(Sets.newHashSet(val3, val4));

        for (int i = 0; i < resultAsList.size(); i++) {
            Assert.assertEquals(Sets.newHashSet(resultAsList.get(i).getSlotStates()), expectedSlots.get(i));
        }
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
