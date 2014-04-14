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
          geometry: GeoScript.unwrap( reader.read( rasterEntry?.groundGeom?.text() ) ),

          width: rasterEntry?.width?.text() as Integer,
          height: rasterEntry?.height?.text() as Integer,
          numBands: rasterEntry?.numberOfBands?.text() as Integer,
          numResLevels: rasterEntry?.numberOfResLevels?.text() as Integer,
          dataType: rasterEntry?.dataType?.text(),

          acquisitionDate: parseDate( rasterEntry?.TimeStamp?.when?.text() ),
          mission: rasterEntry?.metadata?.missionId?.text(),
          sensor: rasterEntry?.metadata?.sensorId?.text(),
          fileType: rasterEntry?.metadata?.fileType?.text()
      )

      if ( !image.save() )
      {
        image.errors.allErrors.each { println messageSource.getMessage( it, null ) }
      }
    }
  }

  def parseDate(String s)
  {
    def d = null
    try
    {
      if ( s )
      {
        d = Date.parse( "yyyy-MM-dd'T'HH:mm:ss'Z'", s )
      }
    }
    catch ( e )
    {
      println e.message
    }
    return d
  }
}