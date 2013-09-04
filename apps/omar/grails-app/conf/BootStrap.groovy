import groovy.sql.Sql
import org.springframework.context.ApplicationContext
import org.codehaus.groovy.grails.commons.GrailsApplication
import grails.util.GrailsUtil

//import geodata.City
//import geodata.CityData

class BootStrap
{
  def grailsApplication
  def dataSourceUnproxied

  def init = { servletContext ->

    joms.oms.Init.instance().initialize()

    def sql = new Sql( dataSourceUnproxied )

    if ( GrailsUtil.isDevelopmentEnv() )
    {
      def shell = new GroovyShell( (ClassLoader)grailsApplication.classLoader,
          new Binding( ctx: (ApplicationContext)grailsApplication.mainContext,
              grailsApplication: (GrailsApplication)grailsApplication ) )

      shell?.run( "./scripts/defaults.groovy" as File, [] )

//      if ( City.count() == 0 )
//      {
//        CityData.load()
//      }

    }

    sql.executeUpdate( 'drop view if exists cswview' )
    sql.executeUpdate( grailsApplication.config.csw.sql )
    sql.close()
  }

  def destroy = {
  }
}
