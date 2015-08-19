#!/usr/bin/env bash
set -x
set -e
ansible all -m shell -a "/sbin/sv stop {{service_name}}" -i scripts/conf/inventory-prod-server -e "@scripts/conf/celos-params-all.json"
ansible all -m shell -a "/sbin/sv stop {{service_name}}" -i scripts/conf/inventory-prod-ui -e "@scripts/conf/celos-params-all.json"
