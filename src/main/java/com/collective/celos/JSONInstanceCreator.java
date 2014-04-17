package com.collective.celos;

import java.lang.reflect.Constructor;

import org.apache.commons.beanutils.ConstructorUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Creates instances of various classes from a JSON configuration.
 * 
 * Every object must have a "type" property (string) containing its Java class.
 * 
 * Additionally, it may have a "properties" property (JSON object) containing additional properties.
 * 
 * The instance creator invokes the class' constructor with a ObjectNode as argument,
 * containing the additional properties (empty if no additional properties are in the JSON).
 */
public class JSONInstanceCreator {

    private static final String TYPE_PROP = "type";
    private static final String PROPERTIES_PROP = "properties";
    private static final ObjectMapper MAPPER = new ObjectMapper();
    
    public Object createInstance(String json) throws Exception {
        return createInstance(MAPPER.readTree(json));
    }
    
    public Object createInstance(JsonNode node) throws Exception {
        ObjectNode properties = getProperties(node);
        Constructor<?> ctor = getConstructor(node);
        return ctor.newInstance(properties);
    }

    ObjectNode getProperties(JsonNode node) {
        JsonNode properties = node.get(PROPERTIES_PROP);
        if (properties == null) {
            return Util.newObjectNode();
        } else {
            if (!properties.isObject()) {
                throw new IllegalArgumentException("Properties must be an object, but is: " + properties);
            } else {
                return (ObjectNode) properties;
            }
        }
    }

    private Constructor<?> getConstructor(JsonNode workflowNode) throws ClassNotFoundException {
        JsonNode typeNode = workflowNode.get(TYPE_PROP);
        if (typeNode == null || !typeNode.isTextual()) {
            throw new IllegalArgumentException("Type must be a string: " + workflowNode.toString());
        }
        String className = typeNode.textValue();
        Class<?> c = Class.forName(className);
        Constructor<?> ctor = ConstructorUtils.getAccessibleConstructor(c, ObjectNode.class);
        if (ctor == null) {
            throw new RuntimeException("Constructor with ObjectNode argument not found for " + className);
        }
        return ctor;
    }
    
}
