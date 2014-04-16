#!/bin/bash

MY_DIR=`dirname $0`
$MY_DIR/grails_conf.sh
GRAILS_DIRECTORY="/usr/local"
GRAILS_VERSION=`grep app.grails.version application.properties | cut -d'=' -f2`
GRAILS_HOME="$GRAILS_DIRECTORY/grails-$GRAILS_VERSION"

echo "GRAILS_HOME=>$GRAILS_HOME"

$GRAILS_HOME/bin/grails test-app unit:spock CategorySpec -echoOut
#$GRAILS_HOME/bin/grails test-app unit:spock CustomerSpec
