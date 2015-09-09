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
package com.collective.celos;

import junit.framework.Assert;
import org.junit.Test;

import java.net.URISyntaxException;

/**
 * Created by akonopko on 09.12.14.
 */
public class AugmentedHdfsPathTest {

    @Test
    public void testAugmentedHdfsPath() throws URISyntaxException {
        Assert.assertEquals(Util.augmentHdfsPath("/myprefix", "/originalPath/${var1}/path/${var2}"), "/myprefix/originalPath/${var1}/path/${var2}");
        Assert.assertEquals(Util.augmentHdfsPath("/myprefix", "hdfs:/originalPath/${var1}/path/${var2}"), "hdfs:/myprefix/originalPath/${var1}/path/${var2}");
        Assert.assertEquals(Util.augmentHdfsPath("/myprefix", "hdfs:///originalPath/${var1}/path/${var2}"), "hdfs:///myprefix/originalPath/${var1}/path/${var2}");
        Assert.assertEquals(Util.augmentHdfsPath("/myprefix", "hdfs://nameservice/originalPath/${var1}/path/${var2}"), "hdfs://nameservice/myprefix/originalPath/${var1}/path/${var2}");
        Assert.assertEquals(Util.augmentHdfsPath("/myprefix", "hdfs://nameservice:2345/originalPath/${var1}/path/${var2}"), "hdfs://nameservice:2345/myprefix/originalPath/${var1}/path/${var2}");

        Assert.assertEquals(Util.augmentHdfsPath("/", "/originalPath/${var1}/path/${var2}"), "/originalPath/${var1}/path/${var2}");
        Assert.assertEquals(Util.augmentHdfsPath("/", "hdfs:/originalPath/${var1}/path/${var2}"), "hdfs:/originalPath/${var1}/path/${var2}");
        Assert.assertEquals(Util.augmentHdfsPath("/", "hdfs:///originalPath/${var1}/path/${var2}"), "hdfs:///originalPath/${var1}/path/${var2}");
        Assert.assertEquals(Util.augmentHdfsPath("/", "hdfs://nameservice/originalPath/${var1}/path/${var2}"), "hdfs://nameservice/originalPath/${var1}/path/${var2}");
        Assert.assertEquals(Util.augmentHdfsPath("/", "hdfs://nameservice:2345/originalPath/${var1}/path/${var2}"), "hdfs://nameservice:2345/originalPath/${var1}/path/${var2}");
    }

}
