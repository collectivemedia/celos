package com.collective.celos.ci.testing.fixtures.compare;

import com.collective.celos.ci.testing.fixtures.compare.json.JsonEntity;
import com.google.common.collect.Sets;
import junit.framework.Assert;
import org.junit.Test;

import java.util.Set;

/**
 * Created by akonopko on 9/29/14.
 */
public class JsonEntityTest {

    @Test
    public void testCompareOK() {

        String json1 = "{\"visitor\":{\"com.collective.pythia.avro.Visitor\":{\"cookie_id\":\"13174ef5358fb01\",\"segments\":[],\"edges\":{},\"behaviors\":{},\"birthdate\":0,\"association_ids\":{}}},\"events\":[{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721351356,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"44cb7937-93c4-4d54-9edf-b7683aec41a9\"},\"network\":\"device_apple_ida\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721352251,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"b38cdc14ffde6226f2f54e5c82f9d0d1dba07796\"},\"network\":\"device_sha1\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721336557,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"323ba4ba1502ac246c1a880ded9d6513\"},\"network\":\"device_md5\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721336557,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"44626f1b-6f14-49a1-8273-a45e256cef18\"},\"network\":\"device_apple_ida\",\"segments\":[]}}}]}";
        String json2 = "{\"visitor\":{\"com.collective.pythia.avro.Visitor\":{\"cookie_id\":\"13174ef5358fb01\",\"segments\":[],\"edges\":{},\"behaviors\":{},\"birthdate\":0,\"association_ids\":{}}},\"events\":[{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721351356,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"44cb7937-93c4-4d54-9edf-b7683aec41a9\"},\"network\":\"device_apple_ida\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721352251,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"b38cdc14ffde6226f2f54e5c82f9d0d1dba07796\"},\"network\":\"device_sha1\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721336557,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"323ba4ba1502ac246c1a880ded9d6513\"},\"network\":\"device_md5\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721336557,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"44626f1b-6f14-49a1-8273-a45e256cef18\"},\"network\":\"device_apple_ida\",\"segments\":[]}}}]}";

        JsonEntity en1 = new JsonEntity(json1);
        JsonEntity en2 = new JsonEntity(json2);

        Assert.assertEquals(en1, en2);
    }

    @Test
    public void testCompareFailsOnAbsence() {

        String json1 = "{\"visitor\":{\"com.collective.pythia.avro.Visitor\":{\"segments\":[],\"edges\":{},\"behaviors\":{},\"birthdate\":0,\"association_ids\":{}}},\"events\":[{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721351356,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"44cb7937-93c4-4d54-9edf-b7683aec41a9\"},\"network\":\"device_apple_ida\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721352251,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"b38cdc14ffde6226f2f54e5c82f9d0d1dba07796\"},\"network\":\"device_sha1\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721336557,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"323ba4ba1502ac246c1a880ded9d6513\"},\"network\":\"device_md5\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721336557,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"44626f1b-6f14-49a1-8273-a45e256cef18\"},\"network\":\"device_apple_ida\",\"segments\":[]}}}]}";
        String json2 = "{\"visitor\":{\"com.collective.pythia.avro.Visitor\":{\"cookie_id\":\"13174ef5358fb01\",\"segments\":[],\"edges\":{},\"behaviors\":{},\"birthdate\":0,\"association_ids\":{}}},\"events\":[{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721351356,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"44cb7937-93c4-4d54-9edf-b7683aec41a9\"},\"network\":\"device_apple_ida\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721352251,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"b38cdc14ffde6226f2f54e5c82f9d0d1dba07796\"},\"network\":\"device_sha1\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721336557,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"323ba4ba1502ac246c1a880ded9d6513\"},\"network\":\"device_md5\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721336557,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"44626f1b-6f14-49a1-8273-a45e256cef18\"},\"network\":\"device_apple_ida\",\"segments\":[]}}}]}";

        JsonEntity en1 = new JsonEntity(json1);
        JsonEntity en2 = new JsonEntity(json2);

        Assert.assertFalse(en1.equals(en2));
    }


    @Test
    public void testCompareFailsOnKey() {

        String json1 = "{\"visitor\":{\"com.collective.pythia.avro.Visitor\":{\"cookie_id\":\"13174ef5358fb01\",\"segments\":[],\"edges\":{},\"behaviors\":{},\"birthdate\":0,\"association_ids\":{}}},\"events\":[{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721351356,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"44cb7937-93c4-4d54-9edf-b7683aec41a9\"},\"network\":\"device_apple_ida\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721352251,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"b38cdc14ffde6226f2f54e5c82f9d0d1dba07796\"},\"network\":\"device_sha1\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721336557,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"323ba4ba1502ac246c1a880ded9d6513\"},\"network\":\"device_md5\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721336557,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"44626f1b-6f14-49a1-8273-a45e256cef18\"},\"network\":\"device_apple_ida\",\"segments\":[]}}}]}";
        String json2 = "{\"visitor\":{\"com.collective.pythia.avro.Visitor\":{\"cookie_id\":\"13174ef5358fb01\",\"segments\":[],\"edges\":{},\"behaviors\":{},\"birthdate\":0,\"association_ids\":{}}},\"events\":[{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721351356,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"44cb7937-93c4-4d54-9edf-b7683aec41a9\"},\"network\":\"device_apple_ida\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721352251,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"b38cdc14ffde6226f2f54e5c82f9d0d1dba07796\"},\"network\":\"device_sha1\",\"segmentz\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721336557,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"323ba4ba1502ac246c1a880ded9d6513\"},\"network\":\"device_md5\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721336557,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"44626f1b-6f14-49a1-8273-a45e256cef18\"},\"network\":\"device_apple_ida\",\"segments\":[]}}}]}";

        JsonEntity en1 = new JsonEntity(json1);
        JsonEntity en2 = new JsonEntity(json2);

        Assert.assertFalse(en1.equals(en2));
    }

    @Test
    public void testCompareFailsOnValue() {

        String json1 = "{\"visitor\":{\"com.collective.pythia.avro.Visitor\":{\"cookie_id\":\"13174ef5358fb01\",\"segments\":[],\"edges\":{},\"behaviors\":{},\"birthdate\":0,\"association_ids\":{}}},\"events\":[{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721351356,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"44cb7937-93c4-4d54-9edf-b7683aec41a9\"},\"network\":\"device_apple_ida\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721352251,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"b38cdc14ffde6226f2f54e5c82f9d0d1dba07796\"},\"network\":\"device_sha1\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721336557,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"323ba4ba1502ac246c1a880ded9d6513\"},\"network\":\"device_md5\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721336557,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"44626f1b-6f14-49a1-8273-a45e256cef18\"},\"network\":\"device_apple_ida\",\"segments\":[]}}}]}";
        String json2 = "{\"visitor\":{\"com.collective.pythia.avro.Visitor\":{\"cookie_id\":\"13174ef5358fb01\",\"segments\":[],\"edges\":{},\"behaviors\":{},\"birthdate\":0,\"association_ids\":{}}},\"events\":[{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721351356,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"44cb7937-93c4-4d54-9edf-b7683aec41a9\"},\"network\":\"device_apple_ida\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721352251,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"b38cdc14ffde6226f2f54e5c82f9d0d1dba07796\"},\"network\":\"device_sha1\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721336557,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"323ba4ba1502ac246c1a880ded9d6513\"},\"network\":\"device_md5\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721336557,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"44626f1b-6f14-49a1-8273-a45e256cef18\"},\"network\":\"device_apple_idb\",\"segments\":[]}}}]}";

        JsonEntity en1 = new JsonEntity(json1);
        JsonEntity en2 = new JsonEntity(json2);

        Assert.assertFalse(en1.equals(en2));
    }

    @Test
    public void testCompareIgnoresPath() {

        String json1 = "{\"visitor\":{\"com.collective.pythia.avro.Visitor\":{\"cookie_id\":\"13174ef5358fb01\",\"segments\":[],\"edges\":{},\"behaviors\":{},\"birthdate\":0,\"association_ids\":{}}},\"events\":[{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403411351356,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"44cb7937-93c4-4d54-9edf-b7683aec41a9\"},\"network\":\"device_apple_ida\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721352251,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"b38cdc14ffde6226f2f54e5c82f9d0d1dba07796\"},\"network\":\"device_sha1\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721336557,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"323ba4ba1502ac246c1a880ded9d6513\"},\"network\":\"device_md5\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721336557,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"44626f1b-6f14-49a1-8273-a45e256cef18\"},\"network\":\"device_apple_ida\",\"segments\":[]}}}]}";
        String json2 = "{\"visitor\":{\"com.collective.pythia.avro.Visitor\":{\"cookie_id\":\"13174ef5358fb01\",\"segments\":[],\"edges\":{},\"behaviors\":{},\"birthdate\":0,\"association_ids\":{}}},\"events\":[{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721351356,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"44cb7937-93c4-4d54-9edf-b7683aec41a9\"},\"network\":\"device_apple_ida\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721352251,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"b38cdc14ffde6226f2f54e5c82f9d0d1dba07796\"},\"network\":\"device_sha1\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721336557,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"323ba4ba1502ac246c1a880ded9d6513\"},\"network\":\"device_md5\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721336557,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"44626f1b-6f14-49a1-8273-a45e256cef18\"},\"network\":\"device_apple_ida\",\"segments\":[]}}}]}";

        Set<String> ignorePaths = Sets.newHashSet("root/events/tstamp");

        JsonEntity en1 = new JsonEntity(json1, ignorePaths);
        JsonEntity en2 = new JsonEntity(json2, ignorePaths);

        Assert.assertTrue(en1.equals(en2));

    }

    @Test
    public void testCompareIgnoresWrongPath() {

        String json1 = "{\"visitor\":{\"com.collective.pythia.avro.Visitor\":{\"cookie_id\":\"13174ef5358fb01\",\"segments\":[],\"edges\":{},\"behaviors\":{},\"birthdate\":0,\"association_ids\":{}}},\"events\":[{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403411351356,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"44cb7937-93c4-4d54-9edf-b7683aec41a9\"},\"network\":\"device_apple_ida\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721352251,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"b38cdc14ffde6226f2f54e5c82f9d0d1dba07796\"},\"network\":\"device_sha1\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721336557,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"323ba4ba1502ac246c1a880ded9d6513\"},\"network\":\"device_md5\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721336557,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"44626f1b-6f14-49a1-8273-a45e256cef18\"},\"network\":\"device_apple_ida\",\"segments\":[]}}}]}";
        String json2 = "{\"visitor\":{\"com.collective.pythia.avro.Visitor\":{\"cookie_id\":\"13174ef5358fb01\",\"segments\":[],\"edges\":{},\"behaviors\":{},\"birthdate\":0,\"association_ids\":{}}},\"events\":[{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721351356,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"44cb7937-93c4-4d54-9edf-b7683aec41a9\"},\"network\":\"device_apple_ida\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721352251,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"b38cdc14ffde6226f2f54e5c82f9d0d1dba07796\"},\"network\":\"device_sha1\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721336557,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"323ba4ba1502ac246c1a880ded9d6513\"},\"network\":\"device_md5\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721336557,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"44626f1b-6f14-49a1-8273-a45e256cef18\"},\"network\":\"device_apple_ida\",\"segments\":[]}}}]}";

        JsonEntity en1 = new JsonEntity(json1);
        JsonEntity en2 = new JsonEntity(json2);

        Assert.assertFalse(en1.equals(en2));
    }

}
