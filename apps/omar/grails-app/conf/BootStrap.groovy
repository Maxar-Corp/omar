import com.vividsolutions.jts.geom.Geometry
import geoscript.GeoScript
import grails.converters.JSON
import groovy.json.JsonSlurper
import groovy.sql.Sql
import org.ossim.omar.ChipFormat
import org.ossim.omar.core.Repository
import org.ossim.omar.security.Requestmap
import org.springframework.context.ApplicationContext
import org.codehaus.groovy.grails.commons.GrailsApplication
import grails.util.GrailsUtil

import javax.sql.DataSource

//import geodata.City
//import geodata.CityData

class BootStrap
{
  def grailsApplication
  def dataSourceUnproxied
  def stagerService

  def init = { servletContext ->

    joms.oms.Init.instance().initialize()

    if ( ChipFormat.count() == 0 )
    {
      def chips = [
          [label: "Large 4X3", width: 800, height: 600, comment: "Temporary"],
          [label: "PowerPoint 1", width: 976, height: 780, comment: "NGA analyst recommended"]
      ]

      chips.each {
        ChipFormat.findOrSaveWhere( it )
      }
    }

    def sql = new Sql( dataSourceUnproxied as DataSource )
//
//    if ( GrailsUtil.isDevelopmentEnv() )
//    {
//      def shell = new GroovyShell( (ClassLoader)grailsApplication.classLoader,
//          new Binding( ctx: (ApplicationContext)grailsApplication.mainContext,
//              grailsApplication: (GrailsApplication)grailsApplication ) )
//
//      shell?.run( "./scripts/defaults.groovy" as File, [] )
//
////      if ( City.count() == 0 )
////      {
////        CityData.load()
////      }
//
//    }
//
    sql.executeUpdate( "drop view if exists cswview" )
    sql.executeUpdate( grailsApplication.config.csw.sql )
    sql.close()

    new Requestmap( url: '/download/**', configAttribute: 'ROLE_DOWNLOAD' ).save()


    JSON.registerObjectMarshaller( Geometry ) {
      def json = GeoScript.wrap( it ).geoJSON

      new JsonSlurper().parseText( json )
    }

    // Just for testing...
//    def testRepo = new Repository( baseDir: '/data1' )
//
//    if ( testRepo.save() )
//    {
//      stagerService.runStager( testRepo )
//    }
  }

  def destroy = {
  }
}
