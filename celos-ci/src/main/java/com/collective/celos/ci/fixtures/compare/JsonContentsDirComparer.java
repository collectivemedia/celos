package com.collective.celos.ci.fixtures.compare;

import com.collective.celos.ci.fixtures.structure.FixDir;
import com.collective.celos.ci.fixtures.structure.FixFile;
import com.collective.celos.ci.fixtures.structure.FixObject;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import org.apache.commons.lang.text.StrBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

        Map<JsonEntity, Integer> actualRes = Maps.newHashMap();
        Map<JsonEntity, Integer> expectedRes = Maps.newHashMap();

        for (Map.Entry<String, FixObject> entry : expectedDirTree.getChildren().entrySet()) {
            FixFile act = actualDirTree.getChildren().get(entry.getKey()).asFixFile();
            FixFile exp = entry.getValue().asFixFile();

            fillResultsMap(expectedRes, exp);
            fillResultsMap(actualRes, act);
        }

        MapDifference<JsonEntity, Integer> difference = Maps.difference(actualRes, expectedRes);
        if (difference.entriesDiffering().size() > 0) {
            String actualDescr = getDifferenceDescr(difference.entriesOnlyOnLeft());
            String expectedDescr = getDifferenceDescr(difference.entriesOnlyOnRight());
            return FixObjectCompareResult.failed(
                    "Diff:\n" +
                    "Actual:\n" +
                    actualDescr + "\n" +
                    "Expected:\n" +
                    expectedDescr);
        }
        return FixObjectCompareResult.success();
    }

    private String getDifferenceDescr(Map<JsonEntity, Integer> map) {
        StrBuilder strBuilder = new StrBuilder();
        for (Map.Entry<JsonEntity, Integer> entry : map.entrySet()) {
            strBuilder.appendln(entry.getKey().toString() + " [" +entry.getValue() + " times]");
        }
        return strBuilder.toString();
    }

    private void fillResultsMap(Map<JsonEntity, Integer> otherRslts, FixFile other) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(other.getContent()));
        String line;
        while ((line = reader.readLine()) != null) {
            JsonEntity entity = new JsonEntity(line, ignorePaths);
            Integer cnt = otherRslts.get(entity);
            otherRslts.put(entity, cnt == null ? 0 : cnt + 1);
        }
    }

}
