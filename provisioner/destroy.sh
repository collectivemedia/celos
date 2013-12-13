#!/bin/bash
echo "terminating cluster"
./aws_provision_dysec.rb --terminate-cluster
echo "delete host file"
rm  ./tmp/hosts
rm ./tmp/inventory
