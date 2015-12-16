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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.collective.celos.*;
import com.collective.celos.database.StateDatabaseConnection;

/**
 * Posting to this servlet triggers a scheduler step.
 * 
 * If the "time" parameter is supplied, the step is performed for that time. 
 */
@SuppressWarnings("serial")
public class SchedulerServlet extends AbstractServlet {

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException {
        try {
            Scheduler scheduler = createAndCacheScheduler();
            ScheduledTime current = getRequestTime(req);
            Set<WorkflowID> workflowIDs = getWorkflowIDs(req);
            try(StateDatabaseConnection connection = getStateDatabase().openConnection()) {
                scheduler.step(current, workflowIDs, connection);
            }
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    Set<WorkflowID> getWorkflowIDs(HttpServletRequest req) {
        String idString = req.getParameter(CelosClient.IDS_PARAM);
        if (idString == null) {
            return Collections.<WorkflowID>emptySet();
        } else {
            String[] idArray = idString.split(",");
            Set<WorkflowID> workflowIDs = new HashSet<>();
            for (int i = 0; i < idArray.length; i++) {
                workflowIDs.add(new WorkflowID(idArray[i]));
            }
            return workflowIDs;
        }
    }

}
