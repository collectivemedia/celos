package com.collective.celos.ci.testing.fixtures.compare;

import com.collective.celos.ci.testing.structure.fixobject.FixDir;
import com.collective.celos.ci.testing.structure.fixobject.FixFile;
import com.collective.celos.ci.testing.structure.fixobject.FixObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrBuilder;

import java.util.*;

/**
 * Created by akonopko on 10/7/14.
 */
public class RecursiveDirComparer implements Comparer<FixDir> {

    private final FixDir expectedDirTree;

    public RecursiveDirComparer(FixDir compareWith) {
        this.expectedDirTree = compareWith;
    }

    public FixObjectCompareResult check(FixDir actualDirTree) throws Exception {
        return checkInternal(expectedDirTree, actualDirTree);
    }

    public FixObjectCompareResult checkInternal(FixDir expectedDirTree, FixDir actualDirTree) throws Exception {

        Map<String, FixObjectCompareResult> fails = Maps.newHashMap();
        Map<String, FixObject> expectedChldrn = expectedDirTree.getChildren();
        Map<String, FixObject> actualChldrn = actualDirTree.getChildren();

        String message = getThisDirError(expectedChldrn, actualChldrn);

        for (Map.Entry<String, FixObject> entry : expectedChldrn.entrySet()) {
            FixObject other = actualChldrn.get(entry.getKey());
            if (other != null && other.isFile() == entry.getValue().isFile()) {
                if (entry.getValue().isFile()) {

                    FixFile expFile = entry.getValue().asFile();
                    PlainFileComparer fileComparer = new PlainFileComparer(expFile.getContent());

                    FixObjectCompareResult compareResult = fileComparer.check(other.asFile());
                    if (compareResult.getStatus() == FixObjectCompareResult.Status.FAIL) {
                        fails.put(entry.getKey(), compareResult);
                    }
                } else {
                    FixObjectCompareResult compareResult = checkInternal(entry.getValue().asDir(), other.asDir());
                    if (compareResult.getStatus() == FixObjectCompareResult.Status.FAIL) {
                        fails.put(entry.getKey(), compareResult);
                    }
                }
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

        appendMessages(expectedDiff, strBuilder, "Files found only in expected set: ");
        appendMessages(actualDiff, strBuilder, "Files found only in result set: ");

        List<String> filesWithDifferentTypes = Lists.newArrayList();
        for (String key : Sets.intersection(expectedChldrn.keySet(), actualChldrn.keySet())) {
            if (expectedChldrn.get(key).isFile() != actualChldrn.get(key).isFile()) {
                String message = key + ": expected is [" + getTypeDescr(expectedChldrn.get(key)) + "] and actual is [" + getTypeDescr(actualChldrn.get(key)) + "]";
                filesWithDifferentTypes.add(message);
            }
        }
        appendMessages(filesWithDifferentTypes, strBuilder, "Files have different types: ");
        return strBuilder.toString();
    }

    private void appendMessages(Collection<String> messages, StrBuilder strBuilder, String header) {
        if (messages.size() == 0) {
            return;
        }
        if (!strBuilder.isEmpty()) {
            strBuilder.appendNewLine();
        }
        strBuilder.append(header);
        List<String> sortedMessages = Lists.newArrayList(messages);
        Collections.sort(sortedMessages);
        strBuilder.appendWithSeparators(sortedMessages, ", ");
    }

    private String getTypeDescr(FixObject fo) {
        return fo.isFile() ? "File" : "Dir";
    }
}
