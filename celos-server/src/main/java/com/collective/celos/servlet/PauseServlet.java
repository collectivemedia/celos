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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Posting to this servlet (un) pauses the specified workflow.
 * 
 * Parameters:
 * 
 * id -- workflow ID
 * 
 * paused -- true/false whether workflow should be paused or unpaused
 */
@SuppressWarnings("serial")
public class PauseServlet extends AbstractServlet {

    private static final String ID_PARAM = "id";
    private static final String PAUSE_PARAM = "paused";

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException {
        try {
            String id = req.getParameter(ID_PARAM);
            if (id == null) {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST, ID_PARAM + " parameter missing.");
                return;
            }

            Scheduler scheduler = getOrCreateCachedScheduler();
            WorkflowID workflowID = new WorkflowID(id);
            Workflow workflow = scheduler.getWorkflowConfiguration().findWorkflow(workflowID);
            if (workflow == null) {
                res.sendError(HttpServletResponse.SC_NOT_FOUND, "Workflow not found: " + id);
                return;
            }

            Boolean pause = Boolean.parseBoolean(req.getParameter(PAUSE_PARAM));
            try(StateDatabaseConnection connection = getStateDatabase().openConnection()) {
                connection.setPaused(workflowID, pause);
            }
        } catch(Exception e) {
            throw new ServletException(e);
        }
    }
}
