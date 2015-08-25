#!/usr/bin/env bash
set -e

SV_TIMEOUT=60

while [[ $# > 1 ]]
do
case $1 in
    --SERVICE_NAME)
    shift # past argument
    SERVICE_NAME="$1"
    ;;
    --SERVICE_DIR)
    shift # past argument
    SERVICE_DIR="$1"
    ;;
    *)
        echo $@
        echo usage: SERVICE_NAME ... SERVICE_DIR ...
        exit 1  # unknown option
    ;;
esac
shift # past argument
done

[[ -z ${SERVICE_NAME} ]] && echo pls specify SERVICE_NAME && exit 1
[[ -z ${SERVICE_DIR} ]] && echo pls specify SERVICE_DIR && exit 1

set -x

if [ -e /sbin/sv ]
then
    SV=/sbin/sv
else
    SV=sv
fi

ln -sf ${SERVICE_DIR} /etc/service/
chmod a+w "/etc/service/${SERVICE_NAME}"
# check runsv is running
if ${SV} status ${SERVICE_NAME} &> /dev/null
then
    ${SV} -w ${SV_TIMEOUT} restart ${SERVICE_NAME}/log
    ${SV} -w ${SV_TIMEOUT} restart ${SERVICE_NAME}
else
    # runsv starts new service with delay,
    # so this needs to fix 'fail: $SERVICE_NAME: runsv not running'
    i=0
    while (( i <= ${SV_TIMEOUT} )) && ! ${SV} status ${SERVICE_NAME} 2> /dev/null
    do
        (( i += 1 ))
        sleep 1
    done
    sleep 1
    ${SV} -w ${SV_TIMEOUT} start ${SERVICE_NAME}/log
    ${SV} -w ${SV_TIMEOUT} start ${SERVICE_NAME}
fi
