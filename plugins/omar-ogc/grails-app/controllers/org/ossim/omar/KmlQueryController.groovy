package org.ossim.omar

import org.springframework.beans.factory.InitializingBean
import org.apache.commons.collections.map.CaseInsensitiveMap

class KmlQueryController implements InitializingBean
{
  def grailsApplication
  def rasterEntrySearchService
  def videoDataSetSearchService
  def flashDirRoot
  def flashUrlRoot
  def kmlService
  def wmsPersistParams = ["stretch_mode",
          "stretch_mode_region", "sharpen_width", "sharpen_sigma",
          "sharpen_mode", "width", "height", "format", "srs",
          "service", "version", "request", "quicklook", "bands",
          "transparent", "bgcolor", "styles", "null_flip", "bbox"]
  def kmlPersistParams = ["googleversion", "visibility"]
  def getkml = {
    // let's just reuse the getImagesKml code
    //redirect(controller: "kmlQuery", action: "getImagesKml", params:params)

    println "KmlQueryController.getkml: SHOULD NEVER SEE THIS!!!!!!!!!!!!!!!!!!!!!!!!!"


    try
    {
      forward(controller: "kmlQuery", action: "getImagesKml", params: params)
    }
    catch (Exception e)
    {}


  }

  def getImagesKml = {
    //println params.sort()

    def caseInsensitiveParams = new CaseInsensitiveMap(params)
    def wmsParams = [:]
    def kmlParams = [:]
    def maxImages = grailsApplication.config.kml.maxImages ?: 10
    def defaultImages = grailsApplication.config.kml.defaultImages ?: 10


    caseInsensitiveParams?.each { wmsParams?.put(it.key.toLowerCase(), it.value)}
    wmsParams = wmsParams.subMap(wmsPersistParams)
    wmsParams.remove("elevation")
    wmsParams.remove("time")
    kmlParams = caseInsensitiveParams.subMap(kmlPersistParams)


    def aoiSet = caseInsensitiveParams?.aoiMinLon &&
            caseInsensitiveParams?.aoiMinLat &&
            caseInsensitiveParams?.aoiMaxLon &&
            caseInsensitiveParams?.aoiMaxLat



    def bounds = null

    if ( wmsParams?.bbox && !aoiSet )
    {
      bounds = wmsParams.bbox?.split(',')

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
      caseInsensitiveParams.centerLon = (caseInsensitiveParams.aoiMinLon.toDouble() +
              caseInsensitiveParams.aoi.MaxLon.toDouble()) * 0.5
      caseInsensitiveParams.centerLat = (caseInsensitiveParams.aoiMinLat.toDouble() +
              caseInsensitiveParams.aoi.MaxLat.toDouble()) * 0.5
      if ( !caseInsensitiveParams.aoiRadius )
      {
        caseInsensitiveParams.aoiRadius = 0.0
      }
    }

    try
    {

      if ( (caseInsensitiveParams?.max == null) || !(caseInsensitiveParams.max =~ /\d+/) )
      {
        caseInsensitiveParams?.max = defaultImages;
      }
      else if ( Integer.parseInt(params.max) > maxImages )
      {
        caseInsensitiveParams?.max = maxImages
      }
    }
    catch (Exception e)   // sanity check
    {
      // this is only caused by a numeric parse we will default to maxImages
      caseInsensitiveParams?.max = 10
    }
    def queryParams = new org.ossim.omar.RasterEntryQuery()

    queryParams.caseInsensitiveBind(caseInsensitiveParams)
    queryParams.startDate = DateUtil.initializeDate("startDate", caseInsensitiveParams)
    queryParams.endDate = DateUtil.initializeDate("endDate", caseInsensitiveParams)

    if ( !caseInsensitiveParams?.containsKey("dateSort") || caseInsensitiveParams?.dateSort == "true" )
    {
      caseInsensitiveParams.order = 'desc'
      caseInsensitiveParams.sort = 'acquisitionDate'
      if ( !queryParams.endDate )
      {
        queryParams.endDate = new Date()
      }
    }
    log.info(queryParams.toMap())

    def rasterEntries = rasterEntrySearchService.runQuery(queryParams, caseInsensitiveParams)
    String kmlText = kmlService.createImagesKml(rasterEntries, wmsParams, caseInsensitiveParams)

    response.setHeader("Content-disposition", "attachment; filename=omar_last_${caseInsensitiveParams.max}_images.kml");
    render(contentType: "application/vnd.google-earth.kml+xml", text: kmlText, encoding: "UTF-8")
  }


  def getVideosKml = {
    def caseInsensitiveParams = new CaseInsensitiveMap(params)
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
      def bounds = caseInsensitiveParams.bbox?.split(',')
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
      if ( (caseInsensitiveParams?.max == null) || !(caseInsensitiveParams.max =~ /\d+/) )
      {
        caseInsensitiveParams?.max = defaultVideos;
      }
      else if ( Integer.parseInt(params.max) > maxVideos )
      {
        caseInsensitiveParams?.max = maxVideos
      }
    }
    catch (Exception e)   // sanity check
    {
      // this is only caused by a numeric parse we will default to maxImages
      caseInsensitiveParams?.max = maxVideos
    }
    if ( caseInsensitiveParams?.googleClientVersion )
    {
      if ( (caseInsensitiveParams?.googleClientVersion[0] as int) > 4 )
      {
        params.embed = true
      }
      else
      {
        params.embed = false
      }
    }
    def queryParams = new org.ossim.omar.VideoDataSetQuery()

    queryParams.caseInsensitiveBind(caseInsensitiveParams)
    //bindData(queryParams, caseInsensitiveParams, ['startDate', 'endDate'])
    //queryParams.startDate = DateUtil.initializeDate("startDate", caseInsensitiveParams)
    //queryParams.endDate = DateUtil.initializeDate("endDate", caseInsensitiveParams)

    if ( !caseInsensitiveParams?.containsKey("dateSort") ||
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
    log.info(queryParams.toMap())
    def videoEntries = videoDataSetSearchService.runQuery(queryParams, caseInsensitiveParams)
    String kmlText = kmlService.createVideosKml(videoEntries, caseInsensitiveParams)

    response.setHeader("Content-disposition", "attachment; filename=omar_last_${caseInsensitiveParams.max}_videos.kml");
    render(contentType: "application/vnd.google-earth.kml+xml", text: kmlText, encoding: "UTF-8")
  }

  def topImages =
  {
    if ( !(params.maximages =~ /\d+/) )
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

    params.remove("maximages")
    params.stretch_mode_region = "viewport"

    String kmlText = kmlService.createTopImagesKml(params)

    response.setHeader("Content-disposition", "attachment; filename=omar_last_${params.max}_images_for_view.kml");
    render(contentType: "application/vnd.google-earth.kml+xml", text: kmlText, encoding: "UTF-8")
  }

  def topVideos = {
    if ( !(params.maxvideos =~ /\d+/) )
    params.max = grailsApplication.config.kml.defaultVideos
    else
      params.max = params.maxvideos.trim().toInteger()
    if ( params.max > grailsApplication.config.kml.maxVideos )
    params.max = grailsApplication.config.kml.maxVideos
    params.remove("maxvideos")

    String kmlText = kmlService.createTopVideosKml(params)
    response.setHeader("Content-disposition", "attachment; filename=omar_last_${params.max}_videos_for_view.kml");
    render(contentType: "application/vnd.google-earth.kml+xml", text: kmlText, encoding: "UTF-8")
  }

  def imageFootprints = {
    params.days = params.imagedays
    if ( (params.imagedays == null) || !(params.imagedays =~ /\d+/) )
    params.days = grailsApplication.config.kml.daysCoverage
    params.remove("imagedays")

    String kmlText = kmlService.createImageFootprint(params)
    response.setHeader("Content-disposition", "attachment; filename=omar_last_${params.days}_days_imagery_coverage.kml");
    render(contentType: "application/vnd.google-earth.kml+xml", text: kmlText, encoding: "UTF-8")
  }

  def videoFootprints = {
    params.days = params.videodays
    if ( (params.videodays == null) || !(params.videodays =~ /\d+/) )
    params.days = grailsApplication.config.kml.daysCoverage
    params.remove("videodays")
    String kmlText = kmlService.createVideoFootprint(params)
    response.setHeader("Content-disposition", "attachment; filename=omar_last_${params.days}_days_video_coverage.kml");
    render(contentType: "application/vnd.google-earth.kml+xml", text: kmlText, encoding: "UTF-8")
  }

  public void afterPropertiesSet()
  {
    flashDirRoot = grailsApplication.config.videoStreaming.flashDirRoot
    flashUrlRoot = grailsApplication.config.videoStreaming.flashUrlRoot
  }
}
