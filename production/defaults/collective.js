var CELOS_USER = "celos";

var CELOS_DEFAULT_OOZIE = "http://admin1.ny7.collective-media.net:11000/oozie";

var CELOS_DEFAULT_HDFS = "hdfs://nameservice1";

var JOB_TRACKER = "admin1.ny7.collective-media.net:8032";

var HIVE_METASTORE = "thrift://admin1.ny7.collective-media.net:9083";

var CELOS_DEFAULT_OOZIE_PROPERTIES = {
    "user.name": CELOS_USER,
    "jobTracker" : JOB_TRACKER,
    "nameNode" : CELOS_DEFAULT_HDFS,
    "hiveMetastore": HIVE_METASTORE,
    "oozie.use.system.libpath": "true"
};
