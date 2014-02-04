#!/bin/bash
set -e
set -x

# Deploy WAR to local Tomcat

sudo /etc/init.d/tomcat6 stop
sudo rm -rf /var/lib/tomcat6/webapps/celos
sudo cp target/celos-0.1.0.war /var/lib/tomcat6/webapps/celos.war
sudo /etc/init.d/tomcat6 start
