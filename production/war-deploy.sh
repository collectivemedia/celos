#!/bin/bash
set -e
set -x

# Deploy WAR to local Tomcat

sudo /etc/init.d/tomcat6 stop
rm -rf /var/lib/tomcat6/webapps/celos
cp target/celos-1.0.0.war /var/lib/tomcat6/webapps/celos.war
sudo /etc/init.d/tomcat6 start
