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
package com.collective.celos.ci.testing.fixtures.convert;

import com.collective.celos.ci.Utils;
import com.collective.celos.ci.testing.fixtures.create.FixObjectCreator;
import com.collective.celos.ci.testing.structure.fixobject.*;
import com.google.common.collect.Maps;
import junit.framework.Assert;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.util.Map;

/**
 * Created by akonopko on 25.01.15.
 */
public class ConversionCreatorTest {

    @Test
    public void testConversionCreatorForFile() throws Exception {
        FixObjectCreator<FixFile> creator = Utils.wrap(new FixFile(IOUtils.toInputStream("lowercase")));
        AbstractFixObjectConverter<FixFile, FixFile> fixObjectConverter = new UpperCaseStringFixFileConverter();
        ConversionCreator<FixFile, FixFile> conversionCreator = new ConversionCreator(creator, fixObjectConverter);

        FixFile result = conversionCreator.create(null);
        Assert.assertEquals("LOWERCASE", IOUtils.toString(result.getContent()));
    }

    @Test
    public void testConversionCreatorForDir() throws Exception {
        FixFile fixFile = new FixFile(IOUtils.toInputStream("lowercase"));
        Map<String, FixFsObject> map = Maps.newHashMap();
        map.put("file", fixFile);
        FixObjectCreator<FixDir> creator = Utils.wrap(new FixDir(map));
        AbstractFixObjectConverter<FixDir, FixDir> fixObjectConverter = new FixDirRecursiveConverter(new UpperCaseStringFixFileConverter());
        ConversionCreator<FixDir, FixDir> conversionCreator = new ConversionCreator(creator, fixObjectConverter);

        FixDir result = conversionCreator.create(null);
        Assert.assertEquals("LOWERCASE", IOUtils.toString(result.getChildren().get("file").asFile().getContent()));
    }

}
