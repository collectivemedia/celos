#!/usr/bin/env bash
set -x
set -e

[[ -z ${GIT_COMMIT} ]] && echo pls specify GIT_COMMIT && exit 1

CELOS_USER=celos
INVENTORY_SERVER=scripts/inventory/production-server
INVENTORY_UI=scripts/inventory/testing-ui

scripts/build.sh

export ANSIBLE_SSH_ARGS=""
# parse server host and port from inventory file
TMP1="$(head -2 ${INVENTORY_SERVER} | tail -1)"
TMP2="$(grep service_port ${INVENTORY_SERVER} | cut -d '=' -f 2)"
SERVER_URL="http://${TMP1}:${TMP2}"

ansible-playbook "scripts/playbooks/celos-${CELOS_ACTION}.yaml" -c ssh -u ${CELOS_USER} -i ${INVENTORY_UI} -e "@scripts/params/common-ui.json" -e service_discovery=${SERVER_URL} -e service_version=${GIT_COMMIT}
