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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.collective.celos.*;
import org.apache.log4j.Logger;

import com.collective.celos.trigger.TriggerStatus;

/**
 * Returns information about a trigger.
 * 
 * Parameters:
 * 
 * id -- workflow ID
 * 
 * time -- scheduled time of slot
 */
@SuppressWarnings("serial")
public class TriggerStatusServlet extends AbstractJSONServlet {
    
    protected static final String ID_PARAM = "id";

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException {
        try {
            String id = req.getParameter(ID_PARAM);
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
            try(StateDatabaseConnection connection = getStateDatabase().openConnection()) {
                TriggerStatus status = wf.getTrigger().getTriggerStatus(connection, ScheduledTime.now(), getRequestTime(req));
                writer.writeValue(res.getOutputStream(), status);
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
    
}
