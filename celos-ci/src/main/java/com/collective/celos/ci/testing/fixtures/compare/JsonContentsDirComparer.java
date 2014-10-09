package com.collective.celos.ci.testing.fixtures.compare;

import com.collective.celos.ci.testing.structure.fixobject.FixDir;
import com.collective.celos.ci.testing.structure.fixobject.FixFile;
import com.collective.celos.ci.testing.structure.fixobject.FixObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by akonopko on 10/7/14.
 */
public class JsonContentsDirComparer implements FixObjectComparer<FixDir> {

    private final Set<String> ignorePaths;

    public JsonContentsDirComparer(Set<String> ignorePaths) {
        this.ignorePaths = ignorePaths;
    }

    public FixObjectCompareResult compare(FixDir expectedDirTree, FixDir actualDirTree) throws Exception {

        Map<JsonEntity, Integer> actualRes = getJsonEntityCountMap(actualDirTree);
        Map<JsonEntity, Integer> expectedRes = getJsonEntityCountMap(expectedDirTree);

        Set<Map.Entry<JsonEntity,Integer>> actualDiffers = Sets.difference(actualRes.entrySet(), expectedRes.entrySet());
        Set<Map.Entry<JsonEntity,Integer>> expectedDiffers = Sets.difference(expectedRes.entrySet(), actualRes.entrySet());

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

    private Map<JsonEntity, Integer> getJsonEntityCountMap(FixDir expectedDirTree) throws IOException {
        Map<JsonEntity, Integer> expectedRes = Maps.newHashMap();

        for (Map.Entry<String, FixObject> entry : expectedDirTree.getChildren().entrySet()) {
            FixFile exp = entry.getValue().asFixFile();

            BufferedReader reader = new BufferedReader(new InputStreamReader(exp.getContent()));
            String line;
            while ((line = reader.readLine()) != null) {
                JsonEntity entity = new JsonEntity(line, ignorePaths);
                Integer cnt = expectedRes.get(entity);
                expectedRes.put(entity, cnt == null ? 1 : cnt + 1);
            }
        }
        return expectedRes;
    }

    private String getDifferenceDescr(Set<Map.Entry<JsonEntity,Integer>> entrySet) {
        List<String> strs = Lists.newArrayList();
        for (Map.Entry<JsonEntity, Integer> entry : entrySet) {
            strs.add(entry.getKey().toString() + " [" + entry.getValue() + " times]");
        }
        Collections.sort(strs);
        return StringUtils.join(strs, "\n");
    }

}
