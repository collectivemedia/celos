package com.collective.celos.ui;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import java.io.IOException;
import java.io.InputStreamReader;
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

        public LibLoader(Context cx, Scriptable scope, Class aClass) {
            this.cx = cx;
            this.scope = scope;
            this.aClass = aClass;
        }

    static final ArrayList<String> commonLibs = new ArrayList<String>() {{
            add("/static/prepare.js");
            add("/static/node_modules/immutable/dist/immutable.js");
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

    static final ArrayList<String> eventLibs = new ArrayList<String>() {{


    }};

    static final ArrayList<String> renderLibs = new ArrayList<String>() {{

            add("/static/node_modules/react/dist/react.js");
            add("/static/node_modules/react-dom/dist/react-dom-server.js");
            add("/static/js/components/Nav.js");
            add("/static/js/components/Sidebar.js");
            add("/static/js/components/ContextMenu.js");
            add("/static/js/components/ModalBox.js");
            add("/static/js/components/SlotsTable.js");
            add("/static/js/components/Main.js");

    }};

    void proceed(ArrayList<String> libs) throws URISyntaxException, IOException {
        InputStreamReader reader;
        for (String jsFileName : libs) {
//                System.out.println(jsFileName);
            final Path path = Paths.get(aClass.getResource(jsFileName).toURI());
//                System.out.println(path.toFile().exists());
            reader = new InputStreamReader(aClass.getResourceAsStream(jsFileName));
            cx.evaluateReader(scope, reader, "jsFileName", JS_PARSE_START_LINE, null);
        }
    }

}
