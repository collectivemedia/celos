package com.collective.celos;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.WrapFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mozilla.javascript.tools.shell.Global;

import java.io.IOException;
import java.io.Reader;
import java.util.Map;

/**
 * Created by akonopko on 20.11.14.
 */
public class JSConfigParser {

    public static final int JS_PARSE_START_LINE = 1;

    private final Context context;
    private final ObjectMapper mapper = new ObjectMapper();

    public JSConfigParser() {
        context = Context.enter();
        context.setLanguageVersion(170);

        /**
         * Treat primitives like strings returned from Java methods
         * as native JS objects.
         */
        WrapFactory wf = new WrapFactory();
        wf.setJavaPrimitiveWrap(false);
        context.setWrapFactory(wf);
    }

    public Global createGlobalScope() {
        Global scope = new Global();
        scope.initStandardObjects(context, true);
        Object wrappedMapper = Context.javaToJS(mapper, scope);
        ScriptableObject.putProperty(scope, "celosMapper", wrappedMapper);
        Object wrappedScope = Context.javaToJS(scope, scope);
        ScriptableObject.putProperty(scope, "celosScope", wrappedScope);

        return scope;
    }

    public void putPropertiesInScope(Map<String, Object> jsParameters, Global scope) {
        if (jsParameters != null) {
            for (String key : jsParameters.keySet()) {
                ScriptableObject.putProperty(scope, key, Context.javaToJS(jsParameters.get(key), scope));
            }
        }
    }

    public Object evaluateReader(Scriptable scope, Reader fileReader, String fileName) throws IOException {
        return context.evaluateReader(scope, fileReader, fileName, JS_PARSE_START_LINE, null);
    }

}
