#!/bin/bash
set -e
set -x

buildr clean package
sudo /etc/init.d/tomcat7 stop
sudo rm -rf /var/lib/tomcat7/webapps/celos
sudo cp target/celos-0.1.war /var/lib/tomcat7/webapps/celos.war
sudo /etc/init.d/tomcat7 start
