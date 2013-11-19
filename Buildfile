repositories.remote << 'http://repo1.maven.org/maven2/'

define 'Celos' do
  project.version = '0.1.0'

  compile.options.source = '1.6'
  compile.options.target = '1.6'

  compile.with 'commons-lang:commons-lang:jar:2.6'
  compile.with 'joda-time:joda-time:jar:2.3'
end
