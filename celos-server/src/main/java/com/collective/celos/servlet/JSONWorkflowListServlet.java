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

import com.collective.celos.Scheduler;
import com.collective.celos.Util;
import com.collective.celos.Workflow;
import com.collective.celos.WorkflowConfiguration;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Returns list of IDs of configured workflows as JSON.
 *
 * GET /workflow-list
 * ==
 * {
 *   "ids": [ "workflow-1", "workflow-2" ]
 * }
 */
@SuppressWarnings("serial")
public class JSONWorkflowListServlet extends AbstractJSONServlet {

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException {
        try {
            Scheduler sch = getOrCreateCachedScheduler();
            WorkflowConfiguration cfg = sch.getWorkflowConfiguration();
            ObjectNode object = createJSONObject(cfg);
            writer.writeValue(res.getOutputStream(), object);
        } catch(Exception e) {
            throw new ServletException(e);
        }
    }

    ObjectNode createJSONObject(WorkflowConfiguration cfg) {
        // Make sure the IDs are sorted
        SortedSet<String> ids = new TreeSet<String>();
        for (Workflow wf : cfg.getWorkflows()) {
            ids.add(wf.getID().toString());
        }
        ArrayNode list = Util.MAPPER.createArrayNode();
        for (String id : ids) {
            list.add(id);
        }
        ObjectNode object = Util.MAPPER.createObjectNode();
        object.put("ids", list);
        return object;
    }

}
