package com.collective.celos.js;

import org.mozilla.javascript.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;


public class StaticRenderTest {


    // TODO select some slot to check sidebar render


    private static final ArrayList<String> RENDER_LIBS = new ArrayList<String>() {{
        add("/static/node_modules/react/dist/react.min.js");
        add("/static/node_modules/react-dom/dist/react-dom-server.js");
        add("/static/js/components/Nav.js");
        add("/static/js/components/Sidebar.js");
        add("/static/js/components/ContextMenu.js");
        add("/static/js/components/ModalBox.js");
        add("/static/js/components/SlotsTable.js");
        add("/static/js/components/Main.js");
    }};


    @Test
    public void StaticRenderRhino() throws IOException, URISyntaxException {
        Context.enter();
        try {
            final LibLoader libLoader = new LibLoader();
            libLoader.proceedCommonLibs();
            libLoader.evaluateLibs(RENDER_LIBS);

            final Object result = libLoader.evaluateResource("/static/set-initial-state.js");

            libLoader.evaluateString("var tmp = React.createElement(CelosMainFetch, { url: \"/main\", request: request });");
            libLoader.evaluateString("var result = ReactDOMServer.renderToStaticMarkup(tmp);");
            libLoader.evaluateString("console.log(result);");

            // Convert the result to a string and print it.
            System.err.println(Context.toString(result));
            Assert.assertEquals(Context.toString(result), "undefined");

        } finally {
            Context.exit();
        }
    }

}
