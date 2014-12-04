#!/bin/bash
# set -x
# set -e

if [ -z "$(sudo docker ps | grep 'celosnn ')" ]; then
    ./scripts/docker/docker-stop.sh

    sudo docker run -h slave1 --privileged -d -t --name celosdn01 akonopko/hadoop-oozie
    sudo docker run -h slave2 --privileged -d -t --name celosdn02 akonopko/hadoop-oozie
    sudo docker run -h master --privileged -d -t -e SLAVES=2 --link celosdn01:slave1 --link celosdn02:slave2 --name celosnn akonopko/hadoop-oozie

    IP_ADDRESS1="$(sudo docker ps | grep 'celosdn01' | awk '{print $1}' | xargs sudo docker inspect | grep IPAddress | awk '{print $2}' | tr -d \" | tr -d ,)"
    IP_ADDRESS2="$(sudo docker ps | grep 'celosdn02' | awk '{print $1}' | xargs sudo docker inspect | grep IPAddress | awk '{print $2}' | tr -d \" | tr -d ,)"
    IP_ADDRESS="$(sudo docker ps | grep 'celosnn ' | awk '{print $1}' | xargs sudo docker inspect | grep IPAddress | awk '{print $2}' | tr -d \" | tr -d ,)"

    ssh-keygen -R $IP_ADDRESS1
    ssh-keygen -R $IP_ADDRESS2
    ssh-keygen -R $IP_ADDRESS

    chmod 600 scripts/docker/test_rsa
    ssh-add scripts/docker/test_rsa

    rm -f hoststempfile
    scp -o StrictHostKeyChecking=no root@$IP_ADDRESS:/etc/hosts scripts/docker/hoststempfile
    scp -o StrictHostKeyChecking=no scripts/docker/hoststempfile root@$IP_ADDRESS1:/etc/hosts
    scp -o StrictHostKeyChecking=no scripts/docker/hoststempfile root@$IP_ADDRESS2:/etc/hosts
    ssh -o StrictHostKeyChecking=no root@$IP_ADDRESS "/sbin/masterStart.sh"

    ssh-copy-id celos@$IP_ADDRESS

    $(curl $IP_ADDRESS:8080 &> /dev/null)
    stat=$?
    while [ "$stat" -ne "0" ]
    do
       echo "Waiting for main node on $IP_ADDRESS to start"
       sleep 5
       $(curl $IP_ADDRESS:8080 &> /dev/null) 
       stat=$?
    done
fi
