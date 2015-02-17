package com.collective.celos;

import com.collective.celos.ci.mode.test.TestRun;
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

        WorkflowConfigurationParser parser = new WorkflowConfigurationParser(defaults, ImmutableMap.of(TestRun.CELOS_USER_JS_VAR, "nameIsChanged"));

        String func = "function (slotId) {" +
                "        return {" +
                "            \"oozie.wf.application.path\": \"/workflow.xml\"," +
                "            \"inputDir\": \"/input\"," +
                "            \"outputDir\": \"/output\"" +
                "        }" +
                "    }";

        String str = "importDefaults(\"test\"); celos.makePropertiesGen(" + func + "); ";
        NativeJavaObject jsResult = (NativeJavaObject) parser.evaluateReader(new StringReader(str), "string");
        PropertiesGenerator generator = (PropertiesGenerator) jsResult.unwrap();
        Assert.assertEquals(generator.getProperties(null).get("user.name").asText(), "nameIsChanged");
    }



}
