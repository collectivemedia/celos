#!/usr/bin/env bash
set -x
set -e

[[ -z ${SERVICE_NAME} ]] && echo pls set SERVICE_NAME && exit 1
[[ -z ${SERVICE_USER} ]] && echo pls set SERVICE_USER && exit 1
[[ -z ${DEPLOY_HOST} ]] && echo pls set DEPLOY_HOST && exit 1
[[ -z ${DEPLOY_PORT} ]] && echo pls set DEPLOY_PORT && exit 1


SSH_NODE="${SERVICE_USER}@${DEPLOY_HOST}"
LOCAL_SCRIPT="./production/runit/local_deploy.sh"

JAR_FILE="${SERVICE_NAME}-0.1.jar"
JAR_PATH="./${SERVICE_NAME}/build/libs/${JAR_FILE}"

SERVICE_HOME="$(ssh ${SSH_NODE} exec echo ~${SERVICE_USER})"
DEST_ROOT="${SERVICE_HOME}/local"

# process jar
./gradlew clean ${SERVICE_NAME}:jar
ssh ${SSH_NODE} "mkdir -p ${DEST_ROOT}/lib"
scp "${JAR_PATH}" "${SSH_NODE}:${DEST_ROOT}/lib/"
#scp "./production/runit/${LOCAL_SCRIPT}" "${SSH_NODE}:${DEST_ROOT}/"
# run local script
ssh ${SSH_NODE} "DEPLOY_HOST=$DEPLOY_HOST DEPLOY_PORT=$DEPLOY_PORT \
                 SERVICE_NAME=$SERVICE_NAME SERVICE_USER=$SERVICE_USER \
                 bash -s" < ${LOCAL_SCRIPT}

curl "http://${DEPLOY_HOST}:${DEPLOY_PORT}/ui" 2> /dev/null | diff - production/runit/hello-ui.txt
