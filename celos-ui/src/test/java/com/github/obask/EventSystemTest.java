package com.github.obask;

import org.junit.Assert;
import org.junit.Test;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;


public class EventSystemTest {

//    @Test
    public void eventSystemTest() {
        // Creates and enters a Context. The Context stores information
        // about the execution environment of a script.
        Context cx = Context.enter();
        try {
            // Initialize the standard objects (Object, Function, etc.)
            // This must be done before scripts can be executed. Returns
            // a scope object that we use in later calls.
            Scriptable scope = cx.initStandardObjects();
            InputStreamReader reader;
            final Class aClass = EventSystemTest.class.getClass();

            final LibLoader libLoader = new LibLoader(cx, scope, aClass);
            libLoader.proceed(LibLoader.commonLibs);
            libLoader.proceed(LibLoader.eventLibs);

            reader = new InputStreamReader(aClass.getResourceAsStream("/static/eval-events.js"));
            Object result = cx.evaluateReader(scope, reader, "render-page.js", LibLoader.JS_PARSE_START_LINE, null);

            // Convert the result to a string and print it.
            System.err.println(Context.toString(result));
            Assert.assertEquals(Context.toString(result), "undefined");

        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        } finally {
            // Exit from the context.
            Context.exit();
        }
    }



    public static void main(String[] args) {
        new EventSystemTest().eventSystemTest();
    }

}
