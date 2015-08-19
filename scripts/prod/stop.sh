#!/usr/bin/env bash
set -x
set -e
ansible all -m shell -a "/sbin/sv stop {{service_name}}" -i scripts/prod/conf/inventory-server -e "@scripts/prod/conf/celos-params.json"
ansible all -m shell -a "/sbin/sv stop {{service_name}}" -i scripts/prod/conf/inventory-ui -e "@scripts/prod/conf/celos-params.json"
