#!/usr/bin/env bash
set -x
set -e
export CELOS_USER=celos
export INVENTORY_SERVER=scripts/inventory/production-server
export INVENTORY_UI=scripts/inventory/testing-ui
export ANSIBLE_SSH_ARGS=""

scripts/build.sh

./scripts/ui-action.sh deploy
