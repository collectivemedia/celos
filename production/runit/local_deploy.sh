#!/usr/bin/env bash
set -e

for i in "$@"
do
case $i in
    n=*|--SERVICE_NAME=*)
    SERVICE_NAME="${i#*=}"
    shift # past argument=value
    ;;
    u=*|--SERVICE_USER=*)
    SERVICE_USER="${i#*=}"
    shift # past argument=value
    ;;
    p=*|--SERVICE_PORT=*)
    SERVICE_PORT="${i#*=}"
    shift # past argument=value
    ;;
    v=*|--CELOS_VERSION=*)
    CELOS_VERSION="${i#*=}"
    shift # past argument=value
    ;;
    j=*|--JAR_FILE=*)
    JAR_FILE="${i#*=}"
    shift # past argument=value
    ;;
    j=*|--JAR_FILE=*)
    JAR_FILE="${i#*=}"
    shift # past argument=value
    ;;
    e=*|extra=*)
        shift # past argument
        SERVICE_ARGS=$@
        break
    ;;
    *)
        echo $i
        echo usage: -n SERVICE_NAME -u SERVICE_USER -p SERVICE_PORT -j JAR_FILE -v CELOS_VERSION
        exit 1  # unknown option
    ;;
esac
done

[[ -z ${SERVICE_NAME} ]] && echo pls specify SERVICE_NAME && exit 1
[[ -z ${SERVICE_USER} ]] && echo pls specify SERVICE_USER && exit 1
[[ -z ${SERVICE_PORT} ]] && echo pls specify SERVICE_PORT && exit 1
[[ -z ${JAR_FILE} ]] && echo pls specify JAR_FILE && exit 1
[[ -z ${CELOS_VERSION} ]] && echo pls specify CELOS_VERSION && exit 1

set -x

echo ${SERVICE_NAME} .... ${SERVICE_ARGS}

if [ -e /sbin/sv ]
then
    SV=/sbin/sv
else
    SV=sv
fi

SERVICE_HOME="$(eval echo ~${SERVICE_USER})"

if [[ ! -d ${SERVICE_HOME} ]]
then
    echo wrong home path
    exit 1
fi

SV_TIMEOUT=22
DEV_NULL="/dev/null"
SV_PATH="${SERVICE_HOME}/runit-sv"
SERVICE_DIR="${SV_PATH}/${SERVICE_NAME}"
DEST_ROOT="${SERVICE_HOME}/local"
RUN_SCRIPT="${DEST_ROOT}/bin/${SERVICE_NAME}"
JAR_PATH="${DEST_ROOT}/lib/${JAR_FILE}"


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
echo >>  ${SERVICE_DIR}/check "exec curl --fail \"http://localhost:${SERVICE_PORT}\" &> ${DEV_NULL}"
chmod a+x ${SERVICE_DIR}/check
# process logs
mkdir -p ${SERVICE_DIR}/log
echo >  ${SERVICE_DIR}/log/run '#!/usr/bin/env bash'
echo >> ${SERVICE_DIR}/log/run "exec chpst -u ${SERVICE_USER} svlogd -tt ${SERVICE_DIR}/log/"
chmod a+x ${SERVICE_DIR}/log/run
# process programm
mkdir -p ${DEST_ROOT}/bin
echo >  ${DEST_ROOT}/bin/${SERVICE_NAME} '#!/usr/bin/env bash'
echo >> ${DEST_ROOT}/bin/${SERVICE_NAME} "CELOS_VERSION=${CELOS_VERSION} exec java -cp \"${JAR_PATH}:/etc/hadoop/conf\" com.collective.celos.server.Main --port ${SERVICE_PORT} ${SERVICE_ARGS}"

chmod a+x ${DEST_ROOT}/bin/${SERVICE_NAME}
# check runsv is running
if ${SV} status ${SERVICE_NAME} &> ${DEV_NULL}
then
    ln -sf ${SERVICE_DIR} /etc/service/
    ${SV} -w ${SV_TIMEOUT} restart ${SERVICE_NAME}
else
    ln -sf ${SERVICE_DIR} /etc/service/
    # runsv starts new service with delay,
    # so this needs to fix 'fail: $SERVICE_NAME: runsv not running'
    i=0
    while (( i <= 7 )) && ! ${SV} status ${SERVICE_NAME} 2> ${DEV_NULL}
    do
        (( i += 1 ))
        sleep 1
    done
    ${SV} -w ${SV_TIMEOUT} start ${SERVICE_NAME}
fi
# need to deploy from different users
chmod a+w "/etc/service/${SERVICE_NAME}"
curl --fail "http://localhost:${SERVICE_PORT}/version"
