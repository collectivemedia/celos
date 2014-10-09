package com.collective.celos.ci.fixtures.compare;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.*;

/**
 * Created by akonopko on 9/29/14.
 */
public class JsonEntity {

    private final Set<String> ignorePaths;
    private final JsonElement jsonElement;
    private final String jsonText;

    public JsonEntity(String json) {
        this.ignorePaths = Collections.emptySet();
        this.jsonElement = deepCopy("root", new JsonParser().parse(json));
        this.jsonText = json;
    }

    public JsonEntity(String json, Set<String> ignorePaths) {
        this.ignorePaths = ignorePaths;
        this.jsonElement = deepCopy("root", new JsonParser().parse(json));
        this.jsonText = json;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JsonEntity that = (JsonEntity) o;

        if (!jsonElement.equals(that.jsonElement)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return jsonElement.hashCode();
    }

    private JsonElement deepCopy(String path, JsonElement el) {
        if (el.isJsonArray()) {
            return deepCopyJsonArray(path, el.getAsJsonArray());
        }
        if (el.isJsonObject()) {
            return deepCopyJsonObject(path, el.getAsJsonObject());
        }
        if (el.isJsonPrimitive() || el.isJsonNull()) {
            return el;
        }
        throw new IllegalStateException("JSONElement should be either Array, Object, Primitive or Null. So you cannot get here");
    }

    private JsonArray deepCopyJsonArray(String path, JsonArray el) {
        JsonArray array = new JsonArray();
        Iterator<JsonElement> element1Iterator = el.iterator();
        while (element1Iterator.hasNext()) {
            array.add(deepCopy(path, element1Iterator.next()));
        }
        return array;
    }

    private JsonObject deepCopyJsonObject(String path, JsonObject el) {

        JsonObject result = new JsonObject();
        for (Map.Entry<String, JsonElement> entry : el.entrySet()) {
            String newPath = path + "/" + entry.getKey();
            if (!ignorePaths.contains(newPath)) {
                result.add(entry.getKey(), deepCopy(newPath, entry.getValue()));
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return jsonText;
    }
}
