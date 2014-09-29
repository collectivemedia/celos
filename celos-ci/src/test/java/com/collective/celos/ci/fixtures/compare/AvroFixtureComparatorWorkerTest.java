package com.collective.celos.ci.fixtures.compare;

import com.collective.celos.ci.fixtures.deploy.AvroFixtureDeployWorker;
import org.apache.avro.Schema;
import org.apache.avro.file.SeekableByteArrayInput;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

/**
 * Created by akonopko on 9/18/14.
 */
public class AvroFixtureComparatorWorkerTest {


    @Test
    public void testReadInputAvroToJSON() throws IOException, URISyntaxException {
        AvroFixtureComparatorWorker worker = new AvroFixtureComparatorWorker();

        Schema schema = new Schema.Parser().parse(Thread.currentThread().getContextClassLoader().getResourceAsStream("com/collective/celos/ci/testing/compare/avro/avro.schema"));

        InputStream inputJson = Thread.currentThread().getContextClassLoader().getResourceAsStream("com/collective/celos/ci/testing/compare/avro/avro.json");

        ByteArrayOutputStream outputJson = new ByteArrayOutputStream();

        AvroFixtureDeployWorker.writeJsonISToAvroOS(inputJson, outputJson, schema);

        InputStream jsonData = Thread.currentThread().getContextClassLoader().getResourceAsStream("com/collective/celos/ci/testing/compare/avro/avro.json");
        InputStream jsonIsBack = worker.readInputAvroToJson(new SeekableByteArrayInput(outputJson.toByteArray()));

        Assert.assertTrue(IOUtils.contentEquals(jsonData, jsonIsBack));
    }

    @Test
    public void compareStreamsWithJsonSucceeds() throws Exception {

        String json1 = "{\"visitor\":{\"com.collective.pythia.avro.Visitor\":{\"cookie_id\":\"13174ef5358fb01\",\"segments\":[],\"edges\":{},\"behaviors\":{},\"birthdate\":0,\"association_ids\":{}}},\"events\":[{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721351356,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"44cb7937-93c4-4d54-9edf-b7683aec41a9\"},\"network\":\"device_apple_ida\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721352251,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"b38cdc14ffde6226f2f54e5c82f9d0d1dba07796\"},\"network\":\"device_sha1\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721336557,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"323ba4ba1502ac246c1a880ded9d6513\"},\"network\":\"device_md5\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721336557,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"44626f1b-6f14-49a1-8273-a45e256cef18\"},\"network\":\"device_apple_ida\",\"segments\":[]}}}]}";
        String json2 = "{\"visitor\":{\"com.collective.pythia.avro.Visitor\":{\"cookie_id\":\"13174ef5358fb01\",\"segments\":[],\"edges\":{},\"behaviors\":{},\"birthdate\":0,\"association_ids\":{}}},\"events\":[{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721351356,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"44cb7937-93c4-4d54-9edf-b7683aec41a9\"},\"network\":\"device_apple_ida\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721352251,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"b38cdc14ffde6226f2f54e5c82f9d0d1dba07796\"},\"network\":\"device_sha1\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721336557,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"323ba4ba1502ac246c1a880ded9d6513\"},\"network\":\"device_md5\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721336557,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"44626f1b-6f14-49a1-8273-a45e256cef18\"},\"network\":\"device_apple_ida\",\"segments\":[]}}}]}";

        AvroFixtureComparatorWorker worker = new AvroFixtureComparatorWorker();
        worker.compareStreamsWithJson(new ByteArrayInputStream(json1.getBytes()), new ByteArrayInputStream(json2.getBytes()), "");
    }


    @Test(expected = CelosResultsCompareException.class)
    public void compareStreamsWithJsonFails() throws Exception {

        String json1 = "{\"visitor\":{\"com.collective.pythia.avro.Visitor\":{\"cookie_id\":\"13174ef5358fb01\",\"segments\":[],\"edges\":{},\"behaviors\":{},\"birthdate\":0,\"association_ids\":{}}},\"events\":[{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721351356,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"44cb7937-93c4-4d54-9edf-b7683aec41a9\"},\"network\":\"device_apple_ida\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721352251,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"b38cdc14ffde6226f2f54e5c82f9d0d1dba07796\"},\"network\":\"device_sha1\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721336557,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"323ba4ba1502ac246c1a880ded9d6513\"},\"network\":\"device_md5\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721336557,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"44626f1b-6f14-49a1-8273-a45e256cef18\"},\"network\":\"device_apple_ida\",\"segments\":[]}}}]}";
        String json2 = "{\"visitor\":{\"com.collective.pythia.avro.Visitor\":{\"cookie_id\":\"13174ef5358fb01\",\"segments\":[],\"edges\":{},\"behaviors\":{},\"birthdate\":0,\"association_ids\":{}}},\"events\":[{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721351356,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"44cb7937-93c4-4d54-9edf-b7683aec41a9\"},\"network\":\"device_apple_ida\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721352251,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"b38cdc14ffde6226f2f54e5c82f9d0d1dba07796\"},\"network\":\"device_sha1\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721336557,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"323ba4ba1502ac246c1a880ded9d6513\"},\"network\":\"device_md5\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721336557,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"44626f1b-6f14-49a1-8273-a45e256cef18\"},\"network\":\"device_apple_idb\",\"segments\":[]}}}]}";

        AvroFixtureComparatorWorker worker = new AvroFixtureComparatorWorker();
        worker.compareStreamsWithJson(new ByteArrayInputStream(json1.getBytes()), new ByteArrayInputStream(json2.getBytes()), "");
    }

}
