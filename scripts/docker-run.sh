#!/bin/bash
sudo docker run -h slave1 --privileged -d -t --name celosdn01 akonopko/hadoop-oozie
sudo docker run -h slave2 --privileged -d -t --name celosdn02 akonopko/hadoop-oozie
sudo docker run -h master --privileged -d -t -e SLAVES=2 --name celosnn --link celosdn01:slave1 --link celosdn02:slave2 akonopko/hadoop-oozie

