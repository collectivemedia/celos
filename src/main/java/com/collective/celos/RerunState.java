package com.collective.celos;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class RerunState extends ValueObject {

    private final ScheduledTime rerunTime;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final String RERUN_TIME_PROP = "rerunTime";

    public RerunState() {
        rerunTime = null;
    }

    public static RerunState fromJsonNode(JsonNode node) {
        ScheduledTime time = new ScheduledTime(node.get(RERUN_TIME_PROP).textValue());
        return new RerunState(time);
    }

    public JsonNode toJsonNode() {
        ObjectNode node = MAPPER.createObjectNode();
        node.put(RERUN_TIME_PROP, rerunTime.toString());
        return node;
    }

    public static RerunState fromTime(ScheduledTime time) {
        return new RerunState(time);
    }

    private RerunState(ScheduledTime time) {
        assert time != null;
        rerunTime = time;
    }

}
