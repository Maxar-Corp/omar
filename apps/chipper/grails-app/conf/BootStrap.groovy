import chipper.GeospatialImage
import geoscript.GeoScript
import geoscript.geom.io.WktReader
import joms.oms.DataInfo
import joms.oms.Init

class BootStrap
{
  def grailsApplication
  def messageSource

  def init = { servletContext ->
//    Init.instance().initialize(3, ['', '-T', 'ossimChipper'] as String[])
    Init.instance().initialize()

    if ( GeospatialImage.count() == 0 )
    {


      [
          grailsApplication.config.chipper.chipImage.orthoImage,
          grailsApplication.config.chipper.panSharpen.colorImage,
          grailsApplication.config.chipper.panSharpen.panImage,
          grailsApplication.config.chipper.twoColorMulti.redImage,
          grailsApplication.config.chipper.twoColorMulti.blueImage,
          grailsApplication.config.chipper.hillShade.mapImage
      ].each { filename ->
        def info = DataInfo.readInfo( filename )
        def oms = new XmlSlurper().parseText( info )
        def reader = new WktReader()

        oms.dataSets.RasterDataSet.rasterEntries.RasterEntry.each { rasterEntry ->
//        println rasterEntry.entryId
//        println rasterEntry.groundGeom

          def image = new GeospatialImage(
              filename: filename,
              entry: rasterEntry?.entryId?.text(),
              geometry: GeoScript.unwrap( reader.read( rasterEntry?.groundGeom?.text() ) )
          )

          if ( !image.save() )
          {
            image.errors.allErrors.each { println messageSource.getMessage( it, null ) }
          }
        }
      }
    }
  }
  def destroy = {
  }
}
