package com.collective.celos;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class RerunState extends ValueObject {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    protected static final int GC_PERIOD_DAYS = 14;

    private static final String RERUN_TIME_PROP = "rerunTime";
    private static final String SLOT_TIME_PROP = "scheduledTime";
    private static final String WORKFLOW_ID_PROP = "workflowId";

    private final WorkflowID workflowId;
    private final ScheduledTime slotTime;
    private final ScheduledTime rerunTime;

    public static RerunState fromJsonNode(JsonNode node) {
        ScheduledTime slotTime = new ScheduledTime(node.get(SLOT_TIME_PROP).textValue());
        ScheduledTime rerunTime = new ScheduledTime(node.get(RERUN_TIME_PROP).textValue());
        WorkflowID wfId = new WorkflowID(node.get(WORKFLOW_ID_PROP).textValue());
        return new RerunState(wfId, slotTime, rerunTime);
    }

    public JsonNode toJsonNode() {
        ObjectNode node = MAPPER.createObjectNode();
        node.put(RERUN_TIME_PROP, rerunTime.toString());
        node.put(WORKFLOW_ID_PROP, workflowId.toString());
        node.put(SLOT_TIME_PROP, slotTime.toString());
        return node;
    }

    public static RerunState fromTime(WorkflowID wfId, ScheduledTime slotTime, ScheduledTime rerunTime) {
        return new RerunState(wfId, slotTime, rerunTime);
    }

    private RerunState(WorkflowID wfId, ScheduledTime slotTime, ScheduledTime rerunTime) {
        assert wfId != null;
        this.workflowId = wfId;
        assert slotTime != null;
        this.slotTime = slotTime;
        assert rerunTime != null;
        this.rerunTime = rerunTime;
    }

    public boolean isExpired(ScheduledTime time) {
        // rerunTime is before (time - GC_PERIOD_DAYS)
        return rerunTime.compareTo(time.minusDays(GC_PERIOD_DAYS)) <= 0;
    }

    public WorkflowID getWorkflowId() {
        return workflowId;
    }

    public ScheduledTime getSlotTime() {
        return slotTime;
    }

    public ScheduledTime getRerunTime() {
        return rerunTime;
    }
}
