#!/usr/bin/env bash
set -x
set -e
export CELOS_USER=celos
export INVENTORY_SERVER=scripts/inventory/production-server
export INVENTORY_UI=scripts/inventory/production-ui
./scripts/server-and-ui-action.sh start
