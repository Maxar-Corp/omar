package chipper

import geoscript.GeoScript
import geoscript.geom.io.WktReader

import geoscript.GeoScript
import geoscript.geom.io.WktReader

class GeospatialImageService
{

  def messageSource
  def dataInfoService

  def processFile(def filename)
  {

    def info = dataInfoService.getInfo( filename )
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