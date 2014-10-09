package com.collective.celos.ci.fixtures.compare;

import com.collective.celos.ci.fixtures.structure.FixDir;
import com.collective.celos.ci.fixtures.structure.FixObject;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * Created by akonopko on 10/7/14.
 */
public class RecursiveDirComparer implements FixObjectComparer<FixDir> {

    public FixObjectCompareResult compare(FixDir expectedDirTree, FixDir actualDirTree) throws Exception {
        Map<String, FixObjectCompareResult> fails = Maps.newHashMap();
        for (Map.Entry<String, FixObject> entry : expectedDirTree.getChildren().entrySet()) {
            FixObject other = actualDirTree.getChildren().get(entry.getKey());
            FixObjectCompareResult compareResult = entry.getValue().compare(other);
            if (compareResult.getStatus() == FixObjectCompareResult.Status.FAIL) {
                fails.put(entry.getKey(), compareResult);
            }
        }
        if (fails.size() > 0) {
            return FixObjectCompareResult.wrapFailed(fails);
        }
        return FixObjectCompareResult.success();
    }

}
