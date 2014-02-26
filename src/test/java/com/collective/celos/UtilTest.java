package com.collective.celos;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class UtilTest {

    @Test(expected=IllegalArgumentException.class)
    public void getStringPropertyDetectsUnsetProperty() {
        ObjectNode node = Util.newObjectNode();
        Util.getStringProperty(node, "foo");
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void getStringPropertyDetectsNonStringProperty() {
        ObjectNode node = Util.newObjectNode();
        node.put("foo", 12);
        Util.getStringProperty(node, "foo");
    }
    
    @Test
    public void getStringPropertyWorks() {
        ObjectNode node = Util.newObjectNode();
        node.put("foo", "bar");
        Assert.assertEquals("bar", Util.getStringProperty(node, "foo"));
    }
    
}
