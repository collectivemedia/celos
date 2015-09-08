// Set this to your Hadoop username
var CELOS_USER = "manuel";
// Set this to your Oozie API endpoint
var CELOS_DEFAULT_OOZIE = "http://oozie002.ny7.collective-media.net:11000/oozie";
// Set this to your HDFS name node
var CELOS_DEFAULT_HDFS = "hdfs://nameservice1";
// Set this to your Hadoop job tracker
var JOB_TRACKER = "cldmgr001.ewr004.collective-media.net:8032";

// No changes beyond this point should be required.

var CELOS_DEFAULT_OOZIE_PROPERTIES = {
    "user.name": CELOS_USER,
    "jobTracker" : JOB_TRACKER,
    "nameNode" : CELOS_DEFAULT_HDFS
};
