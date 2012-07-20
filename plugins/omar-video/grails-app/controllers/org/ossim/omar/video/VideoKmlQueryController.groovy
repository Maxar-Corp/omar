package org.ossim.omar.video

import org.apache.commons.collections.map.CaseInsensitiveMap
import org.springframework.beans.factory.InitializingBean
import org.ossim.omar.ogc.KmlQueryController

class VideoKmlQueryController extends KmlQueryController implements InitializingBean
{
  def videoKmlService
  def videoDataSetSearchService
  def flashDirRoot
  def flashUrlRoot


  def getVideosKml( )
  {
    def caseInsensitiveParams = new CaseInsensitiveMap( params )
    def maxVideos = grailsApplication.config.kml.maxVideos
    def defaultVideos = grailsApplication.config.kml.defaultVideos
    // Convert param names to lower case

    //Utility.removeEmptyParams(params)
    def aoiSet = caseInsensitiveParams?.aoiMinLon &&
            caseInsensitiveParams?.aoiMinLat &&
            caseInsensitiveParams?.aoiMaxLon &&
            caseInsensitiveParams?.aoiMaxLat
    if ( caseInsensitiveParams?.bbox && !aoiSet )
    {
      def bounds = caseInsensitiveParams.bbox?.split( ',' )
      if ( bounds.size() == 4 )
      {
        caseInsensitiveParams?.aoiMinLon = bounds[0]
        caseInsensitiveParams?.aoiMinLat = bounds[1]
        caseInsensitiveParams?.aoiMaxLon = bounds[2]
        caseInsensitiveParams?.aoiMaxLat = bounds[3]
      }
    }
    try
    {
      if ( ( caseInsensitiveParams?.max == null ) || !( caseInsensitiveParams.max =~ /\d+/ ) )
      {
        caseInsensitiveParams?.max = defaultVideos;
      }
      else if ( Integer.parseInt( params.max ) > maxVideos )
      {
        caseInsensitiveParams?.max = maxVideos
      }
    }
    catch ( Exception e )   // sanity check
    {
      // this is only caused by a numeric parse we will default to maxImages
      caseInsensitiveParams?.max = maxVideos
    }
    if ( caseInsensitiveParams?.googleClientVersion )
    {
      if ( ( caseInsensitiveParams?.googleClientVersion[0] as int ) > 4 )
      {
        params.embed = true
      }
      else
      {
        params.embed = false
      }
    }
    def queryParams = new VideoDataSetQuery()

    queryParams.caseInsensitiveBind( caseInsensitiveParams )
    //bindData(queryParams, caseInsensitiveParams, ['startDate', 'endDate'])
    //queryParams.startDate = DateUtil.initializeDate("startDate", caseInsensitiveParams)
    //queryParams.endDate = DateUtil.initializeDate("endDate", caseInsensitiveParams)

    if ( !caseInsensitiveParams?.containsKey( "dateSort" ) ||
            caseInsensitiveParams?.dateSort == "true" )
    {
      caseInsensitiveParams.order = 'desc'
      caseInsensitiveParams.sort = 'startDate'
      if ( !queryParams.endDate )
      {
        queryParams.endDate = new Date()
      }

    }
    // println params
    log.info( queryParams.toMap() )
    def videoEntries = videoDataSetSearchService.runQuery( queryParams, caseInsensitiveParams )
    String kmlText = videoKmlService.createVideosKml( videoEntries, caseInsensitiveParams )

    response.setHeader( "Content-disposition", "attachment; filename=omar_last_${caseInsensitiveParams.max}_videos.kml" );
    render( contentType: "application/vnd.google-earth.kml+xml", text: kmlText, encoding: "UTF-8" )
  }

  def topVideos( )
  {
    if ( !( params.maxvideos =~ /\d+/ ) )
      params.max = grailsApplication.config.kml.defaultVideos
    else
      params.max = params.maxvideos.trim().toInteger()
    if ( params.max > grailsApplication.config.kml.maxVideos )
      params.max = grailsApplication.config.kml.maxVideos
    params.remove( "maxvideos" )

    String kmlText = videoKmlService.createTopVideosKml( params )
    response.setHeader( "Content-disposition", "attachment; filename=omar_last_${params.max}_videos_for_view.kml" );
    render( contentType: "application/vnd.google-earth.kml+xml", text: kmlText, encoding: "UTF-8" )
  }


  def videoFootprints( )
  {
    params.days = params.videodays
    if ( ( params.videodays == null ) || !( params.videodays =~ /\d+/ ) )
      params.days = grailsApplication.config.kml.daysCoverage
    params.remove( "videodays" )
    String kmlText = videoKmlService.createVideoFootprint( params )
    response.setHeader( "Content-disposition", "attachment; filename=omar_last_${params.days}_days_video_coverage.kml" );
    render( contentType: "application/vnd.google-earth.kml+xml", text: kmlText, encoding: "UTF-8" )
  }

  public void afterPropertiesSet( )
  {
    flashDirRoot = grailsApplication.config.videoStreaming.flashDirRoot
    flashUrlRoot = grailsApplication.config.videoStreaming.flashUrlRoot
  }
}
