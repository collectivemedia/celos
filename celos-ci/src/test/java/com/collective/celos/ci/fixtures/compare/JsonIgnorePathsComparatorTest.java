package com.collective.celos.ci.fixtures.compare;

import com.collective.celos.ci.fixtures.util.JsonComparator;
import com.google.common.collect.Sets;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Created by akonopko on 9/29/14.
 */
public class JsonIgnorePathsComparatorTest {


    @Test
    public void testCompareOK() {
        JsonComparator comparator = new JsonComparator();

        String json1 = "{\"visitor\":{\"com.collective.pythia.avro.Visitor\":{\"cookie_id\":\"13174ef5358fb01\",\"segments\":[],\"edges\":{},\"behaviors\":{},\"birthdate\":0,\"association_ids\":{}}},\"events\":[{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721351356,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"44cb7937-93c4-4d54-9edf-b7683aec41a9\"},\"network\":\"device_apple_ida\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721352251,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"b38cdc14ffde6226f2f54e5c82f9d0d1dba07796\"},\"network\":\"device_sha1\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721336557,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"323ba4ba1502ac246c1a880ded9d6513\"},\"network\":\"device_md5\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721336557,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"44626f1b-6f14-49a1-8273-a45e256cef18\"},\"network\":\"device_apple_ida\",\"segments\":[]}}}]}";
        String json2 = "{\"visitor\":{\"com.collective.pythia.avro.Visitor\":{\"cookie_id\":\"13174ef5358fb01\",\"segments\":[],\"edges\":{},\"behaviors\":{},\"birthdate\":0,\"association_ids\":{}}},\"events\":[{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721351356,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"44cb7937-93c4-4d54-9edf-b7683aec41a9\"},\"network\":\"device_apple_ida\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721352251,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"b38cdc14ffde6226f2f54e5c82f9d0d1dba07796\"},\"network\":\"device_sha1\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721336557,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"323ba4ba1502ac246c1a880ded9d6513\"},\"network\":\"device_md5\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721336557,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"44626f1b-6f14-49a1-8273-a45e256cef18\"},\"network\":\"device_apple_ida\",\"segments\":[]}}}]}";

        Assert.assertTrue(comparator.compare(json1, json2));
    }

    @Test
    public void testCompareFailsOnAbsence() {
        JsonComparator comparator = new JsonComparator();

        String json1 = "{\"visitor\":{\"com.collective.pythia.avro.Visitor\":{\"segments\":[],\"edges\":{},\"behaviors\":{},\"birthdate\":0,\"association_ids\":{}}},\"events\":[{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721351356,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"44cb7937-93c4-4d54-9edf-b7683aec41a9\"},\"network\":\"device_apple_ida\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721352251,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"b38cdc14ffde6226f2f54e5c82f9d0d1dba07796\"},\"network\":\"device_sha1\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721336557,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"323ba4ba1502ac246c1a880ded9d6513\"},\"network\":\"device_md5\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721336557,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"44626f1b-6f14-49a1-8273-a45e256cef18\"},\"network\":\"device_apple_ida\",\"segments\":[]}}}]}";
        String json2 = "{\"visitor\":{\"com.collective.pythia.avro.Visitor\":{\"cookie_id\":\"13174ef5358fb01\",\"segments\":[],\"edges\":{},\"behaviors\":{},\"birthdate\":0,\"association_ids\":{}}},\"events\":[{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721351356,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"44cb7937-93c4-4d54-9edf-b7683aec41a9\"},\"network\":\"device_apple_ida\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721352251,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"b38cdc14ffde6226f2f54e5c82f9d0d1dba07796\"},\"network\":\"device_sha1\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721336557,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"323ba4ba1502ac246c1a880ded9d6513\"},\"network\":\"device_md5\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721336557,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"44626f1b-6f14-49a1-8273-a45e256cef18\"},\"network\":\"device_apple_ida\",\"segments\":[]}}}]}";

        Assert.assertFalse(comparator.compare(json1, json2));
    }


    @Test
    public void testCompareFailsOnKey() {
        JsonComparator comparator = new JsonComparator();

        String json1 = "{\"visitor\":{\"com.collective.pythia.avro.Visitor\":{\"cookie_id\":\"13174ef5358fb01\",\"segments\":[],\"edges\":{},\"behaviors\":{},\"birthdate\":0,\"association_ids\":{}}},\"events\":[{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721351356,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"44cb7937-93c4-4d54-9edf-b7683aec41a9\"},\"network\":\"device_apple_ida\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721352251,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"b38cdc14ffde6226f2f54e5c82f9d0d1dba07796\"},\"network\":\"device_sha1\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721336557,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"323ba4ba1502ac246c1a880ded9d6513\"},\"network\":\"device_md5\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721336557,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"44626f1b-6f14-49a1-8273-a45e256cef18\"},\"network\":\"device_apple_ida\",\"segments\":[]}}}]}";
        String json2 = "{\"visitor\":{\"com.collective.pythia.avro.Visitor\":{\"cookie_id\":\"13174ef5358fb01\",\"segments\":[],\"edges\":{},\"behaviors\":{},\"birthdate\":0,\"association_ids\":{}}},\"events\":[{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721351356,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"44cb7937-93c4-4d54-9edf-b7683aec41a9\"},\"network\":\"device_apple_ida\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721352251,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"b38cdc14ffde6226f2f54e5c82f9d0d1dba07796\"},\"network\":\"device_sha1\",\"segmentz\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721336557,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"323ba4ba1502ac246c1a880ded9d6513\"},\"network\":\"device_md5\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721336557,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"44626f1b-6f14-49a1-8273-a45e256cef18\"},\"network\":\"device_apple_ida\",\"segments\":[]}}}]}";

        Assert.assertFalse(comparator.compare(json1, json2));
    }

    @Test
    public void testCompareFailsOnValue() {
        JsonComparator comparator = new JsonComparator();

        String json1 = "{\"visitor\":{\"com.collective.pythia.avro.Visitor\":{\"cookie_id\":\"13174ef5358fb01\",\"segments\":[],\"edges\":{},\"behaviors\":{},\"birthdate\":0,\"association_ids\":{}}},\"events\":[{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721351356,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"44cb7937-93c4-4d54-9edf-b7683aec41a9\"},\"network\":\"device_apple_ida\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721352251,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"b38cdc14ffde6226f2f54e5c82f9d0d1dba07796\"},\"network\":\"device_sha1\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721336557,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"323ba4ba1502ac246c1a880ded9d6513\"},\"network\":\"device_md5\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721336557,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"44626f1b-6f14-49a1-8273-a45e256cef18\"},\"network\":\"device_apple_ida\",\"segments\":[]}}}]}";
        String json2 = "{\"visitor\":{\"com.collective.pythia.avro.Visitor\":{\"cookie_id\":\"13174ef5358fb01\",\"segments\":[],\"edges\":{},\"behaviors\":{},\"birthdate\":0,\"association_ids\":{}}},\"events\":[{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721351356,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"44cb7937-93c4-4d54-9edf-b7683aec41a9\"},\"network\":\"device_apple_ida\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721352251,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"b38cdc14ffde6226f2f54e5c82f9d0d1dba07796\"},\"network\":\"device_sha1\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721336557,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"323ba4ba1502ac246c1a880ded9d6513\"},\"network\":\"device_md5\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721336557,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"44626f1b-6f14-49a1-8273-a45e256cef18\"},\"network\":\"device_apple_idb\",\"segments\":[]}}}]}";

        Assert.assertFalse(comparator.compare(json1, json2));
    }

    @Test
    public void testCompareIgnoresPath() {
        JsonComparator comparator = new JsonComparator(Sets.newHashSet("root/events/tstamp"));

        String json1 = "{\"visitor\":{\"com.collective.pythia.avro.Visitor\":{\"cookie_id\":\"13174ef5358fb01\",\"segments\":[],\"edges\":{},\"behaviors\":{},\"birthdate\":0,\"association_ids\":{}}},\"events\":[{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403411351356,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"44cb7937-93c4-4d54-9edf-b7683aec41a9\"},\"network\":\"device_apple_ida\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721352251,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"b38cdc14ffde6226f2f54e5c82f9d0d1dba07796\"},\"network\":\"device_sha1\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721336557,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"323ba4ba1502ac246c1a880ded9d6513\"},\"network\":\"device_md5\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721336557,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"44626f1b-6f14-49a1-8273-a45e256cef18\"},\"network\":\"device_apple_ida\",\"segments\":[]}}}]}";
        String json2 = "{\"visitor\":{\"com.collective.pythia.avro.Visitor\":{\"cookie_id\":\"13174ef5358fb01\",\"segments\":[],\"edges\":{},\"behaviors\":{},\"birthdate\":0,\"association_ids\":{}}},\"events\":[{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721351356,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"44cb7937-93c4-4d54-9edf-b7683aec41a9\"},\"network\":\"device_apple_ida\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721352251,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"b38cdc14ffde6226f2f54e5c82f9d0d1dba07796\"},\"network\":\"device_sha1\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721336557,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"323ba4ba1502ac246c1a880ded9d6513\"},\"network\":\"device_md5\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721336557,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"44626f1b-6f14-49a1-8273-a45e256cef18\"},\"network\":\"device_apple_ida\",\"segments\":[]}}}]}";

        Assert.assertTrue(comparator.compare(json1, json2));
    }

    @Test
    public void testCompareIgnoresWrongPath() {
        JsonComparator comparator = new JsonComparator(Sets.newHashSet("root/event/tstamp"));

        String json1 = "{\"visitor\":{\"com.collective.pythia.avro.Visitor\":{\"cookie_id\":\"13174ef5358fb01\",\"segments\":[],\"edges\":{},\"behaviors\":{},\"birthdate\":0,\"association_ids\":{}}},\"events\":[{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403411351356,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"44cb7937-93c4-4d54-9edf-b7683aec41a9\"},\"network\":\"device_apple_ida\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721352251,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"b38cdc14ffde6226f2f54e5c82f9d0d1dba07796\"},\"network\":\"device_sha1\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721336557,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"323ba4ba1502ac246c1a880ded9d6513\"},\"network\":\"device_md5\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721336557,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"44626f1b-6f14-49a1-8273-a45e256cef18\"},\"network\":\"device_apple_ida\",\"segments\":[]}}}]}";
        String json2 = "{\"visitor\":{\"com.collective.pythia.avro.Visitor\":{\"cookie_id\":\"13174ef5358fb01\",\"segments\":[],\"edges\":{},\"behaviors\":{},\"birthdate\":0,\"association_ids\":{}}},\"events\":[{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721351356,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"44cb7937-93c4-4d54-9edf-b7683aec41a9\"},\"network\":\"device_apple_ida\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721352251,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"b38cdc14ffde6226f2f54e5c82f9d0d1dba07796\"},\"network\":\"device_sha1\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721336557,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"323ba4ba1502ac246c1a880ded9d6513\"},\"network\":\"device_md5\",\"segments\":[]}}},{\"cookie_id\":\"13174ef5358fb01\",\"tstamp\":1403721336557,\"edge\":\"multiscreen\",\"changes\":{\"com.collective.pythia.avro.Command\":{\"operation\":\"ADD\",\"association_id\":{\"string\":\"44626f1b-6f14-49a1-8273-a45e256cef18\"},\"network\":\"device_apple_ida\",\"segments\":[]}}}]}";

        Assert.assertFalse(comparator.compare(json1, json2));
    }

}
