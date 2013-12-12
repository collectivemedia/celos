#!/bin/bash
set -e
set -x

buildr clean package
sudo /etc/init.d/tomcat7 stop
sudo rm -rf /var/lib/tomcat7/webapps/Celos-0.1.0
sudo cp target/Celos-0.1.0.war /var/lib/tomcat7/webapps/
sudo /etc/init.d/tomcat7 start
