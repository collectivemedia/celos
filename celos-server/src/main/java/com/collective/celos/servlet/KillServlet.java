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
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Posting to this servlet kills the specified slot's underlying job.
 * 
 * Parameters:
 * 
 * id -- workflow ID
 * 
 * time -- scheduled time of slot
 */
@SuppressWarnings("serial")
public class KillServlet extends AbstractServlet {
    
    private static Logger LOGGER = Logger.getLogger(KillServlet.class);
    
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException {
        try {
            ScheduledTime time = getRequestTime(req);
            String id = req.getParameter(CelosClient.ID_PARAM);
            if (id == null) {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST, CelosClient.ID_PARAM + " parameter missing.");
                return;
            }

            Scheduler scheduler = getOrCreateCachedScheduler();
            WorkflowID workflowID = new WorkflowID(id);
            Workflow workflow = scheduler.getWorkflowConfiguration().findWorkflow(workflowID);
            if (workflow == null) {
                res.sendError(HttpServletResponse.SC_NOT_FOUND, "Workflow not found: " + id);
                return;
            }

            SlotID slotID = new SlotID(workflowID, time);
            if (!workflow.getSchedule().isTimeInSchedule(time, scheduler)) {
                res.sendError(HttpServletResponse.SC_NOT_FOUND, "Slot is not found: " + slotID);
                return;
            }
            try (StateDatabaseConnection db = getStateDatabase().openConnection()) {
                SlotState state = db.getSlotState(slotID);
                LOGGER.info("Killing slot: " + slotID);
                if (state == null) {
                    db.putSlotState(new SlotState(slotID, SlotState.Status.KILLED));
                } else {
                    if (state.getExternalID() != null) {
                        workflow.getExternalService().kill(slotID, state.getExternalID());
                    }
                    SlotState newState = state.transitionToKill();
                    db.putSlotState(newState);
                }
            }
        } catch(Exception e) {
            throw new ServletException(e);
        }
    }

}
