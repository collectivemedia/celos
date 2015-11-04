/*
 * Copyright 2015 Collective, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.collective.celos;

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

    @Test(expected=IllegalArgumentException.class)
    public void requireProperBucketIDorRegisterKeyThrows() {
        Util.requireProperBucketIDorRegisterKey("foo/bar");
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
    public void getIntPropertyDetectsUnsetProperty() {
        ObjectNode node = Util.newObjectNode();
        Util.getIntProperty(node, "foo");
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void getIntPropertyDetectsNonIntProperty() {
        ObjectNode node = Util.newObjectNode();
        node.put("foo", "foo");
        Util.getIntProperty(node, "foo");
    }
    
    @Test
    public void getIntPropertyWorks() {
        ObjectNode node = Util.newObjectNode();
        node.put("foo", 12);
        Assert.assertEquals(12, Util.getIntProperty(node, "foo"));
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
    
    @Test(expected=IllegalArgumentException.class)
    public void getObjectPropertyDetectsUnsetProperty() {
        ObjectNode node = Util.newObjectNode();
        Util.getObjectProperty(node, "foo");
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void getObjectPropertyDetectsNonObjectProperty() {
        ObjectNode node = Util.newObjectNode();
        node.put("foo", 12);
        Util.getObjectProperty(node, "foo");
    }
    
    @Test
    public void getObjectPropertyWorks() {
        ObjectNode node = Util.newObjectNode();
        ObjectNode nested = Util.newObjectNode();
        node.put("foo", nested);
        Assert.assertEquals(nested, Util.getObjectProperty(node, "foo"));
    }

    @Test
    public void toNominalTimeFormat() {
        String formatted = new DateTime(2013, 1, 3, 12, 10).toString(DateTimeFormat.forPattern("YYYY-MM-dd'T'HH:mm'Z"));
        Assert.assertEquals(formatted, "2013-01-03T12:10Z");
    }
    
}
