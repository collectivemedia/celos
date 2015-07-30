#!/usr/bin/env bash
set -x
set -e

[[ -z ${SERVICE_NAME} ]] && exit 1

DEPLOY_HOST=admin1.lga2.collective-media.net
JAR_FILE="${SERVICE_NAME}-0.1.jar"
JAR_PATH="./${SERVICE_NAME}/build/libs/${JAR_FILE}"
SERVICE_USER="celos-ci"
DEST_ROOT="/home/celos-ci/local"

./gradlew clean test "${SERVICE_NAME}:jar"

mkdir -p ${DEST_ROOT}/bin
echo >  ${DEST_ROOT}/bin/${SERVICE_NAME} "#!/usr/bin/env bash"
echo >> ${DEST_ROOT}/bin/${SERVICE_NAME} "exec chpst -u ${SERVICE_USER} ${DEST_ROOT}/lib/${JAR_FILE}"

mkdir -p ${DEST_ROOT}
cp -f ${JAR_PATH} ${DEST_ROOT}/lib/
/sbin/sv restart ${SERVICE_NAME}


#ansible all -i "${DEPLOY_HOST}," -u "${SERVICE_USER}" -m command -a "mkdir -p ${DEST_PATH}"
#ansible all -i "${DEPLOY_HOST}," -u "${SERVICE_USER}" -m copy -a "src=${JAR_PATH} dest=${DEST_PATH}"
#ansible all -i "${DEPLOY_HOST}," -u "${SERVICE_USER}" -m command -a "sv restart ${SERVICE_NAME}"

# sleep 0.5
#curl http://localhost:8080 | diff - test.tmp
