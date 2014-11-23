package com.collective.celos.ci.testing.fixtures.compare;

import com.collective.celos.ci.config.deploy.CelosCiContext;
import com.collective.celos.ci.testing.fixtures.create.FixObjectCreator;
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
public class RecursiveDirComparer implements FixtureComparer<FixDir> {

    private final FixObjectCreator<FixDir> expectedDataCreator;
    private final FixObjectCreator<FixDir> actualDataCreator;

    public RecursiveDirComparer(FixObjectCreator<FixDir> expectedDataCreator, FixObjectCreator<FixDir> actualDataCreator) {
        this.expectedDataCreator = expectedDataCreator;
        this.actualDataCreator = actualDataCreator;
    }

    public FixObjectCompareResult check(CelosCiContext context) throws Exception {
        return checkInternal(expectedDataCreator.create(context), actualDataCreator.create(context), context);
    }

    public FixObjectCreator<FixDir> getExpectedDataCreator() {
        return expectedDataCreator;
    }

    public FixObjectCreator<FixDir> getActualDataCreator() {
        return actualDataCreator;
    }

    private FixObjectCompareResult checkInternal(FixDir expectedDirTree, FixDir actualDirTree, CelosCiContext context) throws Exception {

        Map<String, FixObjectCompareResult> fails = Maps.newHashMap();
        Map<String, FixObject> expectedChldrn = expectedDirTree.getChildren();
        Map<String, FixObject> actualChldrn = actualDirTree.getChildren();

        String message = getThisDirError(expectedChldrn, actualChldrn);

        for (Map.Entry<String, FixObject> entry : expectedChldrn.entrySet()) {
            FixObject other = actualChldrn.get(entry.getKey());
            if (other != null && other.isFile() == entry.getValue().isFile()) {
                if (entry.getValue().isFile()) {

                    FixFile expFile = entry.getValue().asFile();
                    PlainFileComparer fileComparer = new PlainFileComparer(expFile.getContent(), other.asFile());

                    FixObjectCompareResult compareResult = fileComparer.check(context);
                    if (compareResult.getStatus() == FixObjectCompareResult.Status.FAIL) {
                        fails.put(entry.getKey(), compareResult);
                    }
                } else {
                    FixObjectCompareResult compareResult = checkInternal(entry.getValue().asDir(), other.asDir(), context);
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
