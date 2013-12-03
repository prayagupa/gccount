#!/bin/bash

GRAILS_DIRECTORY="/usr/local"
GRAILS_VERSION=`grep app.grails.version application.properties | cut -d'=' -f2`
GRAILS_HOME="$GRAILS_DIRECTORY/grails-$GRAILS_VERSION"
GRAILS_OPTS="-Xmx2G -Xms1024m -XX:MaxPermSize=1024m"

export GRAILS_HOME
#export GRAILS_OPTS
#$GRAILS_HOME/bin/grails $*

$GRAILS_HOME/bin/grails compile
#$GRAILS_HOME/bin/grails test-app
#echo "TESTS SUCCESSFUL..."
#$GRAILS_HOME/bin/grails -Dserver.port=8443 run-app
