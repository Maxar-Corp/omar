
import org.codehaus.groovy.grails.commons.GrailsClassUtils as GCU
import org.springframework.orm.hibernate3.SessionFactoryUtils
import org.springframework.orm.hibernate3.SessionHolder
import org.springframework.transaction.support.TransactionSynchronizationManager

includeTargets << grailsScript("Bootstrap")
includeTargets << grailsScript("Init")
includeTargets << grailsScript("Package")

grailsHome = Ant.project.properties."environment.GRAILS_HOME"

target(main: "The description of the script goes here!") {
  depends(checkVersion, configureProxy, packageApp, classpath)
  runScript()
}

setDefaultTarget(main)


target(runScript: "Main implementation that executes the specified script after starting up the application environment") {
  parseArguments()
  if ( argsMap["params"].size() == 0 )
  {
    event("StatusError", ["Required script name parameter is missing"])
    System.exit 1
  }
  compile()
  //classLoader = new URLClassLoader([classesDir.toURL()] as URL[], rootLoader)
  //Thread.currentThread().setContextClassLoader(classLoader)
  loadApp()
  configureApp()
  configureHibernateSession()
  scriptFile = argsMap["params"].getAt(0)
  // remove our script name from the params so when we call the script it's not in there
  argsMap["params"].remove(scriptFile)
  //executeScript(scriptFile, classLoader, argsMap)
  executeScript(scriptFile, grailsApp.classLoader, argsMap)
}

def configureHibernateSession()
{
  // without this you'll get a lazy initialization exception when using a many-to-many relationship
  def sessionFactory = appCtx.getBean("sessionFactory")
  def session = SessionFactoryUtils.getSession(sessionFactory, true)
  TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(session))
}

def executeScript(scriptFile, classLoader, argsMap)
{
  File script = new File(scriptFile)
  if ( script.exists() )
  {
    def shell = new GroovyShell(classLoader, new Binding(ctx: appCtx, grailsApplication: grailsApp, argsMap: argsMap, args: args))
    shell.evaluate(script.text)
  }
  else
  {
    event("StatusError", ["Designated script doesn't exist: $scriptFile"])
  }
}

//// this argument parsing target was submitted as a patch to grails and is availble in Init.groovy as of 1.0.3
//// see http://jira.codehaus.org/browse/GRAILS-2663
//
//argsMap = [params: []]
//
//target(parseArguments: "Parse the arguments passed on the command line") {
//    args?.tokenize().each {  token ->
//        def nameValueSwitch = token =~ "--?(.*)=(.*)"
//        if (nameValueSwitch.matches()) { // this token is a name/value pair (ex: --foo=bar or -z=qux)
//            argsMap[nameValueSwitch[0][1]] = nameValueSwitch[0][2]
//        } else {
//            def nameOnlySwitch = token =~ "--?(.*)"
//            if (nameOnlySwitch.matches()) {  // this token is just a switch (ex: -force or --help)
//                argsMap[nameOnlySwitch[0][1]] = true
//            } else { // single item tokens, append in order to an array of params
//                argsMap["params"] << token
//            }
//        }
//    }
//    event("StatusUpdate", ["Done parsing arguments: $argsMap"])
//}
