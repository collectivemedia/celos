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

/**
 * Posting to this servlet reruns the specified slot.
 * 
 * Parameters:
 * 
 * id -- workflow ID
 * 
 * time -- scheduled time of slot
 */
@SuppressWarnings("serial")
public class RerunServlet extends AbstractServlet {
    
    private static Logger LOGGER = Logger.getLogger(RerunServlet.class);
    
    private static final String ID_PARAM = "id";

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException {
        try {
            ScheduledTime time = getRequestTime(req);
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

            SlotID slot = new SlotID(workflowID, time);
            if (!workflow.getSchedule().isTimeInSchedule(time, scheduler)) {
                res.sendError(HttpServletResponse.SC_NOT_FOUND, "Slot is not found: " + slot);
                return;
            }

            try(StateDatabaseConnection db = scheduler.getStateDatabase().openConnection()) {
                SlotState state = db.getSlotState(slot);
                if (state != null) {
                    updateSlotToRerun(state, db);
                }
                db.markSlotForRerun(slot, ScheduledTime.now());
            }
        } catch(Exception e) {
            throw new ServletException(e);
        }
    }

    void updateSlotToRerun(SlotState state, StateDatabaseConnection db) throws Exception {
        LOGGER.info("Scheduling Slot for rerun: " + state.getSlotID());
        SlotState newState = state.transitionToRerun();
        db.putSlotState(newState);
    }

}
