import org.springframework.context.ApplicationContext
import org.codehaus.groovy.grails.commons.GrailsApplication
import grails.util.GrailsUtil

class BootStrap
{

  def authenticateService
  def grailsApplication


  def init = {servletContext ->

    joms.oms.Init.instance().initialize()

    if ( GrailsUtil.isDevelopmentEnv() )
    {
      def shell = new GroovyShell((ClassLoader) grailsApplication.classLoader,
              new Binding(ctx: (ApplicationContext) grailsApplication.mainContext,
                      grailsApplication: (GrailsApplication) grailsApplication))

      shell?.run("./scripts/defaults.groovy" as File, [])
    }
  }

  def destroy = {
  }
}
