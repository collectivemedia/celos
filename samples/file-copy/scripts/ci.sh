#!/bin/bash
set -e
set -x

# Run integration tests against currently checkout local source on test cluster.
cd ..
rm -fr celos_deploy
mkdir -p celos_deploy/hdfs

cp src/main/celos/* celos_deploy
cp src/main/oozie/* celos_deploy/hdfs

java -jar ../../celos-ci/build/libs/celos-ci-fat-0.1.jar --testDir src/test/celos-ci --deployDir celos_deploy --workflowName file-copy --mode TEST --target file:///home/celos-ci/celos-settings/targets/test.json

echo You win! All tests OK.