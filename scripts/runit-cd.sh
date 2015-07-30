#!/usr/bin/env bash
set -x
set -e

[[ -z ${SERVICE_NAME} ]] && exit 1

DEPLOY_HOST=admin1.lga2.collective-media.net
JAR_PATH="./${SERVICE_NAME}/build/libs/${SERVICE_NAME}-0.1.jar"
SERVICE_USER="celos-ci"
DEST_PATH="/home/celos-ci/local/lib/"

./gradlew clean test "${SERVICE_NAME}:jar"

mkdir -p ${DEST_PATH}
cp -f ${JAR_PATH} ${DEST_PATH}
/sbin/sv restart ${SERVICE_NAME}

#ansible all -i "${DEPLOY_HOST}," -u "${SERVICE_USER}" -m command -a "mkdir -p ${DEST_PATH}"
#ansible all -i "${DEPLOY_HOST}," -u "${SERVICE_USER}" -m copy -a "src=${JAR_PATH} dest=${DEST_PATH}"
#ansible all -i "${DEPLOY_HOST}," -u "${SERVICE_USER}" -m command -a "sv restart ${SERVICE_NAME}"

# sleep 0.5
#curl http://localhost:8080 | diff - test.tmp
