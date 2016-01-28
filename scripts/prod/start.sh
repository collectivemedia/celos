#!/usr/bin/env bash
set -x
set -e
export CELOS_USER=celos

ACTION=start

export INVENTORY_SERVER=scripts/inventory/production-server
export INVENTORY_UI=scripts/inventory/production-ui
scripts/server-and-ui-action.sh ${ACTION}

export INVENTORY_SERVER=scripts/inventory/conflux-server
export INVENTORY_UI=scripts/inventory/conflux-ui
./scripts/server-and-ui-action.sh ${ACTION}
