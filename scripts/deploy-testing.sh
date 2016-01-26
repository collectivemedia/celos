#!/usr/bin/env bash
set -x
set -e
[[ -z ${GIT_COMMIT} ]] && echo pls specify GIT_COMMIT && exit 1

export CELOS_USER=celos
export INVENTORY_SERVER=scripts/inventory/testing-server
export INVENTORY_UI=scripts/inventory/testing-ui
export ANSIBLE_SSH_ARGS=""

scripts/build.sh

scripts/server-and-ui-action.sh deploy
