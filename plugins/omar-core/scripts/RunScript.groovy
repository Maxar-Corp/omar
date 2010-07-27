import org.codehaus.groovy.grails.commons.GrailsClassUtils as GCU
import org.springframework.orm.hibernate3.SessionFactoryUtils
import org.springframework.orm.hibernate3.SessionHolder
import org.springframework.transaction.support.TransactionSynchronizationManager

includeTargets << grailsScript("_GrailsBootstrap")
includeTargets << grailsScript("_GrailsRun")
includeTargets << grailsScript("_GrailsSettings")
includeTargets << grailsScript("_GrailsClean")

target(main: "Execute the specified script after starting up the application environment") {
  depends(checkVersion, configureProxy, packageApp, classpath)
  runScript()
}

target(runScript: "Main implementation that executes the specified script after starting up the application environment") {
  parseArguments()
  if ( argsMap["params"].size() == 0 )
  {
    event("StatusError", ["Required script name parameter is missing"])
    System.exit 1
  }
  compile()
  loadApp()
  configureApp()
  configureHibernateSession()
  argsMap["params"].each { scriptFile ->
    executeScript(scriptFile, classLoader)
  }
}

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

setDefaultTarget(main)
