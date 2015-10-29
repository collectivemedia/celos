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
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Returns JSON information about a single slot.
 */
@SuppressWarnings("serial")
public class JSONSlotStateServlet extends AbstractJSONServlet {
    
    private static final String ID_PARAM = "id";

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException {
        String id = req.getParameter(ID_PARAM);
        try {
            if (id == null) {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST, ID_PARAM + " parameter missing.");
                return;
            }
            Scheduler scheduler = getOrCreateCachedScheduler();
            SlotID slotID = new SlotID(new WorkflowID(id), getRequestTime(req));
            try (StateDatabaseConnection connection = scheduler.getStateDatabase().openConnection()) {
                SlotState slotState = connection.getSlotState(slotID);
                if (slotState == null) {
                    res.sendError(HttpServletResponse.SC_NOT_FOUND, "Slot not found: " + id);
                } else {
                    ObjectNode object = slotState.toJSONNode();
                    writer.writeValue(res.getOutputStream(), object);
                }
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
    
}
