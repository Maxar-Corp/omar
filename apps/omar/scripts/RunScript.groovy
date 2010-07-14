import org.codehaus.groovy.grails.commons.GrailsClassUtils as GCU
import org.springframework.orm.hibernate3.SessionFactoryUtils
import org.springframework.orm.hibernate3.SessionHolder
import org.springframework.transaction.support.TransactionSynchronizationManager


includeTargets << grailsScript("Init")

grailsHome = Ant.project.properties."environment.GRAILS_HOME"

includeTargets << grailsScript("Package")
includeTargets << grailsScript("Bootstrap")

def configureHibernateSession()
{
  // without this you'll get a lazy initialization exception when using a many-to-many relationship
  def sessionFactory = appCtx.getBean("sessionFactory")
  def session = SessionFactoryUtils.getSession(sessionFactory, true)
  TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(session))
}

def executeScript(scriptFile, classLoader)
{
  File script = new File(scriptFile)
  if ( script.exists() )
  {
    def shell = new GroovyShell(classLoader, new Binding(ctx: appCtx, grailsApplication: grailsApp))
    shell.evaluate(script.text)
  }
  else
  {
    event("StatusError", ["Designated script doesn't exist: $scriptFile"])
  }
}


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
  argsMap["params"].each { scriptFile ->
    //executeScript(scriptFile, classLoader)
    executeScript(scriptFile, grailsApp.classLoader)
  }
}


target(main: "The description of the script goes here!") {
  depends(checkVersion, configureProxy, packageApp, classpath)
  runScript()

}

setDefaultTarget(main)

//
//target('default': "Execute the specified script after starting up the application environment") {
//}
//
//
//
//// this argument parsing target has actually been submitted as a patch to Init.groovy after some feedback
//// on the grails user mailing list and will hopefully be in the next release of grails.
//// Vote it up if you like it: http://jira.codehaus.org/browse/GRAILS-2663
//
//argsMap = [params: []]
//
//target(parseArguments: "Parse the arguments passed on the command line") {
//  args?.tokenize().each {  token ->
//    def nameValueSwitch = token =~ "--?(.*)=(.*)"
//    if ( nameValueSwitch.matches() )
//    { // this token is a name/value pair (ex: --foo=bar or -z=qux)
//      argsMap[nameValueSwitch[0][1]] = nameValueSwitch[0][2]
//    }
//    else
//    {
//      def nameOnlySwitch = token =~ "--?(.*)"
//      if ( nameOnlySwitch.matches() )
//      {  // this token is just a switch (ex: -force or --help)
//        argsMap[nameOnlySwitch[0][1]] = true
//      }
//      else
//      { // single item tokens, append in order to an array of params
//        argsMap["params"] << token
//      }
//    }
//  }
//  event("StatusUpdate", ["Done parsing arguments: $argsMap"])
//}
