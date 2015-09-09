/*
 * Copyright 2015 Collective, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.collective.celos.ci.testing.fixtures.compare;

import com.collective.celos.ci.mode.test.TestRun;
import com.collective.celos.ci.testing.fixtures.create.FixObjectCreator;
import com.collective.celos.ci.testing.structure.fixobject.FixFsObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.lang.text.StrBuilder;

import java.util.*;

/**
 * Created by akonopko on 10/7/14.
 */
public class RecursiveFsObjectComparer implements FixtureComparer {

    private final FixObjectCreator<? extends FixFsObject> expectedDataCreator;
    private final FixObjectCreator<? extends FixFsObject> actualDataCreator;

    public RecursiveFsObjectComparer(FixObjectCreator<? extends FixFsObject> expectedDataCreator, FixObjectCreator<? extends FixFsObject> actualDataCreator) {
        this.expectedDataCreator = expectedDataCreator;
        this.actualDataCreator = actualDataCreator;
    }

    public FixObjectCompareResult check(TestRun testRun) throws Exception {
        FixFsObject expected = expectedDataCreator.create(testRun);
        FixFsObject actual = actualDataCreator.create(testRun);

        if (expected.isDir() && actual.isDir()) {
            return checkDir(testRun, expected, actual);
        } else if (expected.isFile() && actual.isFile()) {
            return checkFile(testRun, expected, actual);
        } else {
            return FixObjectCompareResult.failed(getWrongTypeDesc("RecursiveFsObjectComparer", expected, actual));
        }

    }

    public FixObjectCreator<? extends FixFsObject> getExpectedDataCreator() {
        return expectedDataCreator;
    }

    public FixObjectCreator<? extends FixFsObject> getActualDataCreator() {
        return actualDataCreator;
    }

    private FixObjectCompareResult checkFile(TestRun testRun, FixFsObject expected, FixFsObject actual) throws Exception {
        PlainFileComparer fileComparer = new PlainFileComparer(expected.asFile().getContent(), actual.asFile());

        return fileComparer.check(testRun);
    }

    private FixObjectCompareResult checkDir(TestRun testRun, FixFsObject expectedDirTree, FixFsObject actualDirTree) throws Exception {

        Map<String, FixFsObject> expectedChldrn = expectedDirTree.getChildren();
        Map<String, FixFsObject> actualChldrn = actualDirTree.getChildren();

        FixObjectCompareResult contentResult = checkDirFileList(expectedChldrn, actualChldrn);
        Map<String, FixObjectCompareResult> fails = checkDirChildren(testRun, expectedChldrn, actualChldrn);

        if (!fails.isEmpty() || contentResult.getStatus() == FixObjectCompareResult.Status.FAIL) {
            return FixObjectCompareResult.wrapFailed(fails, contentResult.getMessage());
        }
        return FixObjectCompareResult.SUCCESS;
    }



    private FixObjectCompareResult checkDirFileList(Map<String, FixFsObject> expectedChldrn, Map<String, FixFsObject> actualChldrn) {
        Set<String> expectedDiff = Sets.difference(expectedChldrn.keySet(), actualChldrn.keySet());
        Set<String> actualDiff = Sets.difference(actualChldrn.keySet(), expectedChldrn.keySet());

        List<String> filesWithDifferentTypes = Lists.newArrayList();
        for (String key : Sets.intersection(expectedChldrn.keySet(), actualChldrn.keySet())) {
            FixFsObject expected = expectedChldrn.get(key);
            FixFsObject actual = actualChldrn.get(key);
            if (expected.isFile() != actual.isFile()) {
                String message = getWrongTypeDesc(key, expected, actual);
                filesWithDifferentTypes.add(message);
            }
        }

        if (expectedDiff.isEmpty() && actualDiff.isEmpty() && filesWithDifferentTypes.isEmpty()) {
            return FixObjectCompareResult.SUCCESS;
        } else {
            StrBuilder strBuilder = new StrBuilder();
            appendMessages(expectedDiff, strBuilder, "Files found only in expected set: ");
            appendMessages(actualDiff, strBuilder, "Files found only in result set: ");
            appendMessages(filesWithDifferentTypes, strBuilder, "Files have different types: ");
            return FixObjectCompareResult.failed(strBuilder.toString());
        }
    }

    private String getWrongTypeDesc(String key, FixFsObject expected, FixFsObject actual) {
        return key + ": expected is [" + getTypeDescr(expected) + "] and actual is [" + getTypeDescr(actual) + "]";
    }


    private Map<String, FixObjectCompareResult> checkDirChildren(TestRun testRun, Map<String, FixFsObject> expectedChldrn, Map<String, FixFsObject> actualChldrn) throws Exception {
        Map<String, FixObjectCompareResult> fails = Maps.newHashMap();
        for (Map.Entry<String, FixFsObject> entry : expectedChldrn.entrySet()) {
            FixFsObject other = actualChldrn.get(entry.getKey());
            if (other != null && other.isFile() == entry.getValue().isFile()) {
                if (entry.getValue().isFile()) {

                    FixObjectCompareResult compareResult = checkFile(testRun, entry.getValue(), other);
                    if (compareResult.getStatus() == FixObjectCompareResult.Status.FAIL) {
                        fails.put(entry.getKey(), compareResult);
                    }
                } else {
                    FixObjectCompareResult compareResult = checkDir(testRun, entry.getValue().asDir(), other.asDir());
                    if (compareResult.getStatus() == FixObjectCompareResult.Status.FAIL) {
                        fails.put(entry.getKey(), compareResult);
                    }
                }
            }
        }
        return fails;
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

    private String getTypeDescr(FixFsObject fo) {
        return fo.isFile() ? "File" : "Dir";
    }
}
