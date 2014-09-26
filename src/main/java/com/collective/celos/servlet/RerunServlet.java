package com.collective.celos.servlet;

import com.collective.celos.*;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.SortedSet;

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
    private static final String START_TIME_PARAM = "startTime";
    private static final String END_TIME_PARAM = "endTime";
    
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException {
        try {
            ScheduledTime time = getRequestTime(req);
            String id = req.getParameter(ID_PARAM);
            if (id == null) {
                throw new IllegalArgumentException(ID_PARAM + " parameter missing.");
            }
            WorkflowID workflowID = new WorkflowID(id);
            if (req.getParameter(START_TIME_PARAM) == null || req.getParameter(END_TIME_PARAM) == null) {
                SlotID slot = new SlotID(workflowID, time);
                StateDatabase db = getOrCreateCachedScheduler().getStateDatabase();
                updateSlotToRerun(slot, db);
                LOGGER.info("Slot scheduled for rerun: " + slot);
            } else {
                ScheduledTime startTime = getRequestTime(req, START_TIME_PARAM);
                ScheduledTime endTime = getRequestTime(req, END_TIME_PARAM);
                updateMultipleSlots(startTime, endTime, workflowID);
            }
        } catch(Exception e) {
            throw new ServletException(e);
        }
    }

    void updateMultipleSlots(ScheduledTime startTime, ScheduledTime endTime, WorkflowID workflowID) throws Exception {

        Scheduler scheduler = getOrCreateCachedScheduler();
        StateDatabase db = scheduler.getStateDatabase();

        Workflow workflow = scheduler.getWorkflowConfiguration().findWorkflow(workflowID);
        SortedSet<ScheduledTime> scheduledTimes = workflow.getSchedule().getScheduledTimes(startTime, endTime);

        StringBuffer stringBuffer = new StringBuffer();
        for (ScheduledTime st : scheduledTimes) {
            if (stringBuffer.length() > 0) {
                stringBuffer.append(", ");
            }
            SlotID slot = new SlotID(workflowID, st);
            updateSlotToRerun(slot, db);
            stringBuffer.append(slot);
        }

        LOGGER.info("Slots are scheduled for rerun: " + stringBuffer);
    }


    void updateSlotToRerun(SlotID slot, StateDatabase db) throws Exception {
        SlotState state = db.getSlotState(slot);
        if (state == null) {
            throw new IllegalArgumentException("Slot not found: " + slot);
        }
        SlotState newState = state.transitionToRerun();
        db.putSlotState(newState);
    }

}
