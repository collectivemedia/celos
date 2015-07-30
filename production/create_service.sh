#!/bin/bash

SERVICE_NAME="celos"
SERVICE_USER="plex"
RUN_SCRIPT="/Users/obaskakov/celos.sh"


[[ -z ${SERVICE_NAME} ]] && exit 1
[[ -z ${SERVICE_USER} ]] && exit 1
[[ -z ${RUN_SCRIPT}   ]] && exit 1

# TODO check /etc/sv exists

SERVICE_DIR="/etc/sv/${SERVICE_NAME}/"

# unlink runit before making changes
rm -f /etc/service/${SERVICE_NAME}
mkdir -p ${SERVICE_DIR}
echo "#!/bin/bash" > ${SERVICE_DIR}/run
echo "exec chpst -u ${SERVICE_USER} ${RUN_SCRIPT}" >> ${SERVICE_DIR}/run
mkdir -p ${SERVICE_DIR}/log
echo >  ${SERVICE_DIR}/log/run "#!/bin/bash"
echo >> ${SERVICE_DIR}/log/run "exec chpst -u ${SERVICE_USER} svlogd -tt /var/log/${SERVICE_NAME}/runit"
mkdir -p ${SERVICE_DIR}/supervise
chown -R root ${SERVICE_DIR}
chown -R ${SERVICE_USER} ${SERVICE_DIR}/supervise
ln -s ${SERVICE_DIR} /etc/service/${SERVICE_NAME}

echo "DONE"
