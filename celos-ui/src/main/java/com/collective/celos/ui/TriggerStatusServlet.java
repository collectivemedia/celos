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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Renders the UI HTML.
 */
public class TriggerStatusServlet extends HttpServlet {

    private static final String ID_PARAM = "id";
    private static final String TIME_PARAM = "time";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        try {
            URL celosURL = (URL) Util.requireNonNull(getServletContext().getAttribute(Main.CELOS_URL_ATTR));
            URL hueURL = (URL) getServletContext().getAttribute(Main.HUE_URL_ATTR);
            File configFile = (File) getServletContext().getAttribute(Main.CONFIG_FILE_ATTR);
            final List<String> timestamps = Arrays.stream(req.getParameter(TIME_PARAM).split(","))
                    .limit(ReactWorkflowsServlet.MULTI_SLOT_INFO_LIMIT)
                    .collect(toList());
            ObjectMapper mapper = new ObjectMapper();
            CelosClient client = new CelosClient(celosURL.toURI());
            final ArrayNode node = mapper.createArrayNode();
            for (String ts : timestamps) {
                node.add(client.getTriggerStatusAsText(req.getParameter(ID_PARAM), ts));
            }
            response.setContentType("application/json;charset=UTF-8");
            mapper.writeValue(response.getWriter(), node);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
