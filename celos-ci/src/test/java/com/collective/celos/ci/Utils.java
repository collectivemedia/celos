package com.collective.celos.ci;

import com.collective.celos.ci.mode.test.TestRun;
import com.collective.celos.ci.testing.fixtures.create.FixObjectCreator;
import com.collective.celos.ci.testing.structure.fixobject.FixFsObject;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * Created by akonopko on 07.12.14.
 */
public class Utils {

    public static <T extends FixFsObject> FixObjectCreator<T> wrap(final T fixObj) {
        return wrap(fixObj, "");
    }

    public static <T extends FixFsObject> FixObjectCreator<T> wrap(final T fixObj, final String desc) {
        return new FixObjectCreator() {
            @Override
            public FixFsObject create(TestRun testRun) throws Exception {
                return fixObj;
            }

            @Override
            public String getDescription(TestRun testRun) {
                return desc;
            }
        };
    }

    private static final JsonParser jsonParser = new JsonParser();

    public static Map<JsonElement, Integer> fillMapWithJsonFromIS(InputStream content) throws IOException {
        Map<JsonElement, Integer> jsonElems = Maps.newHashMap();
        BufferedReader reader = new BufferedReader(new InputStreamReader(content));
        String line;
        while ((line = reader.readLine()) != null) {
            JsonElement entity = jsonParser.parse(line) ;
            Integer cnt = jsonElems.get(entity);
            jsonElems.put(entity, cnt == null ? 1 : cnt + 1);
        }
        return jsonElems;
    }


}
