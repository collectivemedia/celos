repositories.remote << 'http://repo1.maven.org/maven2/'
repositories.remote << 'https://repository.cloudera.com/artifactory/cloudera-repos/'

OOZIE_CLIENT = 'org.apache.oozie:oozie-client:jar:3.3.0-cdh4.2.1'
HADOOP = transitive('org.apache.hadoop:hadoop-client:jar:2.0.0-cdh4.2.1')
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

DEPENDENCIES = [
  OOZIE_CLIENT,
  HADOOP,
  COMMONS,
  JACKSON_CORE,
  JACKSON_DATABIND,
  JACKSON_ANNOTATIONS,
  JODA,
  JSON_SIMPLE,
]

POWERMOCK = [
  'org.powermock:powermock-module-junit4:jar:1.5.2',
  'org.powermock:powermock-api-mockito:jar:1.5.2',
]

TEST_DEPENDENCIES = [
  POWERMOCK,
]

define 'Celos' do
  project.version = '0.1.0'
  manifest['Main-Class'] = 'com.collective.celos.Main'

  compile.options.source = '1.6'
  compile.options.target = '1.6'

  compile.with(DEPENDENCIES)
  
  test.with(TEST_DEPENDENCIES)
  
#  package(:war).include((compile.dependencies - HADOOP), :path => "WEB-INF/lib")
  package :war
end
