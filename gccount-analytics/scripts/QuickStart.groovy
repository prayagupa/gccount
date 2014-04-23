import grails.util.GrailsNameUtils
import groovy.text.SimpleTemplateEngine
import org.codehaus.groovy.grails.plugins.PluginManagerHolder

includeTargets << grailsScript("_GrailsInit")
includeTargets << grailsScript('_GrailsBootstrap')

input = args.split('\n')
packageName= ''
inputClassName = ''
inputClassNameInLowercase = ''

appLocation__ = new File(getClass().protectionDomain.codeSource.location.path).parent
appLocation_ = System.properties['base.dir']
appLocation = new File(".").getCanonicalPath()
pluginName = 'gccount-analytics'
def pluginDir = appLocation

///////////////////////////////////
// grails quick-start com.gccount.es CustomerReport
///////////////////////////////////

target(createBuilderListener: "create builder and listener!") {
	configureClassName()
	//createController()
	//createService()
    createEsListener()
}

overwriteAll = false
templateDir = "$pluginDir/src/templates"
appDir = "$appLocation/grails-app"
srcDir = "$appLocation/src"
templateAttributes = [packageName: packageName,
	                  inputClassName:inputClassName,
	                  inputClassNameInLowercase:inputClassNameInLowercase]

templateEngine = new SimpleTemplateEngine()

packageToDir = { String packageName ->
	String dir = ''
	if (packageName) {
		dir = packageName.replaceAll('\\.', '/') + '/'
	}
	return dir
}

okToWrite = { String dest ->

	def file = new File(dest)
	if (overwriteAll || !file.exists()) {
		return true
	}

	String propertyName = "file.overwrite.$file.name"
	ant.input(addProperty: propertyName, message: "$dest exists, ok to overwrite?",
		validargs: 'y,n,a', defaultvalue: 'y')

	if (ant.antProject.properties."$propertyName" == 'n') {
		return false
	}

	if (ant.antProject.properties."$propertyName" == 'a') {
		overwriteAll = true
	}

	true
}

generateFile = { String templatePath, String outputPath ->
	if (!okToWrite(outputPath)) {
		return
	}

	File templateFile = new File(templatePath)
	if (!templateFile.exists()) {
		errorMessage "\nERROR: $templatePath doesn't exist"
		return
	}

	File outFile = new File(outputPath)

	// in case it's in a package, create dirs
	ant.mkdir dir: outFile.parentFile

	outFile.withWriter { writer ->
		templateEngine.createTemplate(templateFile.text).make(templateAttributes).writeTo(writer)
	}

	printMessage "generated $outFile.absolutePath"
}

printMessage = { String message -> event('StatusUpdate', [message]) }
errorMessage = { String message -> event('StatusError', [message]) }



public void configureClassName(){
	/*if(!input){
		return false
	}*/
	packageName= input[0]
	inputClassName = input[1]
	inputClassNameInLowercase = inputClassName.toString().toLowerCase();
	inputClassName = Character.toString(inputClassNameInLowercase.charAt(0)).toUpperCase()+inputClassName.substring(1);
}

public void createController(){
	try{
		String dir = packageToDir(packageName)
		generateFile "$templateDir/controller.template", "$appDir/controllers/${dir}${inputClassName}Controller.groovy"
	}catch(Exception e){
		print(e)
	}
}

public void createService(){
	try{
		String dir = packageToDir(packageName)
	generateFile "$templateDir/service.template", "$appDir/services/${dir}${inputClassName}Service.groovy"
	}catch(Exception e){
		print(e)
	}
}

public void createEsListener(){
    try{
        String dir = packageToDir(packageName)
        String destClass = "$srcDir/java/${dir}${inputClassName}ActionListener.java"
        generateFile "$templateDir/listener.tmpl", destClass
        printMessage "Listener created at ${destClass}."
    }catch(Exception e){
        print(e)
    }
}

setDefaultTarget(createBuilderListener)
