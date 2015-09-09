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

import com.collective.celos.ci.testing.structure.fixobject.FixFile;
import junit.framework.Assert;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.InputStream;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * Created by akonopko on 10/9/14.
 */
public class PlainFileComparerTest {


    @Test
    public void testComparesOk() throws Exception {

        InputStream inputStream1 = IOUtils.toInputStream("stream");
        InputStream inputStream2 = IOUtils.toInputStream("stream");

        FixFile fixFile1 = mock(FixFile.class);
        PlainFileComparer comparer = new PlainFileComparer(inputStream2, fixFile1);

        doReturn(inputStream1).when(fixFile1).getContent();
        FixObjectCompareResult compareResult = comparer.check(null);
        Assert.assertEquals(compareResult, FixObjectCompareResult.SUCCESS);
    }

    @Test
    public void testCompareFails() throws Exception {

        InputStream inputStream1 = IOUtils.toInputStream("stream");
        InputStream inputStream2 = IOUtils.toInputStream("stream2");

        FixFile fixFile1 = mock(FixFile.class);
        PlainFileComparer comparer = new PlainFileComparer(inputStream2, fixFile1);

        doReturn(inputStream1).when(fixFile1).getContent();
        FixObjectCompareResult compareResult = comparer.check(null);

        Assert.assertEquals(compareResult.getStatus(), FixObjectCompareResult.Status.FAIL);
    }


}
