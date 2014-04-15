#!/bin/bash
set -e
set -x

# Deploy WAR to local Tomcat (usually run on test cluster)

sudo /etc/init.d/tomcat7 stop
sudo rm -rf /var/lib/tomcat7/webapps/celos
sudo cp target/celos-1.0.0.war /var/lib/tomcat7/webapps/celos.war
sudo /etc/init.d/tomcat7 start
