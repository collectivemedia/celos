package com.collective.celos.js;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class LibLoader {

    public static final int JS_PARSE_START_LINE = 1;

    private Context cx;
    private Scriptable scope;
    private Class aClass;

    public LibLoader() {
        this.cx = Context.getCurrentContext();
        this.scope = cx.initStandardObjects();
        this.aClass = LibLoader.class;
    }

    Object evaluateString(String code) throws IOException {
        return cx.evaluateString(scope, code, "<cmd>", JS_PARSE_START_LINE, null);
    }

    Object evaluateResource(String path) throws IOException {
        final Reader reader = new InputStreamReader(aClass.getResourceAsStream(path));
        return cx.evaluateReader(scope, reader, "...", JS_PARSE_START_LINE, null);
    }

    void proceedCommonLibs() throws IOException, URISyntaxException {
        evaluateLibs(commonLibs);
    }

    static final ArrayList<String> commonLibs = new ArrayList<String>() {{
            add("/static/node_modules/immutable/dist/immutable.min.js");
            add("/static/node_modules/events/events.js");
            add("/static/node_modules/object-assign/index.js");
            add("/static/node_modules/js-promise/js-promise.js");

            add("/static/js/lib.js");
            add("/static/js/Dispatcher.js");
            add("/static/js/stores/sidebarStore.js");
            add("/static/js/stores/slotsStore.js");

            add("/mocks/config.js");
            add("/mocks/groupFlume.js");
            add("/mocks/groupParquetify.js");

        }};

    void evaluateLibs(ArrayList<String> libs) throws URISyntaxException, IOException {
        InputStreamReader reader;
        for (String jsFileName : libs) {
            final Path path = Paths.get(aClass.getResource(jsFileName).toURI());
            reader = new InputStreamReader(aClass.getResourceAsStream(jsFileName));
            cx.evaluateReader(scope, reader, "jsFileName", JS_PARSE_START_LINE, null);
        }
    }

    static public void main(String[] args) {
        Context.enter();
        try {
            Context cx = Context.getCurrentContext();
            Scriptable scope = cx.initStandardObjects();
            final Object result = cx.evaluateString(scope, "var tmp = {x: null}; JSON.stringify(tmp);", "...", 0, null);
            final String s = Context.toString(result);
            System.out.println(s);
        } finally {
            Context.exit();
        }
    }

}
