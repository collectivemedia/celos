repositories.remote << 'http://repo1.maven.org/maven2/'
repositories.remote << 'https://repository.cloudera.com/artifactory/cloudera-repos/'

OOZIE_CLIENT = 'org.apache.oozie:oozie-client:jar:4.0.0-cdh5.1.2'
HADOOP = [
  'org.apache.hadoop:hadoop-common:jar:2.3.0-cdh5.1.2',
  'org.apache.hadoop:hadoop-hdfs:jar:2.3.0-cdh5.1.2',
  'org.apache.hadoop:hadoop-auth:jar:2.3.0-cdh5.1.2',
  'com.google.protobuf:protobuf-java:jar:2.5.0',
  'commons-logging:commons-logging:jar:1.1.3',
  'commons-configuration:commons-configuration:jar:1.10',
  'com.google.guava:guava:jar:11.0.2',
  'org.slf4j:slf4j-api:jar:1.7.5',
  'org.slf4j:slf4j-log4j12:jar:1.7.5',
  'commons-cli:commons-cli:jar:1.2',
  'commons-codec:commons-codec:jar:1.4'
]
COMMONS = [
  'commons-lang:commons-lang:jar:2.6',
  'commons-collections:commons-collections:jar:3.2.1',
  'commons-io:commons-io:jar:2.4'
]
JACKSON_CORE = 'com.fasterxml.jackson.core:jackson-core:jar:2.3.0'
JACKSON_DATABIND = 'com.fasterxml.jackson.core:jackson-databind:jar:2.3.0'
JACKSON_ANNOTATIONS = 'com.fasterxml.jackson.core:jackson-annotations:jar:2.3.0'
JODA = 'joda-time:joda-time:jar:2.3'
# used by Oozie (as per rrman Buildfile)
JSON_SIMPLE = 'com.googlecode.json-simple:json-simple:jar:1.1.1'
QUARTZ_SCHEDULER = 'org.quartz-scheduler:quartz:jar:2.1.1'
RHINO = 'org.mozilla:rhino:jar:1.7R4'
MOCKITO = 'org.mockito:mockito-all:jar:1.9.5'
JETTY = ['javax.servlet:javax.servlet-api:jar:3.1.0', 'org.eclipse.jetty.aggregate:jetty-all:jar:9.2.2.v20140723']
CLI = 'commons-cli:commons-cli:jar:1.2'
SERVLET = 'javax.servlet:servlet-api:jar:2.3'

LOG4J = ['log4j:log4j:jar:1.2.17', 'log4j:apache-log4j-extras:jar:1.2.17']

GET_OUTTA_MY_JAR = ['log4j-']

DEPENDENCIES = [
  SERVLET,
  OOZIE_CLIENT,
  HADOOP,
  COMMONS,
  JACKSON_CORE,
  JACKSON_DATABIND,
  JACKSON_ANNOTATIONS,
  JODA,
  JSON_SIMPLE,
  QUARTZ_SCHEDULER,
  RHINO,
  JETTY,
  CLI
].flatten.reject {|tdep|
  GET_OUTTA_MY_JAR.select {|x| tdep.to_s.include?(x)}.any?
}.push(LOG4J)

POWERMOCK = [
  'org.powermock:powermock-module-junit4:jar:1.5.2',
  'org.powermock:powermock-api-mockito:jar:1.5.2',
]

TEST_DEPENDENCIES = [
  POWERMOCK, MOCKITO
]

VERSION = '1.0.0'

define 'celos' do

  project.version = VERSION

  compile.options.source = '1.7'
  compile.options.target = '1.7'

  compile.with(DEPENDENCIES)
  test.with(TEST_DEPENDENCIES)

  package(:jar)
  package(:war).libs -= artifacts('javax.servlet:servlet-api:jar:2.3')
end
