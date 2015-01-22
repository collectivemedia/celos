package com.collective.celos.ci.mode.test.client;

import com.collective.celos.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

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

    public Set<WorkflowID> getWorkflowList() throws IOException {
        HttpResponse getResponse = execute(workflowListGet);
        InputStream content = getResponse.getEntity().getContent();
        return parseWorkflowIdsList(content);
    }

    public List<SlotState> getWorkflowStatus(WorkflowID workflowID) throws IOException {
        return getWorkflowStatus(workflowID, null);
    }

    public List<SlotState> getWorkflowStatus(WorkflowID workflowID, ScheduledTime scheduledTime) throws IOException {
        String timeQuery = "";
        if (scheduledTime != null) {
            timeQuery = "&time=" + timeFormatter.formatPretty(scheduledTime);
        }
        HttpGet workflowListGet = new HttpGet(address + "/workflow?id=" + workflowID + timeQuery);
        HttpResponse getResponse = execute(workflowListGet);
        InputStream content = getResponse.getEntity().getContent();
        return parseWorkflowStatusesMap(workflowID, content);
    }

    public void iterateScheduler(ScheduledTime scheduledTime) throws IOException {
        iterateScheduler(scheduledTime, Collections.<WorkflowID>emptySet());
    }

    public void iterateScheduler(ScheduledTime scheduledTime, Set<WorkflowID> workflowIDs) throws IOException {
        String workflowIdStr;
        if (!workflowIDs.isEmpty()) {
            workflowIdStr = "&ids=" + StringUtils.join(workflowIDs, ",");
        } else {
            workflowIdStr = "";
        }
        HttpPost post = new HttpPost(address + "/scheduler?time=" + timeFormatter.formatPretty(scheduledTime) + workflowIdStr);
        HttpResponse postResponse = execute(post);
        EntityUtils.consume(postResponse.getEntity());
    }

    private HttpResponse execute(HttpUriRequest request) throws IOException {
        HttpResponse getResponse = client.execute(request);
        if (errorResponse(getResponse)) {
            throw new IOException(getResponse.getStatusLine().toString());
        }
        return getResponse;
    }

    private boolean errorResponse(HttpResponse getResponse) {
        return getResponse.getStatusLine() != null && getResponse.getStatusLine().getStatusCode() != 200;
    }

    private static class WorkflowList {
        private Set<WorkflowID> ids;

        public Set<WorkflowID> getIds() {
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


    Set<WorkflowID> parseWorkflowIdsList(InputStream content) throws IOException {
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

}
