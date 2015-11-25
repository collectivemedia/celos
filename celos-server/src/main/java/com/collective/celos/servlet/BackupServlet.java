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
package com.collective.celos.servlet;

import com.collective.celos.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@SuppressWarnings("serial")
public class BackupServlet extends AbstractJSONServlet {

    private static final String ID_PARAM = "id";
    private static final String PAUSED_PARAM = "paused";
    private static final String SLOTS_PARAM = "slots";
    private static final String START_TIME_PARAM = "start";
    private static final String WORKFLOWS_PARAM = "workflows";

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException {
        try {
            Scheduler scheduler = getOrCreateCachedScheduler();

            String startTimeParam = req.getParameter(START_TIME_PARAM);
            if (startTimeParam == null) {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST, START_TIME_PARAM + " parameter missing.");
                return;
            }

            ScheduledTime startTime = new ScheduledTime(startTimeParam);
            ScheduledTime endTime = ScheduledTime.now();

            try (StateDatabaseConnection connection = getStateDatabase().openConnection()) {
                ArrayNode list = Util.MAPPER.createArrayNode();
                scheduler.getWorkflowConfiguration().getWorkflows().stream().forEach( wf -> list.add(getWorkflowNode(scheduler, wf, startTime, endTime, connection)));
                ObjectNode node = Util.MAPPER.createObjectNode();
                node.put(WORKFLOWS_PARAM, list);
                writer.writeValue(res.getOutputStream(), node);
            }

        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try {
            try (StateDatabaseConnection connection = getStateDatabase().openConnection()) {
                JsonNode value = Util.JSON_READER.readTree(new InputStreamReader(req.getInputStream(), StandardCharsets.UTF_8));
                ArrayNode workflows = (ArrayNode) value.get(WORKFLOWS_PARAM);
                for (JsonNode node : workflows) {
                    putWorkflowNode(node, connection);
                }
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private void putWorkflowNode(JsonNode node, StateDatabaseConnection connection) throws Exception {
        WorkflowID workflowID = new WorkflowID(node.get(ID_PARAM).textValue());
        Boolean paused = node.get(PAUSED_PARAM).booleanValue();
        for (JsonNode jsonNode : node.get(SLOTS_PARAM)) {
            connection.putSlotState(SlotState.fromJSONNode(workflowID, jsonNode));
        }
        connection.setPaused(workflowID, paused);
    }

    private ObjectNode getWorkflowNode(Scheduler scheduler, Workflow wf, ScheduledTime startTime, ScheduledTime endTime, StateDatabaseConnection connection) {
        startTime = wf.getStartTime().getDateTime().isAfter(startTime.getDateTime()) ? wf.getStartTime() : startTime;
        try {
            ObjectNode node = Util.MAPPER.createObjectNode();
            List<SlotState> slotStates = scheduler.getSlotStates(wf, startTime, endTime, connection);
            List<JsonNode> objectNodes = Lists.newArrayList();
            for (SlotState state : Lists.reverse(slotStates)) {
                objectNodes.add(state.toJSONNode());
            }
            node.put(ID_PARAM, wf.getID().toString());
            node.put(PAUSED_PARAM, connection.isPaused(wf.getID()));
            node.putArray(SLOTS_PARAM).addAll(objectNodes);
            return node;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
