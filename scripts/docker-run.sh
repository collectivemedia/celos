#!/bin/bash
# set -x
# set -e

if [ -z "$(sudo docker ps | grep 'celosnn ')" ]; then
    ./scripts/docker-stop.sh

    sudo docker run -h slave1 --privileged -d -t --name celosdn01 akonopko/hadoop-oozie
    sudo docker run -h slave2 --privileged -d -t --name celosdn02 akonopko/hadoop-oozie
    sudo docker run -h master --privileged -d -t -e SLAVES=2 --name celosnn --link celosdn01:slave1 --link celosdn02:slave2 akonopko/hadoop-oozie

    IP_ADDRESS="$(sudo docker ps | grep 'celosnn ' | awk '{print $1}' | xargs sudo docker inspect | grep IPAddress | awk '{print $2}' | tr -d \" | tr -d ,)"
    ssh-keygen -R $IP_ADDRESS

    ssh-add scripts/test_rsa
    sleep 1
    ssh -o StrictHostKeyChecking=no celos@$IP_ADDRESS "ls"

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
