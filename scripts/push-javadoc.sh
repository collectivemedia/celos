#!/usr/bin/env bash

# get sources path
DIR="$(dirname ${BASH_SOURCE[0]})/.."
INDEXES="com.collective.celos.ci.config.deploy com.collective.celos.ci.testing.structure.fixobject com.collective.celos.ui com.collective.celos.ci.testing.fixtures.deploy com.collective.celos.ci.testing.structure.tree com.collective.celos.ci.mode.test com.collective.celos.ci.config com.collective.celos.trigger com.collective.celos com.collective.celos.ci.mode com.collective.celos.ci.testing.fixtures.create com.collective.celos.ci.deploy com.collective.celos.ci com.collective.celos.ci.testing.fixtures.compare.json com.collective.celos.ci.testing.tree com.collective.celos.ci.testing.fixtures.deploy.hive com.collective.celos.server com.collective.celos.ci.testing.fixtures.compare com.collective.celos.ci.testing.fixtures.convert com.collective.celos.servlet"
CLASSES="${DIR}/celos-server/build/classes/test:${DIR}/celos-server/build/classes/main:${DIR}/celos-server/build/resources/test:${DIR}/celos-server/build/resources/main"
SOURCES="${DIR}/celos-common/src/main/java:${DIR}/celos-ui/src/main/java:${DIR}/celos-ui/src/test/java:${DIR}/celos-ui/src/main/webapp:${DIR}/celos-server/src/main/java:${DIR}/celos-server/src/test/java:${DIR}/celos-server/src/main/resources:${DIR}/celos-server/src/main/webapp:${DIR}/celos-server/src/test/resources:${DIR}/celos-ci/src/main/java:${DIR}/celos-ci/src/test/java:${DIR}/celos-ci/src/main/resources:${DIR}/celos-ci/src/test/resources"
JAVADOC=${JAVA_HOME:-"/usr/java/latest"}/bin/javadoc

set -e
set -x

cd ${DIR}
git clean -f -d
git reset --quiet .
git checkout .
[ $(git rev-parse --abbrev-ref HEAD) == "gh-pages" ] && exit 1
${JAVADOC} -encoding UTF8 -protected -splitindex ${INDEXES} -classpath ${CLASSES} -sourcepath ${SOURCES} -d ${DIR}/javadoc
git add ./javadoc
git stash
git branch -D gh-pages
git checkout gh-pages
git clean -f -d
git reset --quiet .
git checkout .
git rm -rf --quiet ${DIR}/javadoc
rm -rf ./javadoc
git stash pop --quiet
git reset --quiet .
git add ./javadoc
# check current branch name
[ $(git rev-parse --abbrev-ref HEAD) == "gh-pages" ] || exit 1
git commit -m "Published documentation to [gh-pages]."
git push origin gh-pages
git checkout -
