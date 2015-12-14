package com.github.obask;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class TryNashorn {

        public static void main(String[] args) throws Exception {
            ScriptEngineManager factory = new ScriptEngineManager();
            ScriptEngine nashorn = factory.getEngineByName("nashorn");
            try {
                System.out.println("Hello, Java!");

                InputStreamReader reader;
                final Class aClass = TryNashorn.class.getClass();
                // load libs
                final ArrayList<String> libs = new ArrayList<>();

//                nashorn.eval("var console = {log: print, error: print};");
                libs.add("/pre.js");

                libs.add("/node_modules/react/dist/react.js");
                libs.add("/node_modules/react-dom/dist/react-dom-server.js");
                libs.add("/node_modules/immutable/dist/immutable.js");
                libs.add("/node_modules/events/events.js");
                libs.add("/node_modules/object-assign/index.js");
                libs.add("/node_modules/js-promise/js-promise.js");

                libs.add("/js/lib.js");
                libs.add("/js/Nav.js");
                libs.add("/js/Dispatcher.js");
                libs.add("/js/stores/sidebarStore.js");
                libs.add("/js/stores/slotsStore.js");
                libs.add("/js/components/Sidebar.js");
                libs.add("/js/components/ContextMenu.js");
                libs.add("/js/components/ModalBox.js");

                libs.add("/js/components/SlotsTable.js");
                libs.add("/js/components/Main.js");

                for (String jsFileName : libs) {
                    reader = new InputStreamReader(aClass.getResourceAsStream(jsFileName));
                    nashorn.eval(reader);
                }
                System.out.println("libs loaded");

                // app
                reader = new InputStreamReader(aClass.getResourceAsStream("/ololo.js"));
                nashorn.eval(reader);

            }
            catch (final ScriptException se) {
                se.printStackTrace();
            }
        }

}
