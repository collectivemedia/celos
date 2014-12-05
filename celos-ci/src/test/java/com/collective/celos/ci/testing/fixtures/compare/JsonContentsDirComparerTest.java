package com.collective.celos.ci.testing.fixtures.compare;

import com.collective.celos.ci.testing.fixtures.read.FixFileTreeObjectCreator;
import com.collective.celos.ci.testing.structure.fixobject.FixDir;
import com.google.common.collect.Sets;
import junit.framework.Assert;
import org.junit.Test;

import java.io.File;
import java.io.InputStream;
import java.util.Collections;
import java.util.Set;

/**
 * Created by akonopko on 10/9/14.
 */
public class JsonContentsDirComparerTest {

    @Test
    public void testJsonContentsDirComparerOK() throws Exception {
        InputStream content = Thread.currentThread().getContextClassLoader().getResourceAsStream("com/collective/celos/ci/fixtures/jsoncompare/2/content");
        JsonContentsDirComparer dirComparer = new JsonContentsDirComparer(Collections.EMPTY_SET, content);

        File dir1= new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/fixtures/jsoncompare/1").toURI());

        FixFileTreeObjectCreator creator = new FixFileTreeObjectCreator(dir1.getAbsolutePath());
        FixDir fixDir1 = creator.create().asDir();

        FixObjectCompareResult compareResult = dirComparer.check(fixDir1);
        Assert.assertEquals(compareResult.getStatus(), FixObjectCompareResult.Status.SUCCESS);
    }

    @Test
    public void testJsonContentsDirComparerOKIgnorePaths() throws Exception {

        InputStream content = Thread.currentThread().getContextClassLoader().getResourceAsStream("com/collective/celos/ci/fixtures/jsoncompare/5/content");
        Set<String> ignorePaths = Sets.newHashSet("root/events/tstamp");
        JsonContentsDirComparer dirComparer = new JsonContentsDirComparer(ignorePaths, content);

        File dir1= new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/fixtures/jsoncompare/1").toURI());

        FixFileTreeObjectCreator creator = new FixFileTreeObjectCreator(dir1.getAbsolutePath());
        FixDir fixDir1 = creator.create().asDir();

        FixObjectCompareResult compareResult = dirComparer.check(fixDir1);
        Assert.assertEquals(compareResult.getStatus(), FixObjectCompareResult.Status.SUCCESS);
    }

    @Test
    public void testJsonContentsDirComparerOKIgnorePathsFails() throws Exception {

        InputStream content = Thread.currentThread().getContextClassLoader().getResourceAsStream("com/collective/celos/ci/fixtures/jsoncompare/5/content");
        JsonContentsDirComparer dirComparer = new JsonContentsDirComparer(Collections.EMPTY_SET, content);

        File dir1= new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/fixtures/jsoncompare/1").toURI());

        FixFileTreeObjectCreator creator = new FixFileTreeObjectCreator(dir1.getAbsolutePath());
        FixDir fixDir1 = creator.create().asDir();

        FixObjectCompareResult compareResult = dirComparer.check(fixDir1);
        Assert.assertEquals(compareResult.getStatus(), FixObjectCompareResult.Status.FAIL);
        Assert.assertEquals(compareResult.generateDescription(), "Diff:\n" +
                "Actual differs:\n" +
                "{\"visitor\":{\"com.collective.pythia.avro.Visitor\":{\"cookie_id\":\"12bd0b8c6201000\",\"segments\":[],\"edges\":{},\"behaviors\":{},\"birthdate\":0,\"association_ids\":{}}},\"events\":[{\"cookie_id\":\"12bd0b8c6201000\",\"tstamp\":1403721384549,\"edge\":\"batchimport\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":null,\"network\":\"et\",\"segments\":[49118]}}}]} [1 times]\n" +
                "Expected differs:\n" +
                "{\"visitor\":{\"com.collective.pythia.avro.Visitor\":{\"cookie_id\":\"12bd0b8c6201000\",\"segments\":[],\"edges\":{},\"behaviors\":{},\"birthdate\":0,\"association_ids\":{}}},\"events\":[{\"cookie_id\":\"12bd0b8c6201000\",\"tstamp\":1403721231239,\"edge\":\"batchimport\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":null,\"network\":\"et\",\"segments\":[49118]}}}]} [1 times]\n");
    }

    @Test
    public void testJsonContentsDirComparerFailContent() throws Exception {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("com/collective/celos/ci/fixtures/jsoncompare/4/content");
        JsonContentsDirComparer dirComparer = new JsonContentsDirComparer(Collections.EMPTY_SET, is);

        File dir1= new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/fixtures/jsoncompare/1").toURI());

        FixFileTreeObjectCreator creator = new FixFileTreeObjectCreator(dir1.getAbsolutePath());
        FixDir fixDir1 = creator.create().asDir();

        FixObjectCompareResult compareResult = dirComparer.check(fixDir1);
        Assert.assertEquals(compareResult.getStatus(), FixObjectCompareResult.Status.FAIL);

        Assert.assertEquals(compareResult.generateDescription(), "Diff:\n" +
                "Actual differs:\n" +
                "{\"visitor\":{\"com.collective.pythia.avro.Visitor\":{\"cookie_id\":\"1317beb84b00000\",\"segments\":[],\"edges\":{},\"behaviors\":{},\"birthdate\":0,\"association_ids\":{}}},\"events\":[{\"cookie_id\":\"1317beb84b00000\",\"tstamp\":1403721380452,\"edge\":\"batchimport\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":null,\"network\":\"et\",\"segments\":[49118]}}}]} [2 times]\n" +
                "Expected differs:\n" +
                "{\"visitor\":{\"com.collective.pythia.avro.Visitor\":{\"cookie_id\":\"1317beb84b00000\",\"segments\":[],\"edges\":{},\"behaviors\":{},\"birthdate\":0,\"association_ids\":{}}},\"events\":[{\"cookie_id\":\"1317beb84b00000\",\"tstamp\":1403721380452,\"edge\":\"batchimport\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":null,\"network\":\"et\",\"segments\":[49118]}}}]} [1 times]\n" +
                "{\"visitor\":{\"com.collective.pythia.avro.Visitor\":{\"cookie_id\":\"1317beb84b0zzzz\",\"segments\":[],\"edges\":{},\"behaviors\":{},\"birthdate\":0,\"association_ids\":{}}},\"events\":[{\"cookie_id\":\"1317beb84b00000\",\"tstamp\":1403721380452,\"edge\":\"batchimport\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":null,\"network\":\"et\",\"segments\":[49118]}}}]} [1 times]\n");
    }

    @Test
    public void testJsonContentsDirComparerFailCount() throws Exception {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("com/collective/celos/ci/fixtures/jsoncompare/3/content");
        JsonContentsDirComparer dirComparer = new JsonContentsDirComparer(Collections.EMPTY_SET, is);

        File dir1= new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/fixtures/jsoncompare/1").toURI());

        FixFileTreeObjectCreator creator = new FixFileTreeObjectCreator(dir1.getAbsolutePath());
        FixDir fixDir1 = creator.create().asDir();

        FixObjectCompareResult compareResult = dirComparer.check(fixDir1);
        Assert.assertEquals(compareResult.getStatus(), FixObjectCompareResult.Status.FAIL);
        Assert.assertEquals(compareResult.generateDescription(), "Diff:\n" +
                "Actual differs:\n" +
                "{\"visitor\":{\"com.collective.pythia.avro.Visitor\":{\"cookie_id\":\"12b811f59080000\",\"segments\":[],\"edges\":{},\"behaviors\":{},\"birthdate\":0,\"association_ids\":{}}},\"events\":[{\"cookie_id\":\"12b811f59080000\",\"tstamp\":1403721375367,\"edge\":\"batchimport\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":null,\"network\":\"et\",\"segments\":[49118]}}}]} [2 times]\n" +
                "{\"visitor\":{\"com.collective.pythia.avro.Visitor\":{\"cookie_id\":\"1317beb84b00000\",\"segments\":[],\"edges\":{},\"behaviors\":{},\"birthdate\":0,\"association_ids\":{}}},\"events\":[{\"cookie_id\":\"1317beb84b00000\",\"tstamp\":1403721380452,\"edge\":\"batchimport\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":null,\"network\":\"et\",\"segments\":[49118]}}}]} [2 times]\n" +
                "{\"visitor\":{\"com.collective.pythia.avro.Visitor\":{\"cookie_id\":\"133263e9e100000\",\"segments\":[],\"edges\":{},\"behaviors\":{},\"birthdate\":0,\"association_ids\":{}}},\"events\":[{\"cookie_id\":\"133263e9e100000\",\"tstamp\":1403721385042,\"edge\":\"batchimport\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"REMOVE\",\"association_id\":null,\"network\":\"et\",\"segments\":[49118]}}},{\"cookie_id\":\"133263e9e100000\",\"tstamp\":1403721385042,\"edge\":\"batchimport\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":null,\"network\":\"et\",\"segments\":[49117]}}}]} [2 times]\n" +
                "{\"visitor\":{\"com.collective.pythia.avro.Visitor\":{\"cookie_id\":\"134adb391b00000\",\"segments\":[],\"edges\":{},\"behaviors\":{},\"birthdate\":0,\"association_ids\":{}}},\"events\":[{\"cookie_id\":\"134adb391b00000\",\"tstamp\":1403721376988,\"edge\":\"batchimport\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":null,\"network\":\"et\",\"segments\":[49118]}}}]} [2 times]\n" +
                "Expected differs:\n" +
                "{\"visitor\":{\"com.collective.pythia.avro.Visitor\":{\"cookie_id\":\"12b811f59080000\",\"segments\":[],\"edges\":{},\"behaviors\":{},\"birthdate\":0,\"association_ids\":{}}},\"events\":[{\"cookie_id\":\"12b811f59080000\",\"tstamp\":1403721375367,\"edge\":\"batchimport\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":null,\"network\":\"et\",\"segments\":[49118]}}}]} [1 times]\n" +
                "{\"visitor\":{\"com.collective.pythia.avro.Visitor\":{\"cookie_id\":\"1317beb84b00000\",\"segments\":[],\"edges\":{},\"behaviors\":{},\"birthdate\":0,\"association_ids\":{}}},\"events\":[{\"cookie_id\":\"1317beb84b00000\",\"tstamp\":1403721380452,\"edge\":\"batchimport\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":null,\"network\":\"et\",\"segments\":[49118]}}}]} [1 times]\n" +
                "{\"visitor\":{\"com.collective.pythia.avro.Visitor\":{\"cookie_id\":\"133263e9e100000\",\"segments\":[],\"edges\":{},\"behaviors\":{},\"birthdate\":0,\"association_ids\":{}}},\"events\":[{\"cookie_id\":\"133263e9e100000\",\"tstamp\":1403721385042,\"edge\":\"batchimport\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"REMOVE\",\"association_id\":null,\"network\":\"et\",\"segments\":[49118]}}},{\"cookie_id\":\"133263e9e100000\",\"tstamp\":1403721385042,\"edge\":\"batchimport\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":null,\"network\":\"et\",\"segments\":[49117]}}}]} [1 times]\n" +
                "{\"visitor\":{\"com.collective.pythia.avro.Visitor\":{\"cookie_id\":\"134adb391b00000\",\"segments\":[],\"edges\":{},\"behaviors\":{},\"birthdate\":0,\"association_ids\":{}}},\"events\":[{\"cookie_id\":\"134adb391b00000\",\"tstamp\":1403721376988,\"edge\":\"batchimport\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":null,\"network\":\"et\",\"segments\":[49118]}}}]} [1 times]\n");
    }


}
