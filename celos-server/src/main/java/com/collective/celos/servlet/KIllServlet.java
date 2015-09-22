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
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;

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
public class KIllServlet extends AbstractServlet {
    
    private static Logger LOGGER = Logger.getLogger(KIllServlet.class);
    
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

            SlotID slotID = new SlotID(workflowID, time);
            Set<ScheduledTime> timeSet = workflow.getSchedule().getScheduledTimes(scheduler, time.minusSeconds(1), time.plusSeconds(1));
            if (!timeSet.contains(time)) {
                res.sendError(HttpServletResponse.SC_NOT_FOUND, "Slot is not found: " + slotID);
                return;
            }
            StateDatabase db = scheduler.getStateDatabase();
            SlotState state = db.getSlotState(slotID);

            LOGGER.info("Killing slot: " + slotID);
            if (state == null) {
                db.putSlotState(new SlotState(slotID, SlotState.Status.KILLED));
            } else {
                SlotState newState = state.transitionToKill();
                db.putSlotState(newState);
                if (state.getExternalID() != null) {
                    workflow.getExternalService().kill(slotID, state.getExternalID());
                }
            }

        } catch(Exception e) {
            throw new ServletException(e);
        }
    }

}