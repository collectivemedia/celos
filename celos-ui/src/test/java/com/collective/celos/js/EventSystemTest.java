package com.collective.celos.js;

import org.junit.Assert;
import org.junit.Test;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.Scriptable;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;


public class EventSystemTest {

    @Test
    public void eventSystemTest() throws IOException, URISyntaxException {
        Context.enter();
        try {
            final LibLoader libLoader = new LibLoader();
            libLoader.proceedCommonLibs();
            libLoader.evaluateResource("/static/set-initial-state.js");

            libLoader.evaluateString("AppDispatcher.clearSelection();");
            Object result = libLoader.evaluateString("console.log(JSON.stringify(_internalSlotsData.toJS(), null, 2));");

            // Convert the result to a string and print it.
            System.err.println(Context.toString(result));
            Assert.assertEquals(Context.toString(result), "undefined");

        } finally {
            Context.exit();
        }
    }

    @Test(expected=JavaScriptException.class)
    public void throwExceptionTest() throws IOException, URISyntaxException {
        Context.enter();
        try {
            final LibLoader libLoader = new LibLoader();
            libLoader.proceedCommonLibs();
            libLoader.evaluateString("throw new Error(\"ololo\");");
        } finally {
            Context.exit();
        }
    }

}
