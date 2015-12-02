#!/usr/bin/env bash
set -x
set -e
ansible-playbook ./scripts/prod/celos-stop.yaml -u celos -i scripts/prod/conf/inventory-server -e "@scripts/prod/conf/celos-testing.json"
ansible-playbook ./scripts/prod/celos-stop.yaml -u celos -i scripts/prod/conf/inventory-ui -e "@scripts/prod/conf/celos-testing.json"
