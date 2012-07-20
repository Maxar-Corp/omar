package org.ossim.omar.raster

import org.apache.commons.collections.map.CaseInsensitiveMap
import org.ossim.omar.core.DateUtil
import org.ossim.omar.ogc.KmlQueryController

class RasterKmlQueryController extends KmlQueryController
{
  def rasterKmlService

  def rasterEntrySearchService

  def wmsPersistParams = ["stretch_mode",
          "stretch_mode_region", "sharpen_width", "sharpen_sigma",
          "sharpen_mode", "width", "height", "format", "srs",
          "service", "version", "request", "quicklook", "bands",
          "transparent", "bgcolor", "styles", "null_flip", "bbox"]

  def getImagesKml( )
  {
    //println "getImagesKml: ${params}"

    def caseInsensitiveParams = new CaseInsensitiveMap( params )
    def wmsParams = [:]
    def kmlParams = [:]
    def maxImages = grailsApplication.config.kml.maxImages ?: 10
    def defaultImages = grailsApplication.config.kml.defaultImages ?: 10


    caseInsensitiveParams?.each { wmsParams?.put( it.key.toLowerCase(), it.value )}
    wmsParams = wmsParams.subMap( wmsPersistParams )
    wmsParams.remove( "elevation" )
    wmsParams.remove( "time" )
    kmlParams = caseInsensitiveParams.subMap( kmlPersistParams )


    def aoiSet = caseInsensitiveParams?.aoiMinLon &&
            caseInsensitiveParams?.aoiMinLat &&
            caseInsensitiveParams?.aoiMaxLon &&
            caseInsensitiveParams?.aoiMaxLat



    def bounds = null

    if ( wmsParams?.bbox && !aoiSet )
    {
      bounds = wmsParams.bbox?.split( ',' )

      if ( bounds.size() == 4 )
      {
        caseInsensitiveParams.with {
          aoiMinLon = bounds[0]
          aoiMinLat = bounds[1]
          aoiMaxLon = bounds[2]
          aoiMaxLat = bounds[3]

//          println "Setting AOI: ${aoiMinLon} ${aoiMinLat} ${aoiMaxLon} ${aoiMaxLat}"
        }
      }

    }

    if ( caseInsensitiveParams.bboxToRadius == "true" )
    {
      caseInsensitiveParams.searchMethod = "RADIUS"
      caseInsensitiveParams.centerLon = ( caseInsensitiveParams.aoiMinLon.toDouble() +
              caseInsensitiveParams.aoi.MaxLon.toDouble() ) * 0.5
      caseInsensitiveParams.centerLat = ( caseInsensitiveParams.aoiMinLat.toDouble() +
              caseInsensitiveParams.aoi.MaxLat.toDouble() ) * 0.5
      if ( !caseInsensitiveParams.aoiRadius )
      {
        caseInsensitiveParams.aoiRadius = 0.0
      }
    }

    try
    {

      if ( ( caseInsensitiveParams?.max == null ) || !( caseInsensitiveParams.max =~ /\d+/ ) )
      {
        caseInsensitiveParams?.max = defaultImages;
      }
      else if ( Integer.parseInt( params.max ) > maxImages )
      {
        caseInsensitiveParams?.max = maxImages
      }
    }
    catch ( Exception e )   // sanity check
    {
      // this is only caused by a numeric parse we will default to maxImages
      caseInsensitiveParams?.max = 10
    }
    def queryParams = new RasterEntryQuery()

    queryParams.caseInsensitiveBind( caseInsensitiveParams )
    queryParams.startDate = DateUtil.initializeDate( "startDate", caseInsensitiveParams )
    queryParams.endDate = DateUtil.initializeDate( "endDate", caseInsensitiveParams )

    if ( !caseInsensitiveParams?.containsKey( "dateSort" ) || caseInsensitiveParams?.dateSort == "true" )
    {
      caseInsensitiveParams.order = 'desc'
      caseInsensitiveParams.sort = 'acquisitionDate'
      if ( !queryParams.endDate )
      {
        queryParams.endDate = new Date()
      }
    }
    log.info( queryParams.toMap() )

    def rasterEntries = rasterEntrySearchService.runQuery( queryParams, caseInsensitiveParams )
    String kmlText = rasterKmlService.createImagesKml( rasterEntries, wmsParams, caseInsensitiveParams )

    response.setHeader( "Content-disposition", "attachment; filename=omar_last_${caseInsensitiveParams.max}_images.kml" );
    render( contentType: "application/vnd.google-earth.kml+xml", text: kmlText, encoding: "UTF-8" )
  }

  def imageFootprints( )
  {
    //println "imageFootprints: ${params}"

    params.days = params.imagedays
    if ( ( params.imagedays == null ) || !( params.imagedays =~ /\d+/ ) )
      params.days = grailsApplication.config.kml.daysCoverage
    params.remove( "imagedays" )

    String kmlText = rasterKmlService.createImageFootprint( params )
    response.setHeader( "Content-disposition", "attachment; filename=omar_last_${params.days}_days_imagery_coverage.kml" );
    render( contentType: "application/vnd.google-earth.kml+xml", text: kmlText, encoding: "UTF-8" )
  }

  def topImages( )
  {
    //println "topImages: ${params}"

    if ( !( params.maximages =~ /\d+/ ) )
    {
      params.max = grailsApplication.config.kml.defaultImages
    }
    else
    {
      params.max = params.maximages.trim().toInteger()
    }

    if ( params.max > grailsApplication.config.kml.maxImages )
    {
      params.max = grailsApplication.config.kml.maxImages
    }

    params.remove( "maximages" )
    params.stretch_mode_region = "viewport"

    String kmlText = rasterKmlService.createTopImagesKml( params )

    response.setHeader( "Content-disposition", "attachment; filename=omar_last_${params.max}_images_for_view.kml" );
    render( contentType: "application/vnd.google-earth.kml+xml", text: kmlText, encoding: "UTF-8" )
  }

}
