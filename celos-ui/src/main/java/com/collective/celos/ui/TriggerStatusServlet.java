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
import com.fasterxml.jackson.databind.node.ArrayNode;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Renders the UI HTML.
 */
public class TriggerStatusServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        try {
            final CelosClient client = UICommon.getCelosClient(getServletContext());
            final ArrayNode node = getJsonNodes(req, client);
            response.setContentType("application/json;charset=UTF-8");
            Util.JSON_PRETTY.writeValue(response.getWriter(), node);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    protected ArrayNode getJsonNodes(HttpServletRequest req, CelosClient client) throws Exception {
        final List<String> timestamps = Arrays.stream(req.getParameter(UICommon.TIME_PARAM).split(","))
                .limit(UICommon.MULTI_SLOT_INFO_LIMIT)
                .collect(toList());
        final ArrayNode node = Util.MAPPER.createArrayNode();
        for (String ts : timestamps) {
            node.add(client.getTriggerStatus(req.getParameter(UICommon.ID_PARAM), ts));
        }
        return node;
    }
}
