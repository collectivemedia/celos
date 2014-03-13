package com.collective.celos;

import com.collective.celos.api.Util;
import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class JSONInstanceCreatorTest {
    
    @Test(expected=IllegalArgumentException.class)
    public void propertiesMustBeAnObject() {
        ObjectNode node = Util.newObjectNode();
        node.put("properties", 12);
        new JSONInstanceCreator().getProperties(node);
    }
    
    @Test
    public void returnsEmptyPropertiesIfNotSet() {
        ObjectNode node = Util.newObjectNode();
        Assert.assertEquals(Util.newObjectNode(), new JSONInstanceCreator().getProperties(node));
    }

}
