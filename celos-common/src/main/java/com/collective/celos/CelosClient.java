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
 * Utility class to talk to the Celos server HTTP API.
 */
public class CelosClient {

    private static final String SCHEDULER_PATH = "/scheduler";
    private static final String RERUN_PATH = "/rerun";
    private static final String KILL_PATH = "/kill";
    private static final String PAUSE_PATH = "/pause";
    private static final String SLOT_STATE_PATH = "/slot-state";
    private static final String CLEAR_CACHE_PATH = "/clear-cache";
    private static final String WORKFLOW_LIST_PATH = "/workflow-list";
    private static final String WORKFLOW_SLOTS_PATH = "/workflow-slots";

    public static final String START_TIME_PARAM = "start";
    public static final String END_TIME_PARAM = "end";
    public static final String TIME_PARAM = "time";
    public static final String ID_PARAM = "id";
    public static final String IDS_PARAM = "ids";
    public static final String RERUN_DOWNSTREAM_PARAM = "rerunDownstream";
    public static final String RERUN_UPSTREAM_PARAM = "rerunUpstream";
    public static final String WORKFLOW_SLOTS_INFO_NODE = "info";
    public static final String WORKFLOW_SLOTS_SLOTS_NODE = "slots";
    public static final String WORKFLOW_SLOTS_PAUSED_NODE = "paused";

    private final HttpClient client;
    private final ScheduledTimeFormatter timeFormatter;
    private final URI address;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public CelosClient(URI address) {
        this.address = Util.requireNonNull(address);
        this.client = new DefaultHttpClient();
        this.timeFormatter = new ScheduledTimeFormatter();
    }

    public URI getAddress() {
        return address;
    }

    public Set<WorkflowID> getWorkflowList() throws Exception {
        URIBuilder uriBuilder = new URIBuilder(address);
        uriBuilder.setPath(uriBuilder.getPath() + WORKFLOW_LIST_PATH);

        HttpGet workflowListGet = new HttpGet(uriBuilder.build());
        HttpResponse getResponse = execute(workflowListGet);
        InputStream content = getResponse.getEntity().getContent();
        return parseWorkflowIdsList(content);
    }

    public WorkflowStatus getWorkflowStatus(WorkflowID workflowID, ScheduledTime startTime, ScheduledTime endTime) throws Exception {

        URIBuilder uriBuilder = new URIBuilder(address);
        uriBuilder.setPath(uriBuilder.getPath() + WORKFLOW_SLOTS_PATH);
        if (endTime != null) {
            uriBuilder.addParameter(END_TIME_PARAM, timeFormatter.formatPretty(endTime));
        }
        if (startTime != null) {
            uriBuilder.addParameter(START_TIME_PARAM, timeFormatter.formatPretty(startTime));
        }
        uriBuilder.addParameter(ID_PARAM, workflowID.toString());
        URI uri = uriBuilder.build();

        HttpGet workflowListGet = new HttpGet(uri);
        HttpResponse getResponse = execute(workflowListGet);
        InputStream content = getResponse.getEntity().getContent();
        return parseWorkflowStatus(workflowID, content);
    }

    public WorkflowStatus getWorkflowStatus(WorkflowID workflowID, ScheduledTime endTime) throws Exception {
        return getWorkflowStatus(workflowID, null, endTime);
    }

    public WorkflowStatus getWorkflowStatus(WorkflowID workflowID) throws Exception {
        return getWorkflowStatus(workflowID, null, null);
    }

    public void iterateScheduler() throws Exception {
        iterateScheduler(ScheduledTime.now());
    }

    public void iterateScheduler(ScheduledTime scheduledTime) throws Exception {
        iterateScheduler(scheduledTime, Collections.<WorkflowID>emptySet());
    }

    public void iterateScheduler(ScheduledTime scheduledTime, Set<WorkflowID> workflowIDs) throws Exception {
        URIBuilder uriBuilder = new URIBuilder(address);
        uriBuilder.setPath(uriBuilder.getPath() + SCHEDULER_PATH);
        if (!workflowIDs.isEmpty()) {
            uriBuilder.addParameter(IDS_PARAM, StringUtils.join(workflowIDs, ","));
        }
        uriBuilder.addParameter(TIME_PARAM, timeFormatter.formatPretty(scheduledTime));
        executePost(uriBuilder.build());
    }

    public void clearCache() throws Exception {
        URIBuilder uriBuilder = new URIBuilder(address);
        uriBuilder.setPath(uriBuilder.getPath() + CLEAR_CACHE_PATH);
        executePost(uriBuilder.build());
    }

    public SlotState getSlotState(WorkflowID workflowID, ScheduledTime scheduledTime) throws Exception {
        URIBuilder uriBuilder = new URIBuilder(address);
        uriBuilder.setPath(uriBuilder.getPath() + SLOT_STATE_PATH);
        uriBuilder.addParameter(ID_PARAM, workflowID.toString());
        uriBuilder.addParameter(TIME_PARAM, scheduledTime.toString());

        HttpGet workflowListGet = new HttpGet(uriBuilder.build());
        HttpResponse getResponse = execute(workflowListGet);
        InputStream content = getResponse.getEntity().getContent();
        return SlotState.fromJSONNode(workflowID, objectMapper.readValue(content, ObjectNode.class));
    }

    public void rerunSlot(SlotID slotID) throws Exception {
        rerunSlot(slotID, false, false);
    }

    public void rerunSlot(SlotID slotID, boolean rerunUpstream, boolean rerunDownstream) throws Exception {
        rerunSlot(slotID.getWorkflowID(), slotID.getScheduledTime(), rerunUpstream, rerunDownstream);
    }

    public void rerunSlot(WorkflowID workflowID, ScheduledTime scheduledTime) throws Exception {
        rerunSlot(workflowID, scheduledTime, false, false);
    }

    public void rerunSlot(WorkflowID workflowID, ScheduledTime scheduledTime, boolean rerunUpstream, boolean rerunDownstream) throws Exception {
        URIBuilder uriBuilder = new URIBuilder(address);
        uriBuilder.setPath(uriBuilder.getPath() + RERUN_PATH);
        uriBuilder.addParameter(ID_PARAM, workflowID.toString());
        uriBuilder.addParameter(TIME_PARAM, scheduledTime.toString());
        if (rerunDownstream) {
            uriBuilder.addParameter(RERUN_DOWNSTREAM_PARAM, Boolean.TRUE.toString());
        }
        if (rerunUpstream) {
            uriBuilder.addParameter(RERUN_UPSTREAM_PARAM, Boolean.TRUE.toString());
        }
        executePost(uriBuilder.build());
    }

    public void setWorkflowPaused(WorkflowID workflowID, Boolean paused) throws Exception {
        URIBuilder uriBuilder = new URIBuilder(address);
        uriBuilder.setPath(uriBuilder.getPath() + PAUSE_PATH);
        uriBuilder.addParameter(ID_PARAM, workflowID.toString());
        uriBuilder.addParameter(WORKFLOW_SLOTS_PAUSED_NODE, paused.toString());
        executePost(uriBuilder.build());
    }

    public void kill(WorkflowID workflowID, ScheduledTime scheduledTime) throws Exception {
        URIBuilder uriBuilder = new URIBuilder(address);
        uriBuilder.setPath(uriBuilder.getPath() + KILL_PATH);
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

    WorkflowStatus parseWorkflowStatus(WorkflowID workflowID, InputStream content) throws IOException {
        JsonNode node = objectMapper.readValue(content, JsonNode.class);
        WorkflowInfo info = objectMapper.treeToValue(node.get(WORKFLOW_SLOTS_INFO_NODE), WorkflowInfo.class);
        Boolean paused = node.get(WORKFLOW_SLOTS_PAUSED_NODE).asBoolean();

        Iterator<JsonNode> elems = node.get(WORKFLOW_SLOTS_SLOTS_NODE).elements();
        List<SlotState> result = Lists.newArrayList();
        while (elems.hasNext()) {
            result.add(SlotState.fromJSONNode(workflowID, elems.next()));
        }
        return new WorkflowStatus(info, result, paused);
    }

}
