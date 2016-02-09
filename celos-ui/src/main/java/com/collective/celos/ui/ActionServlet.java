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
package com.collective.celos.ui;

import com.collective.celos.CelosClient;
import com.collective.celos.ScheduledTime;
import com.collective.celos.Util;
import com.collective.celos.WorkflowID;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.util.stream.IntStream;

/**
 * Called from the browser to rerun a slot.
 */
public class ActionServlet extends HttpServlet {

    private final static String ACTION_PARAM = "action";
    private final static String WORKFLOW_NAME_PARAM = "workflowName";
    private final static String TIMESTAMP_PARAM = "ts";
    private final static String SLOTS_PARAM = "slots";
    private final static String KILL_ACTION = "kill";
    private final static String RERUN_ACTION = "rerun";

    protected void processPost(CelosClient client, String action, JsonNode jsonNode) throws Exception {
        for (JsonNode x : jsonNode.get(SLOTS_PARAM)) {
            final WorkflowID workflowName = new WorkflowID(x.get(WORKFLOW_NAME_PARAM).asText());
            final ScheduledTime ts = new ScheduledTime(x.get(TIMESTAMP_PARAM).asText());
            if (KILL_ACTION.equals(action)) {
                client.kill(workflowName, ts);
            } else if (RERUN_ACTION.equals(action)) {
                client.rerunSlot(workflowName, ts);
            } else {
                throw new Exception("action " + action + " not found");
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            final CelosClient client = UICommon.getCelosClient(getServletContext());
            final JsonNode jsonNode = Util.MAPPER.readTree(request.getReader());
            final String action = jsonNode.get(ACTION_PARAM).asText();
            processPost(client, action, jsonNode);
            Util.JSON_PRETTY.writeValue(response.getWriter(), jsonNode);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch(Exception e) {
            throw new ServletException(e);
        }
    }

}
