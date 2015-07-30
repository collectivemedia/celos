#!/usr/bin/env bash
set -x
set -e
DEPLOY_HOST=45.55.153.133
JAR_PATH=./celos-ui-0.1.jar
SERVICE_USER="plex"
DEST_PATH="/etc/sv/celos/lib/"
SERVICE_NAME="celos"

# ./gradlew clean test distTar
/opt/local/bin/ansible all -i "${DEPLOY_HOST}," -u "${CELOS_USER}" -m copy -a "src=${JAR_PATH} dest=${DEST_PATH}"
/opt/local/bin/ansible all -i "${DEPLOY_HOST}," -u "${CELOS_USER}" -m command -a "sv restart ${SERVICE_NAME}"
# sleep 0.5
# /opt/local/bin/http 45.55.153.133:8080 | diff - test.tmp
