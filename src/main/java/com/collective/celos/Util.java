package com.collective.celos;

import org.joda.time.DateTime;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Util {

    public static <T> T requireNonNull(T object) {
        if (object == null) throw new NullPointerException();
        else return object;
    }

    static DateTime toFullHour(DateTime dt) {
        return dt.withMillisOfSecond(0).withSecondOfMinute(0).withMinuteOfHour(0);
    }

    static boolean isFullHour(DateTime dt) {
        return dt.getMillisOfSecond() == 0
            && dt.getSecondOfMinute() == 0
            && dt.getMinuteOfHour() == 0;
    }
    
    public static ObjectNode newObjectNode() {
        return JsonNodeFactory.instance.objectNode();
    }

    public static String getStringProperty(ObjectNode properties, String name) {
        JsonNode node = properties.get(name);
        if (node == null) {
            throw new IllegalArgumentException("Property " + name + " not set.");
        } else if (!node.isTextual()) {
            throw new IllegalArgumentException("Property " + name + " is not a string, but " + node);
        } else {
            return node.textValue();
        }
    }
    
}
