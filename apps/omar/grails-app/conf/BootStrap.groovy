import org.springframework.context.ApplicationContext
import org.codehaus.groovy.grails.commons.GrailsApplication
import grails.util.GrailsUtil

//import geodata.City
//import geodata.CityData

class BootStrap
{
  def grailsApplication


  def init = {servletContext ->

    joms.oms.Init.instance().initialize()

    if ( GrailsUtil.isDevelopmentEnv() )
    {
      def shell = new GroovyShell((ClassLoader)grailsApplication.classLoader,
              new Binding(ctx: (ApplicationContext)grailsApplication.mainContext,
                      grailsApplication: (GrailsApplication)grailsApplication))

      shell?.run("./scripts/defaults.groovy" as File, [])


//      if ( City.count() == 0 )
//      {
//        CityData.load()
//      }

    }
  }

  def destroy = {
  }
}
