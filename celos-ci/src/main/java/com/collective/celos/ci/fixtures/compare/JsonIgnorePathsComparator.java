package com.collective.celos.ci.fixtures.compare;

import com.google.gson.*;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by akonopko on 9/29/14.
 */
public class JsonIgnorePathsComparator {

    private Set<String> ignorePaths = new HashSet<>();

    public JsonIgnorePathsComparator() {
    }

    public JsonIgnorePathsComparator(Set<String> ignorePaths) {
        this.ignorePaths = ignorePaths;
    }

    public boolean compare(String s1, String s2) {
        JsonElement json1 = new JsonParser().parse(s1);
        JsonElement json2 = new JsonParser().parse(s2);
        return compareJsonElement("root", json1, json2);
    }

    private boolean compareJsonElement(String path, JsonElement el1, JsonElement el2) {
        if (ignorePaths.contains(path)) {
            return true;
        }
        if (el1.isJsonArray()) {
            if (el1.isJsonArray() != el2.isJsonArray()) {
                return false;
            }
            return compareJsonArray(path, el1.getAsJsonArray(), el2.getAsJsonArray());
        }
        if (el1.isJsonObject()) {
            if (el1.isJsonObject() != el2.isJsonObject()) {
                return false;
            }
            return compareJsonObject(path, el1.getAsJsonObject(), el2.getAsJsonObject());
        }
        if (el1.isJsonPrimitive()) {
            if (el1.isJsonPrimitive() != el2.isJsonPrimitive()) {
                return false;
            }
            return compareJsonPrimitive(path, el1.getAsJsonPrimitive(), el2.getAsJsonPrimitive());
        }
        if (el1.isJsonNull()) {
            if (el1.isJsonNull() != el2.isJsonNull()) {
                return false;
            }
            return compareJsonNull(path, el1.getAsJsonNull(), el2.getAsJsonNull());
        }
        throw new IllegalStateException("JSONElement should be either Array, Object, Primitive or Null. So you cannot get here");
    }

    private boolean compareJsonArray(String path, JsonArray el1, JsonArray el2) {
        if (el1.size() != el2.size()) {
            return false;
        }
        Iterator<JsonElement> element1Iterator = el1.iterator();
        Iterator<JsonElement> element2Iterator = el2.iterator();
        while (element1Iterator.hasNext()) {
            if (!compareJsonElement(path, element1Iterator.next(), element2Iterator.next())) {
                return false;
            }
        }
        return true;
    }

    private boolean compareJsonObject(String path, JsonObject el1, JsonObject el2) {

        if (el1.entrySet().size() != el2.entrySet().size()) {
            return false;
        }
        for (Map.Entry<String, JsonElement> entry : el1.entrySet()) {
            String key = entry.getKey();
            if (!el2.has(key)) {
                return false;
            }
            if (!compareJsonElement(path + "/" + key, entry.getValue(), el2.get(key))) {
                return false;
            }
        }
        return true;
    }

    private boolean compareJsonPrimitive(String path, JsonPrimitive el1, JsonPrimitive el2) {
        return el1.equals(el2);
    }

    private boolean compareJsonNull(String path, JsonNull el1, JsonNull el2) {
        return el1.equals(el2);
    }


}
