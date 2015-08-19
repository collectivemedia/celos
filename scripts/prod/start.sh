#!/usr/bin/env bash
set -x
set -e
ansible-playbook ./scripts/prod/celos-start.yaml -u celos -i scripts/prod/conf/inventory-server -e "@scripts/prod/conf/celos-params.json"
ansible-playbook ./scripts/prod/celos-start.yaml -u celos -i scripts/prod/conf/inventory-ui -e "@scripts/prod/conf/celos-params.json"
