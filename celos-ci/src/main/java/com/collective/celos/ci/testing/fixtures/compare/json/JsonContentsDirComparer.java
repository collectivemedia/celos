package com.collective.celos.ci.testing.fixtures.compare.json;

import com.collective.celos.ci.mode.test.TestRun;
import com.collective.celos.ci.testing.fixtures.compare.FixObjectCompareResult;
import com.collective.celos.ci.testing.fixtures.compare.FixtureComparer;
import com.collective.celos.ci.testing.fixtures.create.FixObjectCreator;
import com.collective.celos.ci.testing.structure.fixobject.FixDir;
import com.collective.celos.ci.testing.structure.fixobject.FixFile;
import com.collective.celos.ci.testing.structure.fixobject.FixFsObject;
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
    private final FixObjectCreator<FixFile> expectedDataCreator;
    private final FixObjectCreator<FixDir> actualDataCreator;

    public JsonContentsDirComparer(Set<String> ignorePaths, FixObjectCreator<FixFile> expectedDataCreator, FixObjectCreator<FixDir> actualDataCreator) {
        this.ignorePaths = ignorePaths;
        this.expectedDataCreator = expectedDataCreator;
        this.actualDataCreator = actualDataCreator;
        this.constructor = new JsonElementConstructor();
    }

    public FixObjectCompareResult check(TestRun testRun) throws Exception {

        Map<JsonElement, Integer> actualRes = getJsonEntityCountMap(actualDataCreator.create(testRun).asDir().getChildren());
        Map<JsonElement, Integer> expectedRes = Maps.newHashMap();
        fillMapWithJsonFromIS(expectedRes, expectedDataCreator.create(testRun).getContent());

        Set<Map.Entry<JsonElement,Integer>> actualDiffers = Sets.difference(actualRes.entrySet(), expectedRes.entrySet());
        Set<Map.Entry<JsonElement,Integer>> expectedDiffers = Sets.difference(expectedRes.entrySet(), actualRes.entrySet());

        if (actualDiffers.size() + expectedDiffers.size() > 0) {
            String actualDir = getDifference(actualDiffers);
            String expectedDiff = getDifference(expectedDiffers);
            return FixObjectCompareResult.failed(
                    "Diff:\n" +
                            "Actual [" + actualDataCreator.getDescription(testRun) + "]:\n" +
                            actualDir + "\n" +
                            "Expected [" + expectedDataCreator.getDescription(testRun) + "]:\n" +
                            expectedDiff);

        }

        return FixObjectCompareResult.success();
    }

    private <T extends FixFsObject> Map<JsonElement, Integer> getJsonEntityCountMap(Map<String, FixFsObject> children) throws IOException {
        Map<JsonElement, Integer> expectedRes = Maps.newHashMap();

        for (Map.Entry<String, FixFsObject> entry : children.entrySet()) {
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

    private String getDifference(Set<Map.Entry<JsonElement, Integer>> entrySet) {
        List<String> strs = Lists.newArrayList();
        for (Map.Entry<JsonElement, Integer> entry : entrySet) {
            strs.add(entry.getKey().toString() + " [" + entry.getValue() + " times]");
        }
        Collections.sort(strs);
        return StringUtils.join(strs, "\n");
    }

    public Set<String> getIgnorePaths() {
        return ignorePaths;
    }
}
