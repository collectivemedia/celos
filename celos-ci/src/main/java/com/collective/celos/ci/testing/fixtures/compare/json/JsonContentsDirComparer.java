package com.collective.celos.ci.testing.fixtures.compare.json;

import com.collective.celos.ci.mode.test.TestRun;
import com.collective.celos.ci.testing.fixtures.compare.FixObjectCompareResult;
import com.collective.celos.ci.testing.fixtures.compare.FixtureComparer;
import com.collective.celos.ci.testing.structure.fixobject.FixDir;
import com.collective.celos.ci.testing.structure.fixobject.FixObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by akonopko on 10/7/14.
 */
public class JsonContentsDirComparer implements FixtureComparer {

    private final JsonElementConstructor constructor;
    private final Set<String> ignorePaths;
    private final InputStream content;
    private final FixDir fixDir;

    public JsonContentsDirComparer(Set<String> ignorePaths, InputStream content, FixDir fixDir) {
        this.ignorePaths = ignorePaths;
        this.content = content;
        this.fixDir = fixDir;
        this.constructor = new JsonElementConstructor();
    }

    public FixObjectCompareResult check(TestRun testRun) throws Exception {

        Map<JsonElement, Integer> actualRes = getJsonEntityCountMap(fixDir.getChildren());
        Map<JsonElement, Integer> expectedRes = Maps.newHashMap();
        fillMapWithJsonFromIS(expectedRes, content);

        Set<Map.Entry<JsonElement,Integer>> actualDiffers = Sets.difference(actualRes.entrySet(), expectedRes.entrySet());
        Set<Map.Entry<JsonElement,Integer>> expectedDiffers = Sets.difference(expectedRes.entrySet(), actualRes.entrySet());

        if (actualDiffers.size() + expectedDiffers.size() > 0) {
            String actualDescr = getDifferenceDescr(actualDiffers);
            String expectedDescr = getDifferenceDescr(expectedDiffers);
            return FixObjectCompareResult.failed(
                    "Diff:\n" +
                            "Actual differs:\n" +
                            actualDescr + "\n" +
                            "Expected differs:\n" +
                            expectedDescr);

        }

        return FixObjectCompareResult.success();
    }

    private <T extends FixObject> Map<JsonElement, Integer> getJsonEntityCountMap(Map<String, FixObject> children) throws IOException {
        Map<JsonElement, Integer> expectedRes = Maps.newHashMap();

        for (Map.Entry<String, FixObject> entry : children.entrySet()) {
            if (!entry.getValue().isFile()) {
                throw new RuntimeException(getClass().getName() + " only works with directories with no sub-dirs");
            }
            InputStream content = entry.getValue().asFile().getContent();
            fillMapWithJsonFromIS(expectedRes, content);
        }
        return expectedRes;
    }

    private void fillMapWithJsonFromIS(Map<JsonElement, Integer> expectedRes, InputStream content) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(content));
        String line;
        while ((line = reader.readLine()) != null) {
            JsonElement entity = constructor.construct(line, ignorePaths);
            Integer cnt = expectedRes.get(entity);
            expectedRes.put(entity, cnt == null ? 1 : cnt + 1);
        }
    }

    private String getDifferenceDescr(Set<Map.Entry<JsonElement,Integer>> entrySet) {
        List<String> strs = Lists.newArrayList();
        for (Map.Entry<JsonElement, Integer> entry : entrySet) {
            strs.add(entry.getKey().toString() + " [" + entry.getValue() + " times]");
        }
        Collections.sort(strs);
        return StringUtils.join(strs, "\n");
    }

}
