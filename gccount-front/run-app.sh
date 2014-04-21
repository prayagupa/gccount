#!/bin/bash

configure(){
./mysqlBootstrap.sh
#./esBoostrap.sh 
 
 echo "make sure es running."
 echo
 echo "make sure you have applied es mappings."
}

runApp(){
 GRAILS_DIRECTORY="/usr/local"
 TOOL="grails"
 GRAILS_VERSION=`grep app.grails.version application.properties | cut -d'=' -f2`
 GRAILS_HOME="$GRAILS_DIRECTORY/$TOOL-$GRAILS_VERSION"
 #GRAILS_OPTS="-Xmx2G -Xms1024m -XX:MaxPermSize=1024m"
 GRAILS_OPTS="-server -noverify -Xshare:off -Xmx1G -Xms512m -XX:MaxPermSize=512m -XX:PermSize=256m -XX:+UseParallelGC"

 export GRAILS_HOME
 export GRAILS_OPTS
 #$GRAILS_HOME/bin/grails $*

 #$GRAILS_HOME/bin/grails compile
 #$GRAILS_HOME/bin/grails test-app
 #echo "TESTS SUCCESSFUL..."
 $GRAILS_HOME/bin/grails -Dserver.port=8483 run-app > gccount.log
}

configure
runApp

