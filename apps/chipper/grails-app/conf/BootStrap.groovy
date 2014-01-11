import chipper.GeospatialImage

import static groovyx.gpars.GParsPool.withPool

import joms.oms.Init

class BootStrap
{
  def grailsApplication

  def geospatialImageService

  def init = { servletContext ->
//    Init.instance().initialize(3, ['', '-T', 'ossimChipper'] as String[])
    Init.instance().initialize()

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
  }


  def destroy = {
  }
}
