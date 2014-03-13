package com.collective.celos.api;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.Assert;
import org.junit.Test;

public class UtilTest {
    
    @Test(expected=NullPointerException.class)
    public void requireNonNullWorks() {
        Util.requireNonNull(null);
    }

    // DATETIME TESTS
    
    @Test
    public void toFullHourWorks() {
        Assert.assertEquals(new DateTime("2013-11-27T15:00:00.000Z"), Util.toFullHour(new DateTime("2013-11-27T15:36:23.475Z")));
    }
    
    @Test
    public void toFullMinuteWorks() {
        Assert.assertEquals(new DateTime("2013-11-27T15:36:00.000Z"), Util.toFullMinute(new DateTime("2013-11-27T15:36:23.475Z")));
    }
    
    @Test
    public void toFullSecondWorks() {
        Assert.assertEquals(new DateTime("2013-11-27T15:36:23.000Z"), Util.toFullSecond(new DateTime("2013-11-27T15:36:23.475Z")));
    }
    
    @Test
    public void isFullHourWorks() {
        Assert.assertTrue(Util.isFullHour(new DateTime("2013-11-27T15:00:00.000Z")));
        Assert.assertFalse(Util.isFullHour(new DateTime("2013-11-27T15:36:00.000Z")));
    }
    
    @Test
    public void isFullMinuteWorks() {
        Assert.assertTrue(Util.isFullMinute(new DateTime("2013-11-27T15:01:00.000Z")));
        Assert.assertFalse(Util.isFullMinute(new DateTime("2013-11-27T15:36:01.000Z")));
    }
    
    @Test
    public void isFullSecondWorks() {
        Assert.assertTrue(Util.isFullSecond(new DateTime("2013-11-27T15:01:00.000Z")));
        Assert.assertFalse(Util.isFullSecond(new DateTime("2013-11-27T15:36:01.001Z")));
    }
    
    // JSON TESTS
    
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
    
    @Test(expected=IllegalArgumentException.class)
    public void getArrayPropertyDetectsUnsetProperty() {
        ObjectNode node = Util.newObjectNode();
        Util.getArrayProperty(node, "foo");
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void getArrayPropertyDetectsNonArrayProperty() {
        ObjectNode node = Util.newObjectNode();
        node.put("foo", 12);
        Util.getArrayProperty(node, "foo");
    }
    
    @Test
    public void getArrayPropertyWorks() {
        ObjectNode node = Util.newObjectNode();
        ArrayNode array = Util.newArrayNode();
        array.add("bar");
        node.put("foo", array);
        Assert.assertEquals(array, Util.getArrayProperty(node, "foo"));
    }

    @Test
    public void toNominalTimeFormat() {
        String formatted = new DateTime(2013, 1, 3, 12, 10).toString(DateTimeFormat.forPattern("YYYY-MM-dd'T'HH:mm'Z"));
        Assert.assertEquals(formatted, "2013-01-03T12:10Z");
    }
    
}
