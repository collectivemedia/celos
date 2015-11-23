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
import com.collective.celos.database.StateDatabaseConnection;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Returns information about the slot states of a single workflow as JSON.
 * 
 * GET /workflow-slots?id=workflow-1
 * ==
 * {
 *   "info": {
 *       "url": "http://myurl",
 *       "contacts": [
 *           { "name": "John Doe", "email": "John.Doe@Gmail.Com"},
 *           { "name": "Jack Smith", "email": "Jack.Smith@Gmail.Com"},
 *       ]
 *   },
 *   "paused": false,
 *   "slots": [
 *      { "time": "2013-12-07T13:00:00.000Z", "status": "RUNNING", "externalID": "237982137-371832798321-W", retryCount: 5 },
 *      { "time": "2013-12-07T14:00:00.000Z", "status": "READY", "externalID": null, retryCount: 0 },
 *     ...
 *   ]
 * }
 * 
 * If the "end" parameter is supplied, information is returned about 
 * slot states up to that time.  Defaults to the current time.
 *
 * If the "start" parameter is supplied, information is returned about 
 * slot states starting at that time.
 * Defaults to the beginning of the sliding window ending at "end".
 */
@SuppressWarnings("serial")
public class JSONWorkflowSlotsServlet extends AbstractJSONServlet {

    private static final String ID_PARAM = "id";
    private static final String INFO_PARAM = "info";
    private static final String PAUSED_PARAM = "paused";
    private static final String SLOTS_PARAM = "slots";
    private static final String START_TIME_PARAM = "start";
    private static final String END_TIME_PARAM = "end";

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException {
        String id = req.getParameter(ID_PARAM);
        try {
            if (id == null) {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST, ID_PARAM + " parameter missing.");
                return;
            }
            Scheduler scheduler = getOrCreateCachedScheduler();
            Workflow wf = scheduler.getWorkflowConfiguration().findWorkflow(new WorkflowID(id));
            if (wf == null) {
                res.sendError(HttpServletResponse.SC_NOT_FOUND, "Workflow not found: " + id);
                return;
            }

            ScheduledTime endTime = getTimeParam(req, END_TIME_PARAM, new ScheduledTime(DateTime.now(DateTimeZone.UTC)));
            ScheduledTime startTime = getTimeParam(req, START_TIME_PARAM, scheduler.getWorkflowStartTime(wf, endTime));

            if (startTime.plusHours(scheduler.getSlidingWindowHours()).getDateTime().isBefore(endTime.getDateTime())) {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Time interval between start and end is limited to: " + scheduler.getSlidingWindowHours() + " hours");
                return;
            }
            try (StateDatabaseConnection connection = getStateDatabase().openConnection()) {
                List<SlotState> slotStates = scheduler.getSlotStates(wf, startTime, endTime, connection);
                List<JsonNode> objectNodes = Lists.newArrayList();
                for (SlotState state : Lists.reverse(slotStates)) {
                    objectNodes.add(state.toJSONNode());
                }

                ObjectNode node = Util.MAPPER.createObjectNode();
                node.put(INFO_PARAM, (JsonNode) Util.MAPPER.valueToTree(wf.getWorkflowInfo()));
                node.put(PAUSED_PARAM, connection.isPaused(wf.getID()));
                node.putArray(SLOTS_PARAM).addAll(objectNodes);
                writer.writeValue(res.getOutputStream(), node);
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private ScheduledTime getTimeParam(HttpServletRequest req, String paramName, ScheduledTime defaultTime) throws Exception {
        String timeParam = req.getParameter(paramName);
        if (timeParam != null) {
            return new ScheduledTime(timeParam);
        }
        return defaultTime;
    }

}
