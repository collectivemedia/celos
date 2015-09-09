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
package com.collective.celos.ci.testing.fixtures.compare.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by akonopko on 9/29/14.
 */
public class JsonElementConstructor {

    private final JsonParser jsonParser = new JsonParser();

    public JsonElement construct(String json) {
        return jsonParser.parse(json);
    }

    public JsonElement construct(String json, Set<String> ignorePaths) {
        return deepCopy("root", jsonParser.parse(json), ignorePaths);
    }

    private JsonElement deepCopy(String path, JsonElement el, Set<String> ignorePaths) {
        if (el.isJsonArray()) {
            return deepCopyJsonArray(path, el.getAsJsonArray(), ignorePaths);
        }
        if (el.isJsonObject()) {
            return deepCopyJsonObject(path, el.getAsJsonObject(), ignorePaths);
        }
        if (el.isJsonPrimitive() || el.isJsonNull()) {
            return el;
        }
        throw new IllegalStateException("JSONElement should be either Array, Object, Primitive or Null. So you cannot get here");
    }

    private JsonArray deepCopyJsonArray(String path, JsonArray el, Set<String> ignorePaths) {
        JsonArray array = new JsonArray();
        Iterator<JsonElement> element1Iterator = el.iterator();
        while (element1Iterator.hasNext()) {
            array.add(deepCopy(path, element1Iterator.next(), ignorePaths));
        }
        return array;
    }

    private JsonObject deepCopyJsonObject(String path, JsonObject el, Set<String> ignorePaths) {

        JsonObject result = new JsonObject();
        for (Map.Entry<String, JsonElement> entry : el.entrySet()) {
            String newPath = path + "/" + entry.getKey();
            if (!ignorePaths.contains(newPath)) {
                result.add(entry.getKey(), deepCopy(newPath, entry.getValue(), ignorePaths));
            }
        }
        return result;
    }
}
