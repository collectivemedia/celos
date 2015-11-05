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

import com.google.common.collect.ImmutableMap;
import org.junit.Assert;
import org.junit.Test;
import org.mozilla.javascript.NativeJavaObject;

import java.io.File;
import java.io.StringReader;
import java.net.URL;

public class CelosWorkflowConfigurationParserTest {

    @Test
    public void testTakesChangesUsername() throws Exception {

        URL resource = Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/defaults-oozie-props");
        File defaults = new File(resource.toURI());

        WorkflowConfigurationParser parser = new WorkflowConfigurationParser(defaults, ImmutableMap.of("CELOS_USER_JS_VAR", "nameIsChanged"));

        String func = "function (slotId) {" +
                "        return {" +
                "            \"oozie.wf.application.path\": \"/workflow.xml\"," +
                "            \"inputDir\": \"/input\"," +
                "            \"outputDir\": \"/output\"" +
                "        }" +
                "    }";

        String str = "importDefaults(\"test\"); celos.makePropertiesGen(" + func + "); ";
        NativeJavaObject jsResult = (NativeJavaObject) parser.evaluateReader(new StringReader(str), "string",  new MemoryStateDatabase().openConnection());
        PropertiesGenerator generator = (PropertiesGenerator) jsResult.unwrap();
        Assert.assertEquals(generator.getProperties(null).get("user.name").asText(), "nameIsChanged");
    }



}
