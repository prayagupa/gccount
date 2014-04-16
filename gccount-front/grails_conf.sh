#!/bin/sh
#GRAILS_OPTS="-Xmx2G -Xms1024m -XX:MaxPermSize=1024m"
#export GRAILS_OPTS

GRAILS_DIRECTORY="/usr/local"
GRAILS_VERSION=`grep app.grails.version application.properties | cut -d'=' -f2`
GRAILS_HOME="$GRAILS_DIRECTORY/grails-$GRAILS_VERSION"

export GRAILS_HOME
echo "GRAILS_HOME = $GRAILS_HOME"

