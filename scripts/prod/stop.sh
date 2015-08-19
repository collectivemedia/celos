#!/usr/bin/env bash
set -x
set -e
ansible-playbook ./scripts/prod/celos-stop.yaml -i scripts/prod/conf/inventory-server -e "@scripts/prod/conf/celos-params.json"
ansible-playbook ./scripts/prod/celos-stop.yaml -i scripts/prod/conf/inventory-ui -e "@scripts/prod/conf/celos-params.json"
