grails.servlet.version = "2.5" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.6
grails.project.source.level = 1.6
grails.project.dependency.resolver = "maven" // or ivy


forkConfig = [maxMemory: 1024, minMemory: 64, debug: false, maxPerm: 256]
grails.project.fork = [
        test: forkConfig, // configure settings for the test-app JVM
        run: forkConfig, // configure settings for the run-app JVM
        war: forkConfig, // configure settings for the run-war JVM
        console: forkConfig // configure settings for the Swing console JVM
]


//google.appengine.sdk = "/opt/appengine-ChocolateAlgorithm-sdk-1.7.3"
//grails.project.war.file = "target/${appName}-${appVersion}.war"

/**
  * @author   : Prayag Upd
  * @created  : Nov, 2012
  */
def props = new Properties()
new File("grails-app/conf/config.prop").withReader{
    props.load(it)
}
def slurp = new ConfigSlurper().parse(props)

grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // specify dependency exclusions here; for example, uncomment this to disable ehcache:
        // excludes 'ehcache'
    }
    log "error" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    checksums true // Whether to verify checksums on resolve

    repositories {
        inherits true // Whether to inherit repository definitions from plugins

        grailsPlugins()
        grailsHome()
        grailsCentral()

        mavenLocal()
        mavenCentral()

	//neo4j
	//mavenRepo "http://repo.grails.org/grails/repo/"
        // uncomment these (or add new ones) to enable remote dependency resolution from public Maven repositories
        //mavenRepo "http://snapshots.repository.codehaus.org"
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.ChocolateAlgorithm.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"

        mavenRepo "http://repo.typesafe.com/typesafe/releases/"
        mavenRepo "http://repository.cloudera.com/content/repositories/releases/"
        mavenRepo " https://repository.cloudera.com/artifactory/cloudera-repos/"

    }
    dependencies {
        def scalaVersion = '2.9.1'
        def elasticsearchVersion = '0.90.3'

        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.
	    runtime "org.elasticsearch:elasticsearch:$elasticsearchVersion"
        //runtime 'org.elasticsearch:elasticsearch-lang-groovy:1.5.0'
		//mysql_dependency:off
        runtime 'mysql:mysql-connector-java:5.1.20'
        compile "net.sf.ehcache:ehcache-core:2.4.6"

        //compile 'org.clojure:clojure-contrib:1.2.0'

        compile "org.scala-lang:scala-compiler:$scalaVersion",
                "org.scala-lang:scala-library:$scalaVersion"

    }

    plugins {

        compile ":hibernate:$slurp.app.hibernate.version"
        build   ":tomcat:$slurp.app.tomcat.version"
        compile ':scaffolding:1.0.0'

        runtime ":jquery:1.8.0"
        runtime ":resources:1.2.1"

        // Uncomment these (or add new ones) to enable additional resources capabilities
        //runtime ":zipped-resources:1.0"
        //runtime ":cached-resources:1.0"
        //runtime ":yui-minify-resources:0.1.4"


        runtime ":database-migration:1.3.6"

        compile ':cache:1.0.0'
	
        // install twitter-bootstrap resources plugin
        // https://github.com/iPrayag/twitter-bootstrap-scaffolding
        runtime ":twitter-bootstrap:2.1.1"

        // install Fields plugin
        runtime ":fields:1.3"

        //compile ":mongodb:1.0.0.GA"

        //Test Code Coverage Plugin
        test ":code-coverage:1.2.5"

        //
        //compile ":neo4j:1.0.0.M5"
        //
        //compile ":neo4j:latest.version"

        //spring sec plugin
        compile ':spring-security-core:1.2.7.3'

        compile ":joda-time:1.4"

        compile ":scala:0.9.2"

        //compile ":clojure:0.6"
    }
}
