#!/usr/bin/env bash
set -x
set -e
[[ -z ${GIT_COMMIT} ]] && echo pls specify GIT_COMMIT && exit 1

export CELOS_USER=celos
export INVENTORY_SERVER=scripts/inventory/production-server
export INVENTORY_UI=scripts/inventory/production-ui
export ANSIBLE_SSH_ARGS=""

scripts/build.sh

scripts/server-and-ui-action.sh deploy


MINUTES_JAN01_2015=23667720
MINUTES_SINCE_JAN01_2015=$(($(date +%s) / 60 - MINUTES_JAN01_2015))
export CELOS_BUILD_NUMBER=$MINUTES_SINCE_JAN01_2015
./gradlew clean celos-server:uploadArchives celos-ci:uploadArchives celos-common:uploadArchives
(cd scripts/test/check-upload && ./gradlew checkVersion --refresh-dependencies)
