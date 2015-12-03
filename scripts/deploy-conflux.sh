#!/usr/bin/env bash
set -x
set -e
export CELOS_USER=obaskakov

#scripts/build.sh
#ansible-playbook scripts/playbooks/kinit.yaml -c ssh -u ${CELOS_USER} -i ${INVENTORY_SERVER}

export INVENTORY_SERVER=scripts/inventory/conflux-server
export INVENTORY_UI=scripts/inventory/conflux-ui
./scripts/server-and-ui-action.sh stop

export INVENTORY_SERVER=scripts/inventory/testing-server
export INVENTORY_UI=scripts/inventory/testing-ui
./scripts/server-and-ui-action.sh stop
