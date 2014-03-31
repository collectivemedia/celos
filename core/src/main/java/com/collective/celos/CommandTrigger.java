package com.collective.celos;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.collective.celos.api.ScheduledTime;
import com.collective.celos.api.ScheduledTimeFormatter;
import com.collective.celos.api.Trigger;
import com.collective.celos.api.Util;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class CommandTrigger implements Trigger {

    public static final String COMMAND_PROP = "celos.commandTrigger.command";

    private final ScheduledTimeFormatter formatter = new ScheduledTimeFormatter();
    private final List<String> rawCommandElements;

    public CommandTrigger(ObjectNode properties) throws Exception {
        List<String> elements = new LinkedList<>();
        ArrayNode array = Util.getArrayProperty(properties, COMMAND_PROP);
        for (JsonNode element : array) {
            if (!element.isTextual()) {
                throw new IllegalArgumentException("Command may only contain strings: " + array);
            } else {
                elements.add(element.textValue());
            }
        }
        rawCommandElements = Collections.unmodifiableList(elements);
    }
    
    @Override
    public boolean isDataAvailable(ScheduledTime now, ScheduledTime t) throws Exception {
        List<String> cookedCommandElements = new LinkedList<>();
        for (String rawElement : rawCommandElements) {
            cookedCommandElements.add(formatter.replaceTimeTokens(rawElement, t));
        }
        return new ProcessBuilder(cookedCommandElements).start().waitFor() == 0;
    }

    public List<String> getRawCommandElements() {
        return rawCommandElements;
    }

}
