package com.collective.celos;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class RerunState extends ValueObject {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    protected static final int GC_PERIOD_DAYS = 14;

    public WorkflowID getWorkflowId() {
        return workflowId;
    }

    public ScheduledTime getScheduledTime() {
        return scheduledTime;
    }

    public ScheduledTime getRerunTime() {
        return rerunTime;
    }

    public static RerunState fromJsonNode(JsonNode json) throws IOException {
        RerunStateJSON t = MAPPER.treeToValue(json, RerunStateJSON.class);
        return new RerunState(t.workflowId, t.scheduledTime, t.rerunTime);
    }

    public JsonNode toJsonNode() {
        return MAPPER.valueToTree(new RerunStateJSON(this));
    }

    public static RerunState fromTime(WorkflowID wfId, ScheduledTime slotTime, ScheduledTime rerunTime) {
        return new RerunState(wfId, slotTime, rerunTime);
    }

    public boolean isExpired(ScheduledTime time) {
        // rerunTime is before (time - GC_PERIOD_DAYS)
        return rerunTime.compareTo(time.minusDays(GC_PERIOD_DAYS)) <= 0;
    }


    private RerunState(WorkflowID wfId, ScheduledTime slotTime, ScheduledTime rerunTime) {
        this.workflowId = Util.requireNonNull(wfId);
        this.scheduledTime = Util.requireNonNull(slotTime);
        this.rerunTime = Util.requireNonNull(rerunTime);
    }

    private static class RerunStateJSON {
        private WorkflowID workflowId;
        private ScheduledTime scheduledTime;
        private ScheduledTime rerunTime;

        public RerunStateJSON() {}

        public RerunStateJSON(RerunState rerunState) {
            this.workflowId = rerunState.getWorkflowId();
            this.scheduledTime = rerunState.getScheduledTime();
            this.rerunTime = rerunState.getRerunTime();
        }

        public String getWorkflowId() {
            return workflowId.toString();
        }

        public String getScheduledTime() {
            return scheduledTime.toString();
        }

        public String getRerunTime() {
            return rerunTime.toString();
        }
    }

    private final WorkflowID workflowId;
    private final ScheduledTime scheduledTime;
    private final ScheduledTime rerunTime;


}
