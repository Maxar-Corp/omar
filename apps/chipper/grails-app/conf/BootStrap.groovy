import chipper.GeospatialImage
import com.vividsolutions.jts.geom.Geometry
import geoscript.GeoScript
import grails.converters.JSON
import groovy.json.JsonSlurper


import static groovyx.gpars.GParsPool.withPool

import joms.oms.Init

class BootStrap
{
  def grailsApplication

  def geospatialImageService

  def init = { servletContext ->
//    Init.instance().initialize(3, ['', '-T', 'ossimChipper'] as String[])
    Init.instance().initialize()

    //System.setProperty( 'com.sun.media.imageio.disableCodecLib', 'true' )

    if ( GeospatialImage.count() == 0 )
    {
      def fileList = [
          grailsApplication.config.chipper.chipImage.orthoImage,
          grailsApplication.config.chipper.panSharpen.colorImage,
          grailsApplication.config.chipper.panSharpen.panImage,
          grailsApplication.config.chipper.twoColorMulti.redImage,
          grailsApplication.config.chipper.twoColorMulti.blueImage,
          grailsApplication.config.chipper.hillShade.mapImage
      ]

//      withPool {
//        fileList.eachParallel { filename ->
      fileList.each { filename ->
        geospatialImageService.processFile( filename )
      }
//      }
    }

    JSON.registerObjectMarshaller( Geometry ) {
      def json = GeoScript.wrap( it ).geoJSON

      new JsonSlurper().parseText( json )
    }

  }


  def destroy = {
  }
}
