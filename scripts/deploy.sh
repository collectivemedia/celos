#!/usr/bin/env bash
set -x
set -e
export CELOS_USER=celos
export INVENTORY_SERVER=scripts/inventory/production-server
export INVENTORY_UI=scripts/inventory/production-ui
export ANSIBLE_SSH_ARGS=""
scripts/build.sh
ansible-playbook scripts/playbooks/kinit.yaml -c ssh -u ${CELOS_USER} -i ${INVENTORY_SERVER}

ACTION=deploy

./scripts/server-and-ui-action.sh deploy

export INVENTORY_SERVER=scripts/inventory/conflux-server
export INVENTORY_UI=scripts/inventory/conflux-ui
./scripts/server-and-ui-action.sh ${ACTION}

MINUTES_JAN01_2015=23667720
MINUTES_SINCE_JAN01_2015=$(($(date +%s) / 60 - MINUTES_JAN01_2015))
export CELOS_BUILD_NUMBER=$MINUTES_SINCE_JAN01_2015
./gradlew clean celos-server:uploadArchives celos-ci:uploadArchives
(cd scripts/test/check-upload && ./gradlew checkVersion --refresh-dependencies)
