package com.collective.celos.ci.mode.test.client;

import com.collective.celos.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.*;

/**
 * Created by akonopko on 10.12.14.
 */
public class CelosClient {

    private static final String SCHEDULER_PATH = "/scheduler";
    private static final String RERUN_PATH = "/rerun";
    private static final String SLOT_STATE_PATH = "/slot-state";
    private static final String CLEAR_CACHE_PATH = "/clear-cache";
    private static final String WORKFLOW_LIST_PATH = "/workflow-list";
    private static final String WORKFLOW_PATH = "/workflow-slots";
    private static final String TIME_PARAM = "time";
    private static final String ID_PARAM = "id";
    private static final String IDS_PARAM = "ids";

    private final HttpClient client;
    private final ScheduledTimeFormatter timeFormatter;
    private final URI address;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public CelosClient(String address) {
        this.address = URI.create(address);
        this.client = new DefaultHttpClient();
        this.timeFormatter = new ScheduledTimeFormatter();
    }

    public URI getAddress() {
        return address;
    }

    public Set<WorkflowID> getWorkflowList() throws Exception {
        URIBuilder uriBuilder = new URIBuilder(address);
        uriBuilder.setPath(WORKFLOW_LIST_PATH);

        HttpGet workflowListGet = new HttpGet(uriBuilder.build());
        HttpResponse getResponse = execute(workflowListGet);
        InputStream content = getResponse.getEntity().getContent();
        return parseWorkflowIdsList(content);
    }

    public WorkflowStatus getWorkflowStatus(WorkflowID workflowID, ScheduledTime scheduledTime) throws Exception {

        URIBuilder uriBuilder = new URIBuilder(address);
        uriBuilder.setPath(WORKFLOW_PATH);
        if (scheduledTime != null) {
            uriBuilder.addParameter(TIME_PARAM, timeFormatter.formatPretty(scheduledTime));
        }
        uriBuilder.addParameter(ID_PARAM, workflowID.toString());
        URI uri = uriBuilder.build();

        HttpGet workflowListGet = new HttpGet(uri);
        HttpResponse getResponse = execute(workflowListGet);
        InputStream content = getResponse.getEntity().getContent();
        return parseWorkflowStatusesMap(workflowID, content);
    }

    public WorkflowStatus getWorkflowStatus(WorkflowID workflowID) throws Exception {
        return getWorkflowStatus(workflowID, null);
    }

    public void iterateScheduler(ScheduledTime scheduledTime) throws Exception {
        iterateScheduler(scheduledTime, Collections.<WorkflowID>emptySet());
    }

    public void iterateScheduler(ScheduledTime scheduledTime, Set<WorkflowID> workflowIDs) throws Exception {
        URIBuilder uriBuilder = new URIBuilder(address);
        uriBuilder.setPath(SCHEDULER_PATH);
        if (!workflowIDs.isEmpty()) {
            uriBuilder.addParameter(IDS_PARAM, StringUtils.join(workflowIDs, ","));
        }
        uriBuilder.addParameter(TIME_PARAM, timeFormatter.formatPretty(scheduledTime));
        executePost(uriBuilder.build());
    }

    public void clearCache() throws Exception {
        URIBuilder uriBuilder = new URIBuilder(address);
        uriBuilder.setPath(CLEAR_CACHE_PATH);
        executePost(uriBuilder.build());
    }

    public SlotState getSlotState(WorkflowID workflowID, ScheduledTime scheduledTime) throws Exception {
        URIBuilder uriBuilder = new URIBuilder(address);
        uriBuilder.setPath(SLOT_STATE_PATH);
        uriBuilder.addParameter(ID_PARAM, workflowID.toString());
        uriBuilder.addParameter(TIME_PARAM, scheduledTime.toString());

        HttpGet workflowListGet = new HttpGet(uriBuilder.build());
        HttpResponse getResponse = execute(workflowListGet);
        InputStream content = getResponse.getEntity().getContent();
        return SlotState.fromJSONNode(workflowID, objectMapper.readValue(content, ObjectNode.class));
    }

    public void rerunSlot(WorkflowID workflowID, ScheduledTime scheduledTime) throws Exception {
        URIBuilder uriBuilder = new URIBuilder(address);
        uriBuilder.setPath(RERUN_PATH);
        uriBuilder.addParameter(ID_PARAM, workflowID.toString());
        uriBuilder.addParameter(TIME_PARAM, scheduledTime.toString());
        executePost(uriBuilder.build());
    }

    private void executePost(URI request) throws IOException {
        HttpPost post = new HttpPost(request);
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

    Set<WorkflowID> parseWorkflowIdsList(InputStream content) throws IOException {
        return objectMapper.readValue(content, WorkflowList.class).getIds();
    }

    private static final String INFO_NODE = "info";
    private static final String SLOTS_NODE = "slots";

    WorkflowStatus parseWorkflowStatusesMap(WorkflowID workflowID, InputStream content) throws IOException {
        JsonNode node = objectMapper.readValue(content, JsonNode.class);
        WorkflowInfo info = objectMapper.treeToValue(node.get(INFO_NODE), WorkflowInfo.class);

        Iterator<JsonNode> elems = node.get(SLOTS_NODE).elements();
        List<SlotState> result = Lists.newArrayList();
        while (elems.hasNext()) {
            result.add(SlotState.fromJSONNode(workflowID, elems.next()));
        }
        return new WorkflowStatus(info, result);
    }

}
