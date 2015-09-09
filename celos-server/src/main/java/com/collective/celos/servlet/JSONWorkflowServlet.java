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
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Returns information about the slot states of a single workflow as JSON.
 * 
 * GET /workflow?id=workflow-1
 * ==
 * {
 *   "slots": {
 *     "2013-12-07T13:00:00.000Z": { "status": "RUNNING", "externalID": "237982137-371832798321-W", retryCount: 5 },
 *     "2013-12-07T14:00:00.000Z": { "status": "READY", "externalID": null, retryCount: 0 },
 *     ...
 *   }
 * }
 * 
 * If the "time" parameter is supplied, information is returned about 
 * slot states up to that time.
 */
@SuppressWarnings("serial")
@Deprecated
public class JSONWorkflowServlet extends AbstractJSONServlet {

    private static final String ID_PARAM = "id";

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
            } else {
                ScheduledTime time = getRequestTime(req);
                List<SlotState> slotStates = scheduler.getSlotStates(wf, scheduler.getWorkflowStartTime(wf, time), time);
                ObjectNode object = createJSONObject(slotStates);
                writer.writeValue(res.getOutputStream(), object);
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    ObjectNode createJSONObject(List<SlotState> slotStates) {
        ObjectNode node = mapper.createObjectNode();
        for (SlotState state : slotStates) {
            node.put(state.getScheduledTime().toString(), state.toJSONNode());
        }
        return node;
    }

}
