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
import java.util.Enumeration;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

/**
 * Called from the browser to rerun a slot.
 */
public class ActionServlet extends HttpServlet {

    protected void processPost(CelosClient client, String action, JsonNode jsonNode) throws Exception {
        final JsonNode slots = jsonNode.get("slots");
        for (Integer i : IntStream.range(0, slots.size()).toArray()) {
            final JsonNode x = slots.get(i);
            final WorkflowID workflowName = new WorkflowID(x.get("workflowName").asText());
            final ScheduledTime ts = new ScheduledTime(x.get("ts").asText());
            if ("kill".equals(action)) {
                client.kill(workflowName, ts);
            } else if ("rerun".equals(action)) {
                client.rerunSlot(workflowName, ts);
            } else {
                throw new Exception(action + "not found");
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            URL celosURL = (URL) Util.requireNonNull(getServletContext().getAttribute(Main.CELOS_URL_ATTR));
            CelosClient client = new CelosClient(celosURL.toURI());

            ObjectMapper mapper = new ObjectMapper();
            final JsonNode jsonNode = mapper.readTree(request.getReader());
            System.out.println(mapper.writeValueAsString(jsonNode));
            final String action = jsonNode.get("action").asText();
            processPost(client, action, jsonNode);

            mapper.writeValue(response.getWriter(), jsonNode);

            response.setStatus(HttpServletResponse.SC_OK);
        } catch(Exception e) {
            throw new ServletException(e);
        }
    }

}
