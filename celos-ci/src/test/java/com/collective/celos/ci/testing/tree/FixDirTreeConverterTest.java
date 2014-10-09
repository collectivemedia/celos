package com.collective.celos.ci.testing.tree;

import com.collective.celos.ci.testing.fixtures.compare.FixObjectCompareResult;
import com.collective.celos.ci.testing.fixtures.compare.PlainFileComparer;
import com.collective.celos.ci.testing.fixtures.compare.RecursiveDirComparer;
import com.collective.celos.ci.testing.structure.fixobject.*;
import com.google.common.collect.Maps;
import junit.framework.Assert;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Created by akonopko on 10/7/14.
 */
public class FixDirTreeConverterTest {

    @Test
    public void testFixDirTreeConverter() throws Exception {
        FixDirTreeConverter converter = new FixDirTreeConverter();

        FixDir dir = createDirWithFileContent("lowercase");
        FixDir expected = createDirWithFileContent("lowercase".toUpperCase());

        FixDir transformed = converter.transform(dir, new ReverseStringFixFileConverter());
        RecursiveDirComparer comparer = new RecursiveDirComparer();
        FixObjectCompareResult result = comparer.compare(transformed, expected);

        Assert.assertEquals(result.getStatus(), FixObjectCompareResult.Status.SUCCESS);
    }

    private FixDir createDirWithFileContent(String fileContent) {
        FixDir dir1 = createDirWithSubdirsAndFile(fileContent);
        InputStream inputStream2 = IOUtils.toInputStream(fileContent);
        FixFile file = new FixFile(inputStream2, new PlainFileComparer());

        Map<String, FixObject> content = Maps.newHashMap();
        content.put("dir1", dir1);
        content.put("file", file);
        return new FixDir(content, new RecursiveDirComparer());
    }

    private static class ReverseStringFixFileConverter extends AbstractFixFileConverter {

        @Override
        public FixFile convert(FixFile ff) throws IOException {
            String newContent = IOUtils.toString(ff.getContent()).toUpperCase();
            return new FixFile(IOUtils.toInputStream(newContent), ff.getComparer());
        }
    }

    private FixDir createDirWithSubdirsAndFile(String fileContent) {
        FixDir dir1 = getFixDirWithTwoFiles1(fileContent);
        FixDir dir2 = getFixDirWithTwoFiles1(fileContent);
        InputStream inputStream2 = IOUtils.toInputStream(fileContent);
        FixFile file = new FixFile(inputStream2, new PlainFileComparer());

        Map<String, FixObject> content = Maps.newHashMap();
        content.put("dir1", dir1);
        content.put("dir2", dir2);
        content.put("file", file);
        return new FixDir(content, new RecursiveDirComparer());
    }

    private FixDir getFixDirWithTwoFiles1(String fileContent) {
        InputStream inputStream1 = IOUtils.toInputStream(fileContent);
        FixFile file1 = new FixFile(inputStream1, new PlainFileComparer());

        InputStream inputStream2 = IOUtils.toInputStream(fileContent);
        FixFile file2 = new FixFile(inputStream2, new PlainFileComparer());

        Map<String, FixObject> content1 = Maps.newHashMap();
        content1.put("file1", file1);
        content1.put("file2", file2);
        return new FixDir(content1, new RecursiveDirComparer());
    }

}
