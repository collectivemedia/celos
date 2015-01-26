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
    public void testConvertionCreatorForFile() throws Exception {
        FixObjectCreator<FixFile> creator = Utils.wrap(new FixFile(IOUtils.toInputStream("lowercase")));
        AbstractFixObjectConverter<FixFile, FixFile> fixObjectConverter = new UpperCaseStringFixFileConverter();
        ConversionCreator<FixFile, FixFile> conversionCreator = new ConversionCreator(creator, fixObjectConverter);

        FixFile result = conversionCreator.create(null);
        Assert.assertEquals("LOWERCASE", IOUtils.toString(result.getContent()));
    }

    @Test
    public void testConvertionCreatorForDir() throws Exception {
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
