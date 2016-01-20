#!/usr/bin/env bash
set -x
set -e

[[ -z ${CELOS_USER} ]] && echo pls specify CELOS_USER && exit 1
[[ -z ${GIT_COMMIT} ]] && echo pls specify GIT_COMMIT && exit 1
if [[ -f ${INVENTORY_SERVER} ]]
then true
else echo "pls specify INVENTORY_SERVER and make sure INVENTORY_SERVER exists" && exit 1
fi
if [[ -f ${INVENTORY_UI} ]]
then true
else echo "pls specify INVENTORY_UI and make sure INVENTORY_UI exists" && exit 1
fi

CELOS_ACTION=$1

[[ -z ${CELOS_ACTION} ]] && echo 'usage ./scripts/server-and-ui-action.sh CELOS_ACTION (deploy, check, purge, start, stop)' && exit 1
if [ $CELOS_ACTION = "deploy" ] ||
   [ $CELOS_ACTION = "check"  ] ||
   [ $CELOS_ACTION = "purge"  ] ||
   [ $CELOS_ACTION = "start"  ] ||
   [ $CELOS_ACTION = "stop"   ]
then true
else echo 'usage ./scripts/server-and-ui-action.sh CELOS_ACTION (deploy, check, purge, start, stop)' && exit 1
fi

export ANSIBLE_SSH_ARGS=""

# parse server host and port from inventory file
TMP1="$(grep service_name ${INVENTORY_SERVER} | cut -d '=' -f 2)"
TMP2="$(grep service_port ${INVENTORY_SERVER} | cut -d '=' -f 2)"
SERVER_URL="http://${TMP1}:${TMP2}"

ansible-playbook "scripts/playbooks/celos-${CELOS_ACTION}.yaml" -c ssh -u ${CELOS_USER} -i ${INVENTORY_UI} -e "@scripts/params/common-ui.json" -e service_discovery=${SERVER_URL} -e service_version=${GIT_COMMIT}
