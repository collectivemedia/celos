#!/usr/bin/env bash
set -x
set -e
export CELOS_USER=celos
export INVENTORY_SERVER=scripts/inventory/production-server
export INVENTORY_UI=scripts/inventory/production-ui
./scripts/server-and-ui-action.sh stop

export INVENTORY_SERVER=scripts/inventory/jdbc-production-server
export INVENTORY_UI=scripts/inventory/jdbc-production-ui
./scripts/server-and-ui-action.sh stop
