package com.collective.celos.ci.testing.compare;

import com.collective.celos.ci.testing.fixtures.compare.FixObjectCompareResult;
import com.collective.celos.ci.testing.fixtures.compare.PlainFileComparer;
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

        PlainFileComparer comparer = new PlainFileComparer(inputStream2);
        FixFile fixFile1 = mock(FixFile.class);

        doReturn(inputStream1).when(fixFile1).getContent();
        FixObjectCompareResult compareResult = comparer.check(fixFile1);
        Assert.assertEquals(compareResult, FixObjectCompareResult.success());
    }

    @Test
    public void testCompareFails() throws Exception {

        InputStream inputStream1 = IOUtils.toInputStream("stream");
        InputStream inputStream2 = IOUtils.toInputStream("stream2");

        PlainFileComparer comparer = new PlainFileComparer(inputStream2);
        FixFile fixFile1 = mock(FixFile.class);

        doReturn(inputStream1).when(fixFile1).getContent();
        FixObjectCompareResult compareResult = comparer.check(fixFile1);

        Assert.assertEquals(compareResult.getStatus(), FixObjectCompareResult.Status.FAIL);
    }


}
