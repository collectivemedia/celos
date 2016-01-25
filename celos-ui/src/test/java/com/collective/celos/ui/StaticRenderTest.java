package com.collective.celos.ui;

import org.mozilla.javascript.*;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;


public class StaticRenderTest {

    @Test
    public void StaticRenderRhino() {
        // Creates and enters a Context. The Context stores information
        // about the execution environment of a script.
        Context cx = Context.enter();
        try {
            // Initialize the standard objects (Object, Function, etc.)
            // This must be done before scripts can be executed. Returns
            // a scope object that we use in later calls.
            Scriptable scope = cx.initStandardObjects();
            InputStreamReader reader;
            final Class aClass = StaticRenderTest.class.getClass();

            final LibLoader libLoader = new LibLoader(cx, scope, aClass);
            libLoader.proceed(LibLoader.commonLibs);
            libLoader.proceed(LibLoader.renderLibs);

            reader = new InputStreamReader(aClass.getResourceAsStream("/static/render-page.js"));
            Object result = cx.evaluateReader(scope, reader, "filename", LibLoader.JS_PARSE_START_LINE, null);


            cx.evaluateString(scope, "var tmp = React.createElement(CelosMainFetch, { url: \"/main\", request: request });", "<cmd>", 0, null);
            cx.evaluateString(scope, "var result = ReactDOMServer.renderToStaticMarkup(tmp);", "<cmd>", 0, null);
            cx.evaluateString(scope, "console.log(result);", "<cmd>", 0, null);


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

}
