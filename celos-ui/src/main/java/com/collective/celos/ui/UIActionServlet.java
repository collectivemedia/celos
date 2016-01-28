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
import javax.swing.text.html.parser.Entity;
import javax.swing.tree.ExpandVetoException;
import java.io.IOException;
import java.net.URL;

/**
 * Called from the browser to rerun a slot.
 */
public class UIActionServlet extends HttpServlet {
    
    private static final String SLOT_ID = "id";

    protected static void method1(CelosClient client, String action, JsonNode jsonNode) throws Exception {
        jsonNode.get("slots").forEach(x -> {
            try {
                System.out.println("---");
                System.out.println(x.get("workflowName").asText());
                System.out.println(x.get("ts").asText());
                final WorkflowID workflowName = new WorkflowID(x.get("workflowName").asText());
                final ScheduledTime ts = new ScheduledTime(x.get("ts").asText());
                if ("kill".equals(action)) {
                    client.kill(workflowName, ts);
                } else if ("rerun".equals(action)) {
                    client.rerunSlot(workflowName, ts);
                } else {
                    throw new Exception("UIActionServlet failed");
                }
            } catch (Exception e) {
                // FIXME it shouldn't be like this
                e.printStackTrace();
            }
        });
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
            method1(client, action, jsonNode);

            mapper.writeValue(response.getWriter(), jsonNode);

            response.setStatus(HttpServletResponse.SC_OK);
        } catch(Exception e) {
            throw new ServletException(e);
        }
    }

}
