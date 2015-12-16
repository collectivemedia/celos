package com.github.obask;

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

    private static final int JS_PARSE_START_LINE = 1;

    @Test
    public void StaticRenderRhino()
    {
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

            // load libs
            final ArrayList<String> libs = new ArrayList<>();

            libs.add("/static/prepare.js");
            libs.add("/static/node_modules/react/dist/react.js");
            libs.add("/static/node_modules/react-dom/dist/react-dom-server.js");
            libs.add("/static/node_modules/immutable/dist/immutable.js");
            libs.add("/static/node_modules/events/events.js");
            libs.add("/static/node_modules/object-assign/index.js");
            libs.add("/static/node_modules/js-promise/js-promise.js");

            libs.add("/static/js/lib.js");
            libs.add("/static/js/Nav.js");
            libs.add("/static/js/Dispatcher.js");
            libs.add("/static/js/stores/sidebarStore.js");
            libs.add("/static/js/stores/slotsStore.js");
            libs.add("/static/js/components/Sidebar.js");
            libs.add("/static/js/components/ContextMenu.js");
            libs.add("/static/js/components/ModalBox.js");

            libs.add("/static/js/components/SlotsTable.js");
            libs.add("/static/js/components/Main.js");

            for (String jsFileName : libs) {
                System.out.println(jsFileName);
                final Path path = Paths.get(aClass.getResource(jsFileName).toURI());
                System.out.println(path.toFile().exists());
                reader = new InputStreamReader(aClass.getResourceAsStream(jsFileName));
                cx.evaluateReader(scope, reader, "jsFileName", JS_PARSE_START_LINE, null);
            }
            System.out.println("libs loaded");

            reader = new InputStreamReader(aClass.getResourceAsStream("/static/render-page.js"));
            Object result = cx.evaluateReader(scope, reader, "CMD", JS_PARSE_START_LINE, null);

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
