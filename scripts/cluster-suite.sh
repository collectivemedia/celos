#!/bin/bash
# set -e
# set -x

./docker-run.sh

IP_ADDRESS="$(sudo docker ps | grep 'celosnn ' | awk '{print $1}' | xargs sudo docker inspect | grep IPAddress | awk '{print $2}' | tr -d \" | tr -d ,)"

echo 'Starting tests'

rm -f scripts/tmp_inventory

echo "[hadoop_masters]" >> scripts/tmp_inventory
echo "$IP_ADDRESS" >> scripts/tmp_inventory

buildr build package TEST=no
ansible-playbook -i scripts/tmp_inventory -u celos scripts/test/celos_deploy.yaml

ansible-playbook -i scripts/tmp_inventory -u celos  scripts/test/celos_test.yaml
ansible-playbook -i scripts/tmp_inventory -u celos  scripts/test/test-workflow.yaml --extra-vars "workflow=file-copy sample_time=2013-12-20T20:00Z"
ansible-playbook -i scripts/tmp_inventory -u celos  scripts/test/test-workflow.yaml --extra-vars "workflow=file-copy-with-bad-output sample_time=2013-12-20T20:00Z" && false
ansible-playbook -i scripts/tmp_inventory -u celos  scripts/test/test-workflow.yaml --extra-vars "workflow=file-copy-with-missing-output sample_time=2013-12-20T20:00Z" && false

./gradlew build
(cd samples/wordcount && ./gradlew build --project-prop ip_address=$IP_ADDRESS celos_ci )

echo You win! All tests OK.

