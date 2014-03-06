#!/bin/bash

echo "creating security groups now ...."
./sec_groups.rb
echo "creating machines"
./aws_provision_dysec.rb --create-cluster
echo "Done creating"
./aws_provision_dysec.rb --list
sleep 120
rm ./tmp/hosts
echo "preprovisioning stage"
./aws_provision_dysec.rb --start-cluster
echo "Provisioning now, passing it on to ansible"
ansible-playbook -i tmp/inventory -u ubuntu site.yaml -f 4 --skip-tags=oozie,yarn,hive && ansible-playbook -i tmp/inventory -u ubuntu site.yaml -f 4 -t oozie,yarn,hive 

