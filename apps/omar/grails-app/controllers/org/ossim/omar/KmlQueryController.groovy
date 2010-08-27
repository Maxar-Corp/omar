package org.ossim.omar

import org.springframework.beans.factory.InitializingBean

class KmlQueryController implements InitializingBean
{
  def grailsApplication
  def rasterEntrySearchService
  def videoDataSetSearchService
  def flashDirRoot
  def flashUrlRoot
  def kmlService

  def getkml = {

    // Google sends the BBOX with the request
    def wmsParams = [:]

    // Convert param names to lower case
    params?.each { wmsParams?.put(it.key.toLowerCase(), it.value)}

    if ( wmsParams?.bbox )
    {
      def bounds = params.bbox?.split(',')
      if ( bounds.size() >= 4 )
      {
        params.aoiMinLon = bounds[0]
        params.aoiMinLat = bounds[1]
        params.aoiMaxLon = bounds[2]
        params.aoiMaxLat = bounds[3]
      }
    }

    if ( params.max == null || Integer.parseInt(params.max) > 100 )
    {
      params.max = 10
    }

    def queryParams = new RasterEntryQuery()

    bindData(queryParams, params)

    queryParams.startDate = DateUtil.initializeDate("startDate", params)
    queryParams.endDate = DateUtil.initializeDate("endDate", params)

    if ( !params.containsKey("dateSort") || params?.dateSort == "true" )
    {
      params.order = 'desc'
      params.sort = 'acquisitionDate'
      if ( !queryParams.endDate )
      {
        queryParams.endDate = new Date()
      }
    }
    //println params
    log.info(queryParams.toMap())

//    println "kml  queryParams: ${queryParams.toMap()}"
//    println "kml  params: ${params}"
    String kmlText = ""
    def rasterEntries = rasterEntrySearchService.runQuery(queryParams, params)
    if ( !rasterEntries.empty )
    {
      kmlText = kmlService.createKml(rasterEntries, wmsParams);
    }

    response.setHeader("Content-disposition", "attachment; filename=topImages.kml");
    render(contentType: "application/vnd.google-earth.kml+xml", text: kmlText, encoding: "UTF-8")
  }

  def getImagesKml = {
    def wmsParams = [:]
    def maxImages = grailsApplication.config.kml.maxImages
    // Convert param names to lower case
    Utility.removeEmptyParams(params)
    params?.each { wmsParams?.put(it.key.toLowerCase(), it.value)}
    wmsParams = Utility.keepOnlyParams(wmsParams,
            ["stretch_mode",
                    "stretch_mode_region",
                    "sharpen_mode",
                    "quicklook",
                    "null_flip",
                    "bands"])
    wmsParams.remove("elevation")
    wmsParams.remove("time")

    if ( wmsParams?.bbox )
    {
      def bounds = wmsParams.bbox?.split(',')
      params?.aoiMinLon = bounds[0]
      params?.aoiMinLat = bounds[1]
      params?.aoiMaxLon = bounds[2]
      params?.aoiMaxLat = bounds[3]
    }
    if ( params?.max == null || Integer.parseInt(params.max) > maxImages )
    {
      params?.max = maxImages
    }

    def queryParams = new RasterEntryQuery()

    bindData(queryParams, params)
    queryParams.startDate = DateUtil.initializeDate("startDate", params)
    queryParams.endDate = DateUtil.initializeDate("endDate", params)

    if ( !params?.containsKey("dateSort") || params?.dateSort == "true" )
    {
      params.order = 'desc'
      params.sort = 'acquisitionDate'
      if ( !queryParams.endDate )
      {
        queryParams.endDate = new Date()
      }
    }
    log.info(queryParams.toMap())

    def rasterEntries = rasterEntrySearchService.runQuery(queryParams, params)
    String kmlText = kmlService.createImagesKml(rasterEntries, wmsParams, params)

    response.setHeader("Content-disposition", "attachment; filename=omar_last_${params.max}_images.kml");
    render(contentType: "application/vnd.google-earth.kml+xml", text: kmlText, encoding: "UTF-8")
  }


  def getVideosKml = {
    def wmsParams = [:]
    def maxVideos = grailsApplication.config.kml.maxVideos
    // Convert param names to lower case
    params?.each { wmsParams?.put(it.key.toLowerCase(), it.value)}

    //Utility.removeEmptyParams(params)

    if ( wmsParams?.bbox )
    {
      def bounds = wmsParams.bbox?.split(',')
      params?.aoiMinLon = bounds[0]
      params?.aoiMinLat = bounds[1]
      params?.aoiMaxLon = bounds[2]
      params?.aoiMaxLat = bounds[3]
    }
    if ( params?.max == null || Integer.parseInt(params.max) > maxVideos )
    {
      params?.max = maxVideos
    }
    if ( params?.googleClientVersion )
    {
      if ( (params?.googleClientVersion[0] as int) > 4 )
      {
        params.embed = true
      }
      else
      {
        params.embed = false
      }
    }
    def queryParams = new VideoDataSetQuery()

    bindData(queryParams, params, ['startDate', 'endDate'])
    queryParams.startDate = DateUtil.initializeDate("startDate", params)
    queryParams.endDate = DateUtil.initializeDate("endDate", params)

    if ( !params?.containsKey("dateSort") || params?.dateSort == "true" )
    {
      params.order = 'desc'
      params.sort = 'startDate'
      if ( !queryParams.endDate )
      {
        queryParams.endDate = new Date()
      }

    }
    // println params
    log.info(queryParams.toMap())
    def videoEntries = videoDataSetSearchService.runQuery(queryParams, params)
    String kmlText = kmlService.createVideosKml(videoEntries, params)

    response.setHeader("Content-disposition", "attachment; filename=omar_last_${params.max}_videos.kml");
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

/*
  def topImages = {
    String kmlText = kmlService.createTopImagesKml()

    response.setHeader("Content-disposition", "attachment; filename=topImages.kml");
    render(contentType: "application/vnd.google-earth.kml+xml", text: kmlText, encoding: "UTF-8")
  }
*/

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

/*
  def topVideos = {
    String kmlText = kmlService.createTopVideosKml()

    response.setHeader("Content-disposition", "attachment; filename=topVideos.kml");
    render(contentType: "application/vnd.google-earth.kml+xml", text: kmlText, encoding: "UTF-8")
  }
*/
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

  /*
  def imageFootprints= {
    String kmlText = kmlService.createImageFootprint()

    response.setHeader("Content-disposition", "attachment; filename=ImageFootprints.kml");
    render(contentType: "application/vnd.google-earth.kml+xml", text: kmlText, encoding: "UTF-8")
    
  }
  def videoFootprints={
    String kmlText = kmlService.createVideoFootprint()

    response.setHeader("Content-disposition", "attachment; filename=VideoFootprints.kml");
    render(contentType: "application/vnd.google-earth.kml+xml", text: kmlText, encoding: "UTF-8")
  }
  */

  public void afterPropertiesSet()
  {
    flashDirRoot = grailsApplication.config.videoStreaming.flashDirRoot
    flashUrlRoot = grailsApplication.config.videoStreaming.flashUrlRoot
  }
}
