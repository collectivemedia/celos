package com.collective.celos.ci.testing.fixtures.compare;

import com.collective.celos.ci.Utils;
import com.collective.celos.ci.mode.test.TestRun;
import com.collective.celos.ci.testing.fixtures.compare.json.JsonContentsDirComparer;
import com.collective.celos.ci.testing.fixtures.create.FixDirFromResourceCreator;
import com.collective.celos.ci.testing.structure.fixobject.FixDir;
import com.collective.celos.ci.testing.structure.fixobject.FixFile;
import com.google.common.collect.Sets;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.InputStream;
import java.util.Collections;
import java.util.Set;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * Created by akonopko on 10/9/14.
 */
public class JsonContentsDirComparerTest {

    private TestRun testRun;

    @Before
    public void setUp() {
        testRun = mock(TestRun.class);
        doReturn(new File("/")).when(testRun).getTestCasesDir();
    }

    @Test
    public void testJsonContentsDirComparerOK() throws Exception {

        File dir1= new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/fixtures/jsoncompare/1").toURI());
        FixDirFromResourceCreator creator = new FixDirFromResourceCreator(dir1.getAbsolutePath());
        FixDir fixDir1 = creator.create(testRun).asDir();

        InputStream content = Thread.currentThread().getContextClassLoader().getResourceAsStream("com/collective/celos/ci/fixtures/jsoncompare/2/content");
        JsonContentsDirComparer dirComparer = new JsonContentsDirComparer(Collections.EMPTY_SET, Utils.wrap(new FixFile(content)), Utils.wrap(fixDir1));

        FixObjectCompareResult compareResult = dirComparer.check(null);
        Assert.assertEquals(compareResult.getStatus(), FixObjectCompareResult.Status.SUCCESS);
    }

    @Test
    public void testJsonContentsDirComparerOKIgnorePaths() throws Exception {

        InputStream content = Thread.currentThread().getContextClassLoader().getResourceAsStream("com/collective/celos/ci/fixtures/jsoncompare/5/content");
        Set<String> ignorePaths = Sets.newHashSet("root/events/tstamp");

        File dir1= new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/fixtures/jsoncompare/1").toURI());

        FixDirFromResourceCreator creator = new FixDirFromResourceCreator(dir1.getAbsolutePath());
        FixDir fixDir1 = creator.create(testRun).asDir();
        JsonContentsDirComparer dirComparer = new JsonContentsDirComparer(ignorePaths, Utils.wrap(new FixFile(content)), Utils.wrap(fixDir1));

        FixObjectCompareResult compareResult = dirComparer.check(null);
        Assert.assertEquals(compareResult.getStatus(), FixObjectCompareResult.Status.SUCCESS);
    }

    @Test
    public void testJsonContentsDirComparerOKIgnorePathsFails() throws Exception {

        InputStream content = Thread.currentThread().getContextClassLoader().getResourceAsStream("com/collective/celos/ci/fixtures/jsoncompare/5/content");

        File dir1= new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/fixtures/jsoncompare/1").toURI());

        FixDirFromResourceCreator creator = new FixDirFromResourceCreator(dir1.getAbsolutePath());
        FixDir fixDir1 = creator.create(testRun).asDir();
        JsonContentsDirComparer dirComparer = new JsonContentsDirComparer(Collections.EMPTY_SET, Utils.wrap(new FixFile(content), "desc1"), Utils.wrap(fixDir1, "desc2"));

        FixObjectCompareResult compareResult = dirComparer.check(null);
        Assert.assertEquals(compareResult.getStatus(), FixObjectCompareResult.Status.FAIL);
        Assert.assertEquals(compareResult.generateDescription(), "Diff:\n" +
                "Actual [desc2]:\n" +
                "{\"visitor\":{\"com.collective.pythia.avro.Visitor\":{\"cookie_id\":\"12bd0b8c6201000\",\"segments\":[],\"edges\":{},\"behaviors\":{},\"birthdate\":0,\"association_ids\":{}}},\"events\":[{\"cookie_id\":\"12bd0b8c6201000\",\"tstamp\":1403721384549,\"edge\":\"batchimport\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":null,\"network\":\"et\",\"segments\":[49118]}}}]} [1 times]\n" +
                "Expected [desc1]:\n" +
                "{\"visitor\":{\"com.collective.pythia.avro.Visitor\":{\"cookie_id\":\"12bd0b8c6201000\",\"segments\":[],\"edges\":{},\"behaviors\":{},\"birthdate\":0,\"association_ids\":{}}},\"events\":[{\"cookie_id\":\"12bd0b8c6201000\",\"tstamp\":1403721231239,\"edge\":\"batchimport\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":null,\"network\":\"et\",\"segments\":[49118]}}}]} [1 times]\n");
    }

    @Test
    public void testJsonContentsDirComparerFailContent() throws Exception {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("com/collective/celos/ci/fixtures/jsoncompare/4/content");

        File dir1= new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/fixtures/jsoncompare/1").toURI());

        FixDirFromResourceCreator creator = new FixDirFromResourceCreator(dir1.getAbsolutePath());
        FixDir fixDir1 = creator.create(testRun).asDir();
        JsonContentsDirComparer dirComparer = new JsonContentsDirComparer(Collections.EMPTY_SET, Utils.wrap(new FixFile(is)), Utils.wrap(fixDir1));

        FixObjectCompareResult compareResult = dirComparer.check(null);
        Assert.assertEquals(compareResult.getStatus(), FixObjectCompareResult.Status.FAIL);

        Assert.assertEquals(compareResult.generateDescription(), "Diff:\n" +
                "Actual []:\n" +
                "{\"visitor\":{\"com.collective.pythia.avro.Visitor\":{\"cookie_id\":\"1317beb84b00000\",\"segments\":[],\"edges\":{},\"behaviors\":{},\"birthdate\":0,\"association_ids\":{}}},\"events\":[{\"cookie_id\":\"1317beb84b00000\",\"tstamp\":1403721380452,\"edge\":\"batchimport\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":null,\"network\":\"et\",\"segments\":[49118]}}}]} [2 times]\n" +
                "Expected []:\n" +
                "{\"visitor\":{\"com.collective.pythia.avro.Visitor\":{\"cookie_id\":\"1317beb84b00000\",\"segments\":[],\"edges\":{},\"behaviors\":{},\"birthdate\":0,\"association_ids\":{}}},\"events\":[{\"cookie_id\":\"1317beb84b00000\",\"tstamp\":1403721380452,\"edge\":\"batchimport\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":null,\"network\":\"et\",\"segments\":[49118]}}}]} [1 times]\n" +
                "{\"visitor\":{\"com.collective.pythia.avro.Visitor\":{\"cookie_id\":\"1317beb84b0zzzz\",\"segments\":[],\"edges\":{},\"behaviors\":{},\"birthdate\":0,\"association_ids\":{}}},\"events\":[{\"cookie_id\":\"1317beb84b00000\",\"tstamp\":1403721380452,\"edge\":\"batchimport\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":null,\"network\":\"et\",\"segments\":[49118]}}}]} [1 times]\n");
    }

    @Test
    public void testJsonContentsDirComparerFailCount() throws Exception {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("com/collective/celos/ci/fixtures/jsoncompare/3/content");

        File dir1= new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/fixtures/jsoncompare/1").toURI());

        FixDirFromResourceCreator creator = new FixDirFromResourceCreator(dir1.getAbsolutePath());
        FixDir fixDir1 = creator.create(testRun).asDir();
        JsonContentsDirComparer dirComparer = new JsonContentsDirComparer(Collections.EMPTY_SET, Utils.wrap(new FixFile(is), "desc1"), Utils.wrap(fixDir1, "desc2"));

        FixObjectCompareResult compareResult = dirComparer.check(null);
        Assert.assertEquals(compareResult.getStatus(), FixObjectCompareResult.Status.FAIL);
        Assert.assertEquals(compareResult.generateDescription(), "Diff:\n" +
                "Actual [desc2]:\n" +
                "{\"visitor\":{\"com.collective.pythia.avro.Visitor\":{\"cookie_id\":\"12b811f59080000\",\"segments\":[],\"edges\":{},\"behaviors\":{},\"birthdate\":0,\"association_ids\":{}}},\"events\":[{\"cookie_id\":\"12b811f59080000\",\"tstamp\":1403721375367,\"edge\":\"batchimport\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":null,\"network\":\"et\",\"segments\":[49118]}}}]} [2 times]\n" +
                "{\"visitor\":{\"com.collective.pythia.avro.Visitor\":{\"cookie_id\":\"1317beb84b00000\",\"segments\":[],\"edges\":{},\"behaviors\":{},\"birthdate\":0,\"association_ids\":{}}},\"events\":[{\"cookie_id\":\"1317beb84b00000\",\"tstamp\":1403721380452,\"edge\":\"batchimport\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":null,\"network\":\"et\",\"segments\":[49118]}}}]} [2 times]\n" +
                "{\"visitor\":{\"com.collective.pythia.avro.Visitor\":{\"cookie_id\":\"133263e9e100000\",\"segments\":[],\"edges\":{},\"behaviors\":{},\"birthdate\":0,\"association_ids\":{}}},\"events\":[{\"cookie_id\":\"133263e9e100000\",\"tstamp\":1403721385042,\"edge\":\"batchimport\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"REMOVE\",\"association_id\":null,\"network\":\"et\",\"segments\":[49118]}}},{\"cookie_id\":\"133263e9e100000\",\"tstamp\":1403721385042,\"edge\":\"batchimport\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":null,\"network\":\"et\",\"segments\":[49117]}}}]} [2 times]\n" +
                "{\"visitor\":{\"com.collective.pythia.avro.Visitor\":{\"cookie_id\":\"134adb391b00000\",\"segments\":[],\"edges\":{},\"behaviors\":{},\"birthdate\":0,\"association_ids\":{}}},\"events\":[{\"cookie_id\":\"134adb391b00000\",\"tstamp\":1403721376988,\"edge\":\"batchimport\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":null,\"network\":\"et\",\"segments\":[49118]}}}]} [2 times]\n" +
                "Expected [desc1]:\n" +
                "{\"visitor\":{\"com.collective.pythia.avro.Visitor\":{\"cookie_id\":\"12b811f59080000\",\"segments\":[],\"edges\":{},\"behaviors\":{},\"birthdate\":0,\"association_ids\":{}}},\"events\":[{\"cookie_id\":\"12b811f59080000\",\"tstamp\":1403721375367,\"edge\":\"batchimport\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":null,\"network\":\"et\",\"segments\":[49118]}}}]} [1 times]\n" +
                "{\"visitor\":{\"com.collective.pythia.avro.Visitor\":{\"cookie_id\":\"1317beb84b00000\",\"segments\":[],\"edges\":{},\"behaviors\":{},\"birthdate\":0,\"association_ids\":{}}},\"events\":[{\"cookie_id\":\"1317beb84b00000\",\"tstamp\":1403721380452,\"edge\":\"batchimport\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":null,\"network\":\"et\",\"segments\":[49118]}}}]} [1 times]\n" +
                "{\"visitor\":{\"com.collective.pythia.avro.Visitor\":{\"cookie_id\":\"133263e9e100000\",\"segments\":[],\"edges\":{},\"behaviors\":{},\"birthdate\":0,\"association_ids\":{}}},\"events\":[{\"cookie_id\":\"133263e9e100000\",\"tstamp\":1403721385042,\"edge\":\"batchimport\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"REMOVE\",\"association_id\":null,\"network\":\"et\",\"segments\":[49118]}}},{\"cookie_id\":\"133263e9e100000\",\"tstamp\":1403721385042,\"edge\":\"batchimport\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":null,\"network\":\"et\",\"segments\":[49117]}}}]} [1 times]\n" +
                "{\"visitor\":{\"com.collective.pythia.avro.Visitor\":{\"cookie_id\":\"134adb391b00000\",\"segments\":[],\"edges\":{},\"behaviors\":{},\"birthdate\":0,\"association_ids\":{}}},\"events\":[{\"cookie_id\":\"134adb391b00000\",\"tstamp\":1403721376988,\"edge\":\"batchimport\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":null,\"network\":\"et\",\"segments\":[49118]}}}]} [1 times]\n");
    }


}
