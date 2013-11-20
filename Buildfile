repositories.remote << 'http://repo1.maven.org/maven2/'
repositories.remote << 'https://repository.cloudera.com/artifactory/cloudera-repos/'

OOZIE_CLIENT = 'org.apache.oozie:oozie-client:jar:3.3.0-cdh4.2.1'
HADOOP = transitive('org.apache.hadoop:hadoop-client:jar:2.0.0-cdh4.2.1')
COMMONS = [
  'commons-lang:commons-lang:jar:2.6',
  'commons-collections:commons-collections:jar:3.2.1',
]
JODA = 'joda-time:joda-time:jar:2.3'

DEPENDENCIES = [
  OOZIE_CLIENT,
  HADOOP,
  COMMONS,
  JODA,
]

define 'Celos' do
  project.version = '0.1.0'

  compile.options.source = '1.6'
  compile.options.target = '1.6'

  compile.with(DEPENDENCIES)
  
  package(:jar).include((DEPENDENCIES - HADOOP), :path => "lib")
  
end
