#!/usr/bin/env bash
set -x
set -e

[[ -z ${SERVICE_NAME} ]] && echo pls set SERVICE_NAME && exit 1
[[ -z ${SERVICE_USER} ]] && echo pls set SERVICE_USER && exit 1
[[ -z ${DEPLOY_HOST} ]] && echo pls set DEPLOY_HOST && exit 1
[[ -z ${DEPLOY_PORT} ]] && echo pls set DEPLOY_PORT && exit 1

SV="/sbin/sv"

DEV_NULL="/dev/null"


SERVICE_HOME="$(eval echo ~${SERVICE_USER})"

if [[ ! -d ${SERVICE_HOME} ]]
then
    echo wrong home path
    exit 1
fi

SV_PATH0="runit-sv"
SV_PATH="${SERVICE_HOME}/${SV_PATH0}"
SERVICE_DIR="${SV_PATH}/${SERVICE_NAME}"

# FIXME dirty hack
if [[ $SERVICE_NAME == "celos-server" ]]
then
JAR_FILE="celos-0.1.jar"
else
JAR_FILE="${SERVICE_NAME}-0.1.jar"
fi

JAR_PATH="./${SERVICE_NAME}/build/libs/${JAR_FILE}"

DEST_ROOT="${SERVICE_HOME}/local"
RUN_SCRIPT="${DEST_ROOT}/bin/${SERVICE_NAME}"

mkdir -p ${SV_PATH}
# process supervise
mkdir -p ${SERVICE_DIR}/supervise
chmod 755 ${SERVICE_DIR}/supervise
[ -p ${SERVICE_DIR}/supervise/ok ]      || mkfifo ${SERVICE_DIR}/supervise/ok
[ -p ${SERVICE_DIR}/supervise/control ] || mkfifo ${SERVICE_DIR}/supervise/control
# logs supervise
mkdir -p ${SERVICE_DIR}/log
mkdir -p ${SERVICE_DIR}/log/supervise
chmod 755 ${SERVICE_DIR}/log/supervise
[ -p ${SERVICE_DIR}/log/supervise/ok ]      || mkfifo ${SERVICE_DIR}/log/supervise/ok
[ -p ${SERVICE_DIR}/log/supervise/control ] || mkfifo ${SERVICE_DIR}/log/supervise/control

# process run script
mkdir -p ${SERVICE_DIR}
echo >  ${SERVICE_DIR}/run '#!/usr/bin/env bash'
echo >>  ${SERVICE_DIR}/run "exec chpst -u ${SERVICE_USER} ${RUN_SCRIPT}"
chmod a+x ${SERVICE_DIR}/run
# process check script
echo >  ${SERVICE_DIR}/check '#!/usr/bin/env bash'
echo >>  ${SERVICE_DIR}/check "exec curl \"http://${DEPLOY_HOST}:${DEPLOY_PORT}\" &> ${DEV_NULL}"
chmod a+x ${SERVICE_DIR}/check
# process logs
mkdir -p ${SERVICE_DIR}/log
echo >  ${SERVICE_DIR}/log/run '#!/usr/bin/env bash'
echo >> ${SERVICE_DIR}/log/run "exec chpst -u ${SERVICE_USER} svlogd -tt ${SERVICE_DIR}/log/"
chmod a+x ${SERVICE_DIR}/log/run
# process programm
mkdir -p ${DEST_ROOT}/bin
echo >  ${DEST_ROOT}/bin/${SERVICE_NAME} '#!/usr/bin/env bash'
# FIXME dirty hack
if [[ $SERVICE_NAME == "celos-server" ]]
then
echo >> ${DEST_ROOT}/bin/${SERVICE_NAME} "exec java -jar ${DEST_ROOT}/lib/${JAR_FILE} --port ${DEPLOY_PORT}"
else
echo >> ${DEST_ROOT}/bin/${SERVICE_NAME} "exec java -jar ${DEST_ROOT}/lib/${JAR_FILE} --port ${DEPLOY_PORT} --celosAddr http://localhost"
fi

chmod a+x ${DEST_ROOT}/bin/${SERVICE_NAME}
# restart service
if ${SV} check ${SERVICE_NAME} 2> ${DEV_NULL} ;
then
    ln -sf ${SERVICE_DIR} /etc/service/${SERVICE_NAME}
    ${SV} restart ${SERVICE_NAME}
else
    # runsv starts new service with delay, so this fixes 'fail: $SERVICE_NAME: runsv not running'
    ln -sf ${SERVICE_DIR} /etc/service/${SERVICE_NAME}
    i=0
    while (( i <= 7 )) && ! ${SV} check ${SERVICE_NAME} 2> ${DEV_NULL}
    do
        echo "runv isn't runnning, wait 1 sec"
        (( i += 1 ))
        sleep 1
    done
    ${SV} start ${SERVICE_NAME}
fi
# need to deploy from different users
chmod a+w /etc/service/${SERVICE_NAME}
