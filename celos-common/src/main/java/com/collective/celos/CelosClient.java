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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Utility class to talk to the Celos server HTTP API.
 */
public class CelosClient {

    private static final String SCHEDULER_PATH = "/scheduler";
    private static final String RERUN_PATH = "/rerun";
    private static final String SLOT_STATE_PATH = "/slot-state";
    private static final String CLEAR_CACHE_PATH = "/clear-cache";
    private static final String WORKFLOW_LIST_PATH = "/workflow-list";
    private static final String WORKFLOW_SLOTS_PATH = "/workflow-slots";
    private static final String START_TIME_PARAM = "start";
    private static final String END_TIME_PARAM = "end";
    private static final String TIME_PARAM = "time";
    private static final String ID_PARAM = "id";
    private static final String IDS_PARAM = "ids";

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

    public WorkflowStatus getWorkflowStatus(WorkflowID workflowID, ZonedDateTime startTime, ZonedDateTime endTime) throws Exception {

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

    public WorkflowStatus getWorkflowStatus(WorkflowID workflowID, ZonedDateTime endTime) throws Exception {
        return getWorkflowStatus(workflowID, null, endTime);
    }

    public WorkflowStatus getWorkflowStatus(WorkflowID workflowID) throws Exception {
        return getWorkflowStatus(workflowID, null, null);
    }

    public void iterateScheduler() throws Exception {
        iterateScheduler(Util.zonedDateTimeNowUTC());
    }

    public void iterateScheduler(ZonedDateTime scheduledTime) throws Exception {
        iterateScheduler(scheduledTime, Collections.<WorkflowID>emptySet());
    }

    public void iterateScheduler(ZonedDateTime scheduledTime, Set<WorkflowID> workflowIDs) throws Exception {
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

    public SlotState getSlotState(WorkflowID workflowID, ZonedDateTime scheduledTime) throws Exception {
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
        rerunSlot(slotID.getWorkflowID(), slotID.getScheduledDateTime());
    }
    
    public void rerunSlot(WorkflowID workflowID, ZonedDateTime scheduledTime) throws Exception {
        URIBuilder uriBuilder = new URIBuilder(address);
        uriBuilder.setPath(uriBuilder.getPath() + RERUN_PATH);
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

    WorkflowStatus parseWorkflowStatus(WorkflowID workflowID, InputStream content) throws IOException {
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
