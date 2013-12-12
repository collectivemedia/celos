#!/bin/bash
echo "terminating cluster"
./aws_provision_dysec.rb --terminate-cluster
sleep 60
echo "destroying security groups"
./sec_groups_delete.rb 
echo "delete host file"
rm  ./tmp/hosts
rm ./tmp/inventory
