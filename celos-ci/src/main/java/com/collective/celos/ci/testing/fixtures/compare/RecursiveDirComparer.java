package com.collective.celos.ci.testing.fixtures.compare;

import com.collective.celos.ci.testing.structure.fixobject.FixDir;
import com.collective.celos.ci.testing.structure.fixobject.FixFile;
import com.collective.celos.ci.testing.structure.fixobject.FixObject;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrBuilder;
import java.util.Map;
import java.util.Set;

/**
 * Created by akonopko on 10/7/14.
 */
public class RecursiveDirComparer implements Comparer<FixDir> {

    private final FixDir expectedDirTree;

    public RecursiveDirComparer(FixDir compareWith) {
        this.expectedDirTree = compareWith;
    }

    public FixObjectCompareResult check(FixDir actualDirTree) throws Exception {

        Map<String, FixObjectCompareResult> fails = Maps.newHashMap();
        Map<String, FixObject> expectedChldrn = expectedDirTree.getChildren();
        Map<String, FixObject> actualChldrn = actualDirTree.getChildren();

        String message = getThisDirError(expectedChldrn, actualChldrn);

        for (Map.Entry<String, FixObject> entry : expectedChldrn.entrySet()) {
            if (entry.getValue().isFile()) {

                FixFile expFile = entry.getValue().asFile();
                FixFile other = actualChldrn.get(entry.getKey()).asFile();

                PlainFileComparer fileComparer = new PlainFileComparer(expFile.getContent());

                FixObjectCompareResult compareResult = fileComparer.check(other);
                if (compareResult.getStatus() == FixObjectCompareResult.Status.FAIL) {
                    fails.put(entry.getKey(), compareResult);
                }
            } else {

            }
        }
        if (!fails.isEmpty() || !StringUtils.isEmpty(message)) {
            return FixObjectCompareResult.wrapFailed(fails, message);
        }
        return FixObjectCompareResult.success();
    }

    private String getThisDirError(Map<String, FixObject> expectedChldrn, Map<String, FixObject> actualChldrn) {
        Set<String> expectedDiff = Sets.difference(expectedChldrn.keySet(), actualChldrn.keySet());
        Set<String> actualDiff = Sets.difference(actualChldrn.keySet(), expectedChldrn.keySet());
        StrBuilder strBuilder = new StrBuilder();

        if (expectedDiff.size() > 0) {
            strBuilder.append("Files found only in expected set: ");
            strBuilder.appendWithSeparators(expectedDiff, ", ");
        }
        if (actualDiff.size() > 0) {
            if (!strBuilder.isEmpty()) {
                strBuilder.appendNewLine();
            }
            strBuilder.append("Files found only in result set: ");
            strBuilder.appendWithSeparators(actualDiff, ", ");
        }
        return strBuilder.toString();
    }

}
