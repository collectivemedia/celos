#!/usr/bin/env bash
set -x
set -e
[[ -z ${GIT_COMMIT} ]] && echo pls specify GIT_COMMIT && exit 1

# FIXME
export CELOS_USER=celos-ci
export INVENTORY_SERVER=scripts/inventory/integration-server
export INVENTORY_UI=scripts/inventory/integration-ui
export ANSIBLE_SSH_ARGS=""

scripts/build.sh
ansible-playbook scripts/playbooks/kinit.yaml -c ssh -u ${CELOS_USER} -i ${INVENTORY_SERVER}

./scripts/server-and-ui-action.sh deploy
# begin stop-strat
./scripts/server-and-ui-action.sh check
./scripts/server-and-ui-action.sh stop
! ./scripts/server-and-ui-action.sh check
./scripts/server-and-ui-action.sh start
./scripts/server-and-ui-action.sh check
# end stop-strat
ansible-playbook scripts/test/celos_test.yaml -c ssh -u ${CELOS_USER} -i ${INVENTORY_SERVER} -e @scripts/params/common-server.json -e service_version=${GIT_COMMIT}
ansible-playbook scripts/test/ui-test.yaml  -c ssh -u ${CELOS_USER} -i ${INVENTORY_UI} -e @scripts/params/common-ui.json -e service_version=${GIT_COMMIT}
# cleanup
./scripts/server-and-ui-action.sh purge
