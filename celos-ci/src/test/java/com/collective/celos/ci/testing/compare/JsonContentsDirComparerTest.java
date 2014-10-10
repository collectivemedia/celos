package com.collective.celos.ci.testing.compare;

import com.collective.celos.ci.testing.fixtures.compare.FixObjectCompareResult;
import com.collective.celos.ci.testing.fixtures.compare.JsonContentsDirComparer;
import com.collective.celos.ci.testing.fixtures.read.FixFileTreeObjectCreator;
import com.collective.celos.ci.testing.structure.fixobject.FixDir;
import com.collective.celos.ci.testing.structure.fixobject.FixObject;
import com.collective.celos.ci.testing.structure.outfixture.OutFixDir;
import com.collective.celos.ci.testing.structure.outfixture.OutFixFile;
import com.collective.celos.ci.testing.structure.outfixture.OutFixObject;
import com.google.common.collect.Sets;
import junit.framework.Assert;
import org.junit.Test;

import java.io.File;
import java.util.Collections;
import java.util.Set;

/**
 * Created by akonopko on 10/9/14.
 */
public class JsonContentsDirComparerTest {

    @Test
    public void testJsonContentsDirComparerOK() throws Exception {
        JsonContentsDirComparer dirComparer = new JsonContentsDirComparer(Collections.EMPTY_SET);

        File dir1= new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/fixtures/jsoncompare/1").toURI());
        File dir2= new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/fixtures/jsoncompare/2").toURI());

        FixFileTreeObjectCreator creator = new FixFileTreeObjectCreator(dir1.getAbsolutePath());
        OutFixDir fixDir1 = (OutFixDir) creator.createOutFixture();

        FixFileTreeObjectCreator creator2 = new FixFileTreeObjectCreator(dir2.getAbsolutePath());
        FixDir fixDir2 = (FixDir) creator2.createInFixture();

        FixObjectCompareResult compareResult = dirComparer.compare(fixDir1, fixDir2);
        Assert.assertEquals(compareResult.getStatus(), FixObjectCompareResult.Status.SUCCESS);
    }

    @Test
    public void testJsonContentsDirComparerOKIgnorePaths() throws Exception {

        Set<String> ignorePaths = Sets.newHashSet("root/events/tstamp");
        JsonContentsDirComparer dirComparer = new JsonContentsDirComparer(ignorePaths);

        File dir1= new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/fixtures/jsoncompare/1").toURI());
        File dir2= new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/fixtures/jsoncompare/5").toURI());

        FixFileTreeObjectCreator creator = new FixFileTreeObjectCreator(dir1.getAbsolutePath());
        OutFixDir fixDir1 = (OutFixDir) creator.createOutFixture();

        FixFileTreeObjectCreator creator2 = new FixFileTreeObjectCreator(dir2.getAbsolutePath());
        FixDir fixDir2 = (FixDir) creator2.createInFixture();

        FixObjectCompareResult compareResult = dirComparer.compare(fixDir1, fixDir2);
        Assert.assertEquals(compareResult.getStatus(), FixObjectCompareResult.Status.SUCCESS);
    }

    @Test
    public void testJsonContentsDirComparerOKIgnorePathsFails() throws Exception {

        JsonContentsDirComparer dirComparer = new JsonContentsDirComparer(Collections.EMPTY_SET);

        File dir1= new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/fixtures/jsoncompare/1").toURI());
        File dir2= new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/fixtures/jsoncompare/5").toURI());

        FixFileTreeObjectCreator creator = new FixFileTreeObjectCreator(dir1.getAbsolutePath());
        OutFixDir fixDir1 = (OutFixDir) creator.createOutFixture();

        FixFileTreeObjectCreator creator2 = new FixFileTreeObjectCreator(dir2.getAbsolutePath());
        FixDir fixDir2 = (FixDir) creator2.createInFixture();

        FixObjectCompareResult compareResult = dirComparer.compare(fixDir1, fixDir2);
        Assert.assertEquals(compareResult.getStatus(), FixObjectCompareResult.Status.FAIL);
        Assert.assertEquals(compareResult.generateDescription(), "Diff:\n" +
                "Actual differs:\n" +
                "{\"visitor\":{\"com.collective.pythia.avro.Visitor\":{\"cookie_id\":\"13114ef2b401000\",\"segments\":[],\"edges\":{},\"behaviors\":{},\"birthdate\":0,\"association_ids\":{}}},\"events\":[{\"cookie_id\":\"13114ef2b401000\",\"tstamp\":1403721315994,\"edge\":\"batchimport\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":null,\"network\":\"et\",\"segments\":[49117]}}}]} [1 times]\n" +
                "Expected differs:\n" +
                "{\"visitor\":{\"com.collective.pythia.avro.Visitor\":{\"cookie_id\":\"13114ef2b401000\",\"segments\":[],\"edges\":{},\"behaviors\":{},\"birthdate\":0,\"association_ids\":{}}},\"events\":[{\"cookie_id\":\"13114ef2b401000\",\"tstamp\":1403721375994,\"edge\":\"batchimport\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":null,\"network\":\"et\",\"segments\":[49117]}}}]} [1 times]\n");

    }

    @Test
    public void testJsonContentsDirComparerFailContent() throws Exception {
        JsonContentsDirComparer dirComparer = new JsonContentsDirComparer(Collections.EMPTY_SET);

        File dir1= new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/fixtures/jsoncompare/1").toURI());
        File dir2= new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/fixtures/jsoncompare/4").toURI());

        FixFileTreeObjectCreator creator = new FixFileTreeObjectCreator(dir1.getAbsolutePath());
        OutFixDir fixDir1 = (OutFixDir) creator.createOutFixture();

        FixFileTreeObjectCreator creator2 = new FixFileTreeObjectCreator(dir2.getAbsolutePath());
        FixDir fixDir2 = (FixDir) creator2.createInFixture();

        FixObjectCompareResult compareResult = dirComparer.compare(fixDir1, fixDir2);
        Assert.assertEquals(compareResult.getStatus(), FixObjectCompareResult.Status.FAIL);
        Assert.assertEquals(compareResult.generateDescription(), "Diff:\n" +
                "Actual differs:\n" +
                "{\"visitor\":{\"com.collective.pythia.avro.Visitor\":{\"cookie_id\":\"1317beb84b00000\",\"segments\":[],\"edges\":{},\"behaviors\":{},\"birthdate\":0,\"association_ids\":{}}},\"events\":[{\"cookie_id\":\"1317beb84b00000\",\"tstamp\":1403721380452,\"edge\":\"batchimport\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":null,\"network\":\"et\",\"segments\":[49118]}}}]} [1 times]\n" +
                "{\"visitor\":{\"com.collective.pythia.avro.Visitor\":{\"cookie_id\":\"1317beb84b0zzzz\",\"segments\":[],\"edges\":{},\"behaviors\":{},\"birthdate\":0,\"association_ids\":{}}},\"events\":[{\"cookie_id\":\"1317beb84b00000\",\"tstamp\":1403721380452,\"edge\":\"batchimport\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":null,\"network\":\"et\",\"segments\":[49118]}}}]} [1 times]\n" +
                "Expected differs:\n" +
                "{\"visitor\":{\"com.collective.pythia.avro.Visitor\":{\"cookie_id\":\"1317beb84b00000\",\"segments\":[],\"edges\":{},\"behaviors\":{},\"birthdate\":0,\"association_ids\":{}}},\"events\":[{\"cookie_id\":\"1317beb84b00000\",\"tstamp\":1403721380452,\"edge\":\"batchimport\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":null,\"network\":\"et\",\"segments\":[49118]}}}]} [2 times]\n");
    }

    @Test
    public void testJsonContentsDirComparerFailCount() throws Exception {
        JsonContentsDirComparer dirComparer = new JsonContentsDirComparer(Collections.EMPTY_SET);

        File dir1= new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/fixtures/jsoncompare/1").toURI());
        File dir2= new File(Thread.currentThread().getContextClassLoader().getResource("com/collective/celos/ci/fixtures/jsoncompare/3").toURI());

        FixFileTreeObjectCreator creator = new FixFileTreeObjectCreator(dir1.getAbsolutePath());
        OutFixDir fixDir1 = (OutFixDir) creator.createOutFixture();

        FixFileTreeObjectCreator creator2 = new FixFileTreeObjectCreator(dir2.getAbsolutePath());
        FixDir fixDir2 = (FixDir) creator2.createInFixture();

        FixObjectCompareResult compareResult = dirComparer.compare(fixDir1, fixDir2);
        Assert.assertEquals(compareResult.getStatus(), FixObjectCompareResult.Status.FAIL);
        Assert.assertEquals(compareResult.generateDescription(), "Diff:\n" +
                "Actual differs:\n" +
                "{\"visitor\":{\"com.collective.pythia.avro.Visitor\":{\"cookie_id\":\"12b811f59080000\",\"segments\":[],\"edges\":{},\"behaviors\":{},\"birthdate\":0,\"association_ids\":{}}},\"events\":[{\"cookie_id\":\"12b811f59080000\",\"tstamp\":1403721375367,\"edge\":\"batchimport\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":null,\"network\":\"et\",\"segments\":[49118]}}}]} [1 times]\n" +
                "{\"visitor\":{\"com.collective.pythia.avro.Visitor\":{\"cookie_id\":\"1317beb84b00000\",\"segments\":[],\"edges\":{},\"behaviors\":{},\"birthdate\":0,\"association_ids\":{}}},\"events\":[{\"cookie_id\":\"1317beb84b00000\",\"tstamp\":1403721380452,\"edge\":\"batchimport\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":null,\"network\":\"et\",\"segments\":[49118]}}}]} [1 times]\n" +
                "{\"visitor\":{\"com.collective.pythia.avro.Visitor\":{\"cookie_id\":\"133263e9e100000\",\"segments\":[],\"edges\":{},\"behaviors\":{},\"birthdate\":0,\"association_ids\":{}}},\"events\":[{\"cookie_id\":\"133263e9e100000\",\"tstamp\":1403721385042,\"edge\":\"batchimport\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"REMOVE\",\"association_id\":null,\"network\":\"et\",\"segments\":[49118]}}},{\"cookie_id\":\"133263e9e100000\",\"tstamp\":1403721385042,\"edge\":\"batchimport\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":null,\"network\":\"et\",\"segments\":[49117]}}}]} [1 times]\n" +
                "{\"visitor\":{\"com.collective.pythia.avro.Visitor\":{\"cookie_id\":\"134adb391b00000\",\"segments\":[],\"edges\":{},\"behaviors\":{},\"birthdate\":0,\"association_ids\":{}}},\"events\":[{\"cookie_id\":\"134adb391b00000\",\"tstamp\":1403721376988,\"edge\":\"batchimport\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":null,\"network\":\"et\",\"segments\":[49118]}}}]} [1 times]\n" +
                "Expected differs:\n" +
                "{\"visitor\":{\"com.collective.pythia.avro.Visitor\":{\"cookie_id\":\"12b811f59080000\",\"segments\":[],\"edges\":{},\"behaviors\":{},\"birthdate\":0,\"association_ids\":{}}},\"events\":[{\"cookie_id\":\"12b811f59080000\",\"tstamp\":1403721375367,\"edge\":\"batchimport\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":null,\"network\":\"et\",\"segments\":[49118]}}}]} [2 times]\n" +
                "{\"visitor\":{\"com.collective.pythia.avro.Visitor\":{\"cookie_id\":\"1317beb84b00000\",\"segments\":[],\"edges\":{},\"behaviors\":{},\"birthdate\":0,\"association_ids\":{}}},\"events\":[{\"cookie_id\":\"1317beb84b00000\",\"tstamp\":1403721380452,\"edge\":\"batchimport\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":null,\"network\":\"et\",\"segments\":[49118]}}}]} [2 times]\n" +
                "{\"visitor\":{\"com.collective.pythia.avro.Visitor\":{\"cookie_id\":\"133263e9e100000\",\"segments\":[],\"edges\":{},\"behaviors\":{},\"birthdate\":0,\"association_ids\":{}}},\"events\":[{\"cookie_id\":\"133263e9e100000\",\"tstamp\":1403721385042,\"edge\":\"batchimport\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"REMOVE\",\"association_id\":null,\"network\":\"et\",\"segments\":[49118]}}},{\"cookie_id\":\"133263e9e100000\",\"tstamp\":1403721385042,\"edge\":\"batchimport\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":null,\"network\":\"et\",\"segments\":[49117]}}}]} [2 times]\n" +
                "{\"visitor\":{\"com.collective.pythia.avro.Visitor\":{\"cookie_id\":\"134adb391b00000\",\"segments\":[],\"edges\":{},\"behaviors\":{},\"birthdate\":0,\"association_ids\":{}}},\"events\":[{\"cookie_id\":\"134adb391b00000\",\"tstamp\":1403721376988,\"edge\":\"batchimport\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":null,\"network\":\"et\",\"segments\":[49118]}}}]} [2 times]\n");
    }


}
