package com.collective.celos.ci.mode.test.client;

import com.collective.celos.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by akonopko on 10.12.14.
 */
public class CelosClient {

    private final HttpClient client;
    private final ScheduledTimeFormatter timeFormatter;
    private final String address;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpGet workflowListGet;

    public CelosClient(String address) {
        this.address = address;
        this.client = new DefaultHttpClient();
        this.timeFormatter = new ScheduledTimeFormatter();
        this.workflowListGet = new HttpGet(address + "/workflow-list");
    }

    public List<WorkflowID> getWorkflowList() throws IOException {
        HttpResponse getResponse = client.execute(workflowListGet);
        InputStream content = getResponse.getEntity().getContent();
        return parseWorkflowIdsList(content);
    }

    public List<SlotState> getWorkflowStatus(WorkflowID workflowID) throws IOException {
        return getWorkflowStatus(workflowID, null);
    }

    public List<SlotState> getWorkflowStatus(WorkflowID workflowID, ScheduledTime scheduledTime) throws IOException {
        String timeQuery = "";
        if (scheduledTime != null) {
            timeQuery = "&time=" + getTimeInclusiveRange(scheduledTime);
        }
        HttpGet workflowListGet = new HttpGet(address + "/workflow?id=" + workflowID + timeQuery);
        HttpResponse getResponse = client.execute(workflowListGet);
        InputStream content = getResponse.getEntity().getContent();

        return parseWorkflowStatusesMap(workflowID, content);
    }

    public void iterateScheduler(ScheduledTime schedTime) throws IOException {
        String schedTimeStr = getTimeInclusiveRange(schedTime);
        HttpPost post = new HttpPost(address + "/scheduler?time=" + schedTimeStr);
        HttpResponse postResponse = client.execute(post);
        EntityUtils.consume(postResponse.getEntity());
    }

    private static class WorkflowList {
        private List<WorkflowID> ids;

        public List<WorkflowID> getIds() {
            return ids;
        }
    }

    private static class SlotStateDTO {

        SlotState.Status status;
        String externalID;
        Integer retryCount;

        public SlotState.Status getStatus() {
            return status;
        }

        public String getExternalID() {
            return externalID;
        }

        public Integer getRetryCount() {
            return retryCount;
        }
    }


    List<WorkflowID> parseWorkflowIdsList(InputStream content) throws IOException {
        return objectMapper.readValue(content, WorkflowList.class).getIds();
    }

    List<SlotState> parseWorkflowStatusesMap(WorkflowID workflowID, InputStream content) throws IOException {
        JsonNode responseTree = objectMapper.readTree(content);
        Iterator<Map.Entry<String, JsonNode>> iterator = responseTree.getFields();
        List<SlotState> result = Lists.newArrayList();
        while (iterator.hasNext()) {
            Map.Entry<String, JsonNode> entry = iterator.next();
            ScheduledTime itemSchedTime = new ScheduledTime(entry.getKey());
            SlotStateDTO ss = objectMapper.readValue(entry.getValue(), SlotStateDTO.class);
            SlotState slotState = new SlotState(new SlotID(workflowID, itemSchedTime), ss.getStatus(), ss.getExternalID(), ss.getRetryCount());
            result.add(slotState);
        }
        return result;
    }

    private String getTimeInclusiveRange(ScheduledTime schedTime) {
        //add one second to include last slot in the time period
        return timeFormatter.formatPretty(schedTime.plusSeconds(1));
    }

}
