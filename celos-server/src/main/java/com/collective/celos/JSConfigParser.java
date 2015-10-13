/*
 * Copyright 2015 Collective, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.collective.celos;

import org.mozilla.javascript.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mozilla.javascript.tools.shell.Global;

import java.io.IOException;
import java.io.Reader;
import java.util.Map;

/**
 * Massively misnamed, this is in fact a JavaScript evaluator.
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
                ScriptableObject.putProperty(scope, key, jsParameters.get(key));
            }
        }
    }

    public Object evaluateReader(Scriptable scope, Reader fileReader, String fileName) throws IOException {
        return context.evaluateReader(scope, fileReader, fileName, JS_PARSE_START_LINE, null);
    }


    public void validateJsSyntax(Reader fileReader, String fileName) throws IOException {
        context.compileReader(fileReader, fileName, JS_PARSE_START_LINE, null);
    }

}
