package com.collective.celos;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public interface PropertiesGenerator {

    public ObjectNode getProperties(SlotID id);
    
    public static final PropertiesGenerator EMPTY = new PropertiesGenerator() {
        @Override
        public ObjectNode getProperties(SlotID id) {
            return new ObjectMapper().createObjectNode();
        }
    };
    
}
