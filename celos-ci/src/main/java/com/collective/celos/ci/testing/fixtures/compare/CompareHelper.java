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
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by akonopko on 13.02.15.
 */
public class CompareHelper {

    public static <T> FixObjectCompareResult compareEntityNumber(TestRun testRun, FixObjectCreator actualDataCreator, FixObjectCreator expectedDataCreator, Map<T, Integer> expectedRes, Map<T, Integer> actualRes) throws Exception {
        Set<Map.Entry<T, Integer>> expectedDiffers = Sets.difference(expectedRes.entrySet(), actualRes.entrySet());
        Set<Map.Entry<T, Integer>> actualDiffers = Sets.difference(actualRes.entrySet(), expectedRes.entrySet());

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
        return FixObjectCompareResult.SUCCESS;
    }

    private static <T> String getDifference(Set<Map.Entry<T, Integer>> entrySet) {
        List<String> strs = Lists.newArrayList();
        for (Map.Entry<T, Integer> entry : entrySet) {
            strs.add(entry.getKey().toString() + " [" + entry.getValue() + " times]");
        }
        Collections.sort(strs);
        return StringUtils.join(strs, "\n");
    }

}
