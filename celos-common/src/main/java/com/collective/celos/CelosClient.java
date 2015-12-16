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
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

/**
 * Utility class to talk to the Celos server HTTP API.
 */
public class CelosClient {

    private static final String LIST_REGISTER_KEYS_PATH = "/list-register-keys";
    private static final String SCHEDULER_PATH = "/scheduler";
    private static final String RERUN_PATH = "/rerun";
    private static final String KILL_PATH = "/kill";
    private static final String PAUSE_PATH = "/pause";
    private static final String SLOT_STATE_PATH = "/slot-state";
    private static final String TRIGGER_STATUS_PATH = "/trigger-status";
    private static final String CLEAR_CACHE_PATH = "/clear-cache";
    private static final String WORKFLOW_LIST_PATH = "/workflow-list";
    private static final String WORKFLOW_SLOTS_PATH = "/workflow-slots";
    private static final String REGISTER_PATH = "/register";

    public static final String START_TIME_PARAM = "start";
    public static final String END_TIME_PARAM = "end";
    public static final String TIME_PARAM = "time";
    public static final String ID_PARAM = "id";
    public static final String IDS_PARAM = "ids";
    public static final String KEY_PARAM = "key";
    public static final String BUCKET_PARAM = "bucket";
    public static final String PREFIX_PARAM = "prefix";

    public static final String KEYS_NODE = "keys";
    public static final String PAUSE_NODE = "paused";
    public static final String INFO_NODE = "info";
    public static final String SLOTS_NODE = "slots";

    private final HttpClient client;
    private final ScheduledTimeFormatter timeFormatter;
    private final URI address;

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
        return SlotState.fromJSONNode(workflowID, Util.JSON_READER.withType(ObjectNode.class).readValue(content));
    }

    public final static ObjectMapper MAPPER = new ObjectMapper();

    public JsonNode getTriggerStatusAsText(String workflowID, String scheduledTime) throws Exception {
        URIBuilder uriBuilder = new URIBuilder(address);
        uriBuilder.setPath(uriBuilder.getPath() + TRIGGER_STATUS_PATH);
        uriBuilder.addParameter(ID_PARAM, workflowID);
        uriBuilder.addParameter(TIME_PARAM, scheduledTime);

        HttpGet workflowListGet = new HttpGet(uriBuilder.build());
        HttpResponse getResponse = execute(workflowListGet);
        InputStream content = getResponse.getEntity().getContent();
        return MAPPER.readTree(content);
    }

    public void rerunSlot(SlotID slotID) throws Exception {
        rerunSlot(slotID.getWorkflowID(), slotID.getScheduledTime());
    }
    
    public void rerunSlot(WorkflowID workflowID, ScheduledTime scheduledTime) throws Exception {
        URIBuilder uriBuilder = new URIBuilder(address);
        uriBuilder.setPath(uriBuilder.getPath() + RERUN_PATH);
        uriBuilder.addParameter(ID_PARAM, workflowID.toString());
        uriBuilder.addParameter(TIME_PARAM, scheduledTime.toString());
        executePost(uriBuilder.build());
    }

    public void setWorkflowPaused(WorkflowID workflowID, Boolean paused) throws Exception {
        URIBuilder uriBuilder = new URIBuilder(address);
        uriBuilder.setPath(uriBuilder.getPath() + PAUSE_PATH);
        uriBuilder.addParameter(ID_PARAM, workflowID.toString());
        uriBuilder.addParameter(PAUSE_NODE, paused.toString());
        executePost(uriBuilder.build());
    }

    public void kill(WorkflowID workflowID, ScheduledTime scheduledTime) throws Exception {
        URIBuilder uriBuilder = new URIBuilder(address);
        uriBuilder.setPath(uriBuilder.getPath() + KILL_PATH);
        uriBuilder.addParameter(ID_PARAM, workflowID.toString());
        uriBuilder.addParameter(TIME_PARAM, scheduledTime.toString());
        executePost(uriBuilder.build());
    }
    
    //// Registers
    
    /**
     * Returns the specified register value, or null if not found.
     */
    public JsonNode getRegister(BucketID bucket, RegisterKey key) throws Exception {
        URIBuilder uriBuilder = new URIBuilder(address);
        uriBuilder.setPath(uriBuilder.getPath() + REGISTER_PATH);
        uriBuilder.addParameter(BUCKET_PARAM, bucket.toString());
        uriBuilder.addParameter(KEY_PARAM, key.toString());

        HttpResponse res = client.execute(new HttpGet(uriBuilder.build()));
        try {
            switch(res.getStatusLine().getStatusCode()) {
            case HttpServletResponse.SC_NOT_FOUND:
                return null;
            case HttpServletResponse.SC_OK:
                return Util.JSON_READER.readTree(res.getEntity().getContent());
            default:
                throw new Exception(res.getStatusLine().toString());
            }
        } finally {
            EntityUtils.consume(res.getEntity());
        }
    }
    
    /**
     * Sets the specified register value.
     */
    public void putRegister(BucketID bucket, RegisterKey key, JsonNode value) throws Exception {
        URIBuilder uriBuilder = new URIBuilder(address);
        uriBuilder.setPath(uriBuilder.getPath() + REGISTER_PATH);
        uriBuilder.addParameter(BUCKET_PARAM, bucket.toString());
        uriBuilder.addParameter(KEY_PARAM, key.toString());
        executePut(uriBuilder.build(), new StringEntity(Util.JSON_WRITER.writeValueAsString(value), StandardCharsets.UTF_8));
    }
    
    /**
     * Deletes the specified register value.
     */
    public void deleteRegister(BucketID bucket, RegisterKey key) throws Exception {
        URIBuilder uriBuilder = new URIBuilder(address);
        uriBuilder.setPath(uriBuilder.getPath() + REGISTER_PATH);
        uriBuilder.addParameter(BUCKET_PARAM, bucket.toString());
        uriBuilder.addParameter(KEY_PARAM, key.toString());
        executeDelete(uriBuilder.build());
    }

    public void deleteRegistersWithPrefix(BucketID bucket, String prefix) throws Exception {
        URIBuilder uriBuilder = new URIBuilder(address);
        uriBuilder.setPath(uriBuilder.getPath() + REGISTER_PATH);
        uriBuilder.addParameter(BUCKET_PARAM, bucket.toString());
        uriBuilder.addParameter(PREFIX_PARAM, prefix);
        executeDelete(uriBuilder.build());
    }

    public List<RegisterKey> getRegisterKeys(BucketID bucket) throws Exception {
        return getRegisterKeys(bucket, null);
    }

    public List<RegisterKey> getRegisterKeys(BucketID bucket, String prefix) throws Exception {
        URIBuilder uriBuilder = new URIBuilder(address);
        uriBuilder.setPath(uriBuilder.getPath() + LIST_REGISTER_KEYS_PATH);
        uriBuilder.addParameter(BUCKET_PARAM, bucket.toString());
        if (!StringUtils.isEmpty(prefix)) {
            uriBuilder.addParameter(PREFIX_PARAM, prefix);
        }
        InputStream contentStream = execute(new HttpGet(uriBuilder.build())).getEntity().getContent();
        return parseKeyList(contentStream);
    }

    private void executePost(URI request) throws IOException {
        executeAndConsume(new HttpPost(request));
    }
    
    private void executePut(URI request, HttpEntity entity) throws IOException {
        HttpPut put = new HttpPut(request);
        put.setEntity(entity);
        executeAndConsume(put);
    }
    
    private void executeDelete(URI request) throws IOException {
        executeAndConsume(new HttpDelete(request));
    }

    private void executeAndConsume(HttpUriRequest msg) throws IOException {
        HttpResponse postResponse = execute(msg);
        EntityUtils.consume(postResponse.getEntity());
    }

    private HttpResponse execute(HttpUriRequest request) throws IOException {
        HttpResponse getResponse = client.execute(request);
        if (errorResponse(getResponse)) {
            EntityUtils.consume(getResponse.getEntity());
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

    private static class KeyList {
        private List<RegisterKey> keys;

        public List<RegisterKey> getKeys() {
            return keys;
        }
    }

    List<RegisterKey> parseKeyList(InputStream content) throws IOException {
        return Util.JSON_READER.withType(KeyList.class).<KeyList>readValue(content).getKeys();
    }

    Set<WorkflowID> parseWorkflowIdsList(InputStream content) throws IOException {
        return Util.JSON_READER.withType(WorkflowList.class).<WorkflowList>readValue(content).getIds();
    }
    
    WorkflowStatus parseWorkflowStatus(WorkflowID workflowID, InputStream content) throws IOException {
        JsonNode node = Util.JSON_READER.withType(JsonNode.class).readValue(content);
        WorkflowInfo info = Util.JSON_READER.treeToValue(node.get(INFO_NODE), WorkflowInfo.class);
        Boolean paused = node.get(PAUSE_NODE).asBoolean();

        Iterator<JsonNode> elems = node.get(SLOTS_NODE).elements();
        List<SlotState> result = Lists.newArrayList();
        while (elems.hasNext()) {
            result.add(SlotState.fromJSONNode(workflowID, elems.next()));
        }
        return new WorkflowStatus(info, result, paused);
    }

}
