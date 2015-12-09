#!/usr/bin/env bash
set -x
set -e
[[ -z ${GIT_COMMIT} ]] && echo pls specify GIT_COMMIT && exit 1
export ANSIBLE_SSH_ARGS=""
scripts/build.sh
ansible-playbook scripts/prod/kinit.yaml -c ssh -u celos -i scripts/prod/conf/inventory-server -e "@scripts/prod/conf/celos-params.json" -e "@scripts/conf/common-params-server.json"
ansible-playbook scripts/celos-deploy.yaml -c ssh -u celos -i scripts/prod/conf/inventory-server -e "@scripts/prod/conf/celos-params.json" -e "@scripts/conf/common-params-server.json" -e service_version=${GIT_COMMIT}
ansible-playbook scripts/celos-deploy.yaml -c ssh -u celos -i scripts/prod/conf/inventory-ui     -e "@scripts/prod/conf/celos-params.json" -e "@scripts/conf/common-params-ui.json" -e service_version=${GIT_COMMIT}

MINUTES_JAN01_2015=23667720
MINUTES_SINCE_JAN01_2015=$(($(date +%s) / 60 - MINUTES_JAN01_2015))
export CELOS_BUILD_NUMBER=$MINUTES_SINCE_JAN01_2015
./gradlew clean celos-server:uploadArchives celos-ci:uploadArchives
(cd scripts/test/check-upload && ./gradlew checkVersion --refresh-dependencies)
