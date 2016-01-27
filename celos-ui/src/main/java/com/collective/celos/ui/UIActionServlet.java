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
import com.collective.celos.Util;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.text.html.parser.Entity;
import java.io.IOException;
import java.net.URL;

/**
 * Called from the browser to rerun a slot.
 */
public class UIActionServlet extends HttpServlet {
    
    private static final String SLOT_ID = "id";


    private static void processOne(JsonNode x) {
        System.out.println("---");
        System.out.println(x.get("workflowName").asText());
        System.out.println(x.get("ts").asText());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            URL celosURL = (URL) Util.requireNonNull(getServletContext().getAttribute(Main.CELOS_URL_ATTR));
            CelosClient client = new CelosClient(celosURL.toURI());

            ObjectMapper mapper = new ObjectMapper();
            final JsonNode jsonNode = mapper.readTree(request.getReader());
            System.out.println("SUCCESS123!!!!");
            final String action = jsonNode.get("action").asText();
            jsonNode.get("slots").forEach(UIActionServlet::processOne);

//            client.kill(id.getWorkflowID(), id.getScheduledTime());
//            client.getWorkflowStatus(new WorkflowID("DUMMY"), ScheduledTime.now());

            mapper.writeValue(response.getWriter(), jsonNode);
//            System.out.println(mapper.writeValueAsString(jsonNode));

            response.setStatus(HttpServletResponse.SC_OK);
        } catch(Exception e) {
            throw new ServletException(e);
        }
    }
}
