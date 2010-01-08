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
      def bounds = params.BBOX?.split(',')
      params.aoiMinLon = bounds[0]
      params.aoiMinLat = bounds[1]
      params.aoiMaxLon = bounds[2]
      params.aoiMaxLat = bounds[3]
    }

    if ( params.max == null || Integer.parseInt(params.max) > 100 )
    {
      params.max = 10
    }

    def queryParams = new RasterEntryQuery()

    bindData(queryParams, params)

    if ( !params.containsKey("dateSort") || params?.dateSort == "true" )
    {
      params.order = 'desc'
      params.sort = 'acquisitionDate'
      queryParams.endDate = new Date()
    }
    //println params
    log.info(queryParams.toMap())

//    println "kml  queryParams: ${queryParams.toMap()}"
//    println "kml  params: ${params}"

    def rasterEntries = rasterEntrySearchService.runQuery(queryParams, params)
    String kmlText = kmlService.createKml(rasterEntries)

    response.setHeader("Content-disposition", "attachment; filename=topImages.kml");
    render(contentType: "application/vnd.google-earth.kml+xml", text: kmlText, encoding: "UTF-8")


  }

  def getImagesKml = {
    def wmsParams = [:]

    // Convert param names to lower case
    params?.each { wmsParams?.put(it.key.toLowerCase(), it.value)}

    if ( wmsParams?.bbox )
    {
      def bounds = wmsParams.bbox?.split(',')
      params?.aoiMinLon = bounds[0]
      params?.aoiMinLat = bounds[1]
      params?.aoiMaxLon = bounds[2]
      params?.aoiMaxLat = bounds[3]
    }
    if ( params?.max == null || Integer.parseInt(params.max) > 100 )
    {
      params?.max = 10
    }

    def queryParams = new RasterEntryQuery()

    bindData(queryParams, params)

    if ( !params?.containsKey("dateSort") || params?.dateSort == "true" )
    {
      params.order = 'desc'
      params.sort = 'acquisitionDate'
      queryParams.endDate = new Date()
    }
//    println params
    log.info(queryParams.toMap())

    def rasterEntries = rasterEntrySearchService.runQuery(queryParams, params)
    String kmlText = kmlService.createImagesKml(rasterEntries, wmsParams, params)

    response.setHeader("Content-disposition", "attachment; filename=topImages.kml");
    render(contentType: "application/vnd.google-earth.kml+xml", text: kmlText, encoding: "UTF-8")
  }


  def getVideosKml = {
    def wmsParams = [:]

    // Convert param names to lower case
    params?.each { wmsParams?.put(it.key.toLowerCase(), it.value)}

    if ( wmsParams?.bbox )
    {
      def bounds = wmsParams.bbox?.split(',')
      params?.aoiMinLon = bounds[0]
      params?.aoiMinLat = bounds[1]
      params?.aoiMaxLon = bounds[2]
      params?.aoiMaxLat = bounds[3]
    }
    if ( params?.max == null || Integer.parseInt(params.max) > 100 )
    {
      params?.max = 10
    }

    def queryParams = new VideoDataSetQuery()

    bindData(queryParams, params)

    if ( !params?.containsKey("dateSort") || params?.dateSort == "true" )
    {
      params.order = 'desc'
      params.sort = 'startDate'
      queryParams.endDate = new Date()
    }
    // println params
    log.info(queryParams.toMap())

    def videoEntries = videoDataSetSearchService.runQuery(queryParams, params)
    String kmlText = kmlService.createVideosKml(videoEntries)

    response.setHeader("Content-disposition", "attachment; filename=topImages.kml");
    render(contentType: "application/vnd.google-earth.kml+xml", text: kmlText, encoding: "UTF-8")
  }

  def topImages = {
    String kmlText = kmlService.createTopImagesKml()

    response.setHeader("Content-disposition", "attachment; filename=topImages.kml");
    render(contentType: "application/vnd.google-earth.kml+xml", text: kmlText, encoding: "UTF-8")
  }

  def topVideos = {
    String kmlText = kmlService.createTopVideosKml()

    response.setHeader("Content-disposition", "attachment; filename=topVideos.kml");
    render(contentType: "application/vnd.google-earth.kml+xml", text: kmlText, encoding: "UTF-8")
  }

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
  public void afterPropertiesSet()
  {
    flashDirRoot = grailsApplication.config.videoStreaming.flashDirRoot
    flashUrlRoot = grailsApplication.config.videoStreaming.flashUrlRoot
  }
}