#!/bin/bash
set -e
set -x

./scripts/docker/docker-run.sh

IP_ADDRESS="$(sudo docker ps | grep 'celosnn ' | awk '{print $1}' | xargs sudo docker inspect | grep IPAddress | awk '{print $2}' | tr -d \" | tr -d ,)"

echo 'Starting tests'

rm -f scripts/docker/tmp_inventory

echo "[hadoop_masters]" >> scripts/docker/tmp_inventory
echo "$IP_ADDRESS" >> scripts/docker/tmp_inventory

buildr clean package

ansible-playbook -i scripts/docker/tmp_inventory -u celos scripts/docker/celos_deploy_on_docker.yaml
ansible-playbook -i scripts/docker/tmp_inventory -u celos scripts/test/celos_test.yaml
ansible-playbook -i scripts/docker/tmp_inventory -u celos scripts/test/test-workflow.yaml --extra-vars "workflow=file-copy sample_time=2013-12-20T20:00Z"
ansible-playbook -i scripts/docker/tmp_inventory -u celos scripts/test/test-workflow.yaml --extra-vars "workflow=file-copy-with-bad-output sample_time=2013-12-20T20:00Z" && false
ansible-playbook -i scripts/docker/tmp_inventory -u celos scripts/test/test-workflow.yaml --extra-vars "workflow=file-copy-with-missing-output sample_time=2013-12-20T20:00Z" && false

(cd samples/wordcount && ./gradlew build --project-prop target_uri=sftp://celos@$IP_ADDRESS/etc/celos/targets/testing.json celos_ci )

echo You win! All tests OK.

