#!/usr/bin/env bash
set -x

[[ -z ${SERVICE_NAME} ]] && exit 1

SERVICE_HOME="/home/celos-ci"
SERVICE_USER="celos-ci"
SV_PATH0="runit-sv"
SV_PATH="${SERVICE_HOME}/${SV_PATH0}"

RUN_SCRIPT="${SERVICE_HOME}/local/bin/${SERVICE_NAME}"


#[[ -z ${SERVICE_USER} ]] && exit 1
#[[ -z ${RUN_SCRIPT}   ]] && exit 1
#[[ -z ${SV_PATH}      ]] && exit 1


SERVICE_DIR="${SV_PATH}/${SERVICE_NAME}"
#SERVICE_LOGS="/var/log/${SERVICE_NAME}/runit"

if [ -d ${SERVICE_DIR} ]
then
    echo service exists: ${SERVICE_DIR}
    exit 1
fi

mkdir -p ${SV_PATH}

# unlink runit before making changes
sv stop ${SERVICE_NAME}
sleep 1
sv down ${SERVICE_NAME}
rm -f /etc/service/${SERVICE_NAME}
mkdir -p ${SERVICE_DIR}
mkdir -p ${SERVICE_DIR}/log
# process supervise
mkdir -p ${SERVICE_DIR}/supervise
chmod 755 ${SERVICE_DIR}/supervise
mkfifo ${SERVICE_DIR}/supervise/ok
mkfifo ${SERVICE_DIR}/supervise/control
# process supervise
echo >  ${SERVICE_DIR}/run "#!/usr/bin/env bash"
echo >> ${SERVICE_DIR}/run "exec chpst -u ${SERVICE_USER} ${RUN_SCRIPT}"
chmod a+x ${SERVICE_DIR}/run
#mkdir -p ${SERVICE_LOGS}
echo >  ${SERVICE_DIR}/log/run "#!/usr/bin/env bash"
echo >> ${SERVICE_DIR}/log/run "exec chpst -u ${SERVICE_USER} svlogd -tt ${SERVICE_DIR}/log/"
chmod a+x ${SERVICE_DIR}/log/run
ln -s ${SERVICE_DIR} /etc/service/${SERVICE_NAME}

echo "DONE"
