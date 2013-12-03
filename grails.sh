GRAILS_OPTS="-Xmx2G -Xms1024m -XX:MaxPermSize=1024m"
export GRAILS_OPTS

grails test-app

echo "TESTS SUCCESSFUL..."

grails -Dserver.port=8443 run-app
