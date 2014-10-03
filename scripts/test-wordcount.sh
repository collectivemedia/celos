#!/bin/bash
set -e
set -x

(cd samples/wordcount; gradle build deploy_dir)
gradle build
java -jar build/libs/celos-ci-2.0.jar com.collective.celos.ci.CelosCi --deployDir build/celos_deploy --target sftp://celos001.ny7.collective-media.net/home/akonopko/target.json --workflowName wordcount  --mode TEST --testDir src/test/celos-ci
