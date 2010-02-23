import org.springframework.beans.factory.InitializingBean
import org.grails.plugins.springsecurity.service.AuthenticateService


class RasterSearchController implements InitializingBean
{
  def grailsApplication
  def baseWMS
  def dataWMS

  def authenticateService
  def rasterEntrySearchService

  public static final List tagHeaderList = [
      "File Type",
      "Class Name",
      "Mission",
      "Country",
      "Target Id",
      "Sensor",
      "Image Id"
  ]

  public static final List tagNameList = [
      "file_type",
      "class_name",
      "mission",
      "country",
      "targetid",
      "sensor",
      "imageid"
  ]

  static defaultAction = 'search'

  def search = {
    def queryParams = new RasterEntryQuery()

    bindData(queryParams, params)

    if ( request.method == 'POST' )
    {
      if ( !params.max )
      {
        params.max = 10
      }

      def user = authenticateService.principal().username
      def starttime = System.currentTimeMillis()

      def rasterEntries = rasterEntrySearchService.runQuery(queryParams, params)

// This was the old way
//        def metadataTags = null
//
//        if ( rasterEntries )
//        {
//          metadataTags = MetadataTag.createCriteria().list {
//            inList("rasterEntry", rasterEntries)
//          }
//        }
//
//        def tags = metadataTags?.groupBy { it.rasterEntry }

      def endtime = System.currentTimeMillis()

      def logData = [
          TYPE: "raster_search",
          START: new Date(starttime),
          END: new Date(endtime),
          ELAPSE_TIME_MILLIS: endtime - starttime,
          USER: user,
          PARAMS: params
      ]

      log.info(logData)
      println logData

      chain(action: "results", model: [rasterEntries: rasterEntries /*, tags: tags*/], params: params)
    }
    else
    {
      println queryParams?.toMap()

      return [queryParams: queryParams, baseWMS: baseWMS, dataWMS: dataWMS]
    }
  }

  def results = {
//    println params

    def starttime = System.currentTimeMillis()

    if ( !params.max )
    {
      params.max = 10
    }

    def rasterEntries = null
    def tags = null
    def queryParams = new RasterEntryQuery()

    bindData(queryParams, params)

    if ( chainModel )
    {
      rasterEntries = chainModel.rasterEntries
      tags = chainModel.tags
    }
    else
    {

      rasterEntries = rasterEntrySearchService.runQuery(queryParams, params)

// This was the old way
//      def metadataTags = MetadataTag.createCriteria().list {
//        inList("rasterEntry", rasterEntries)
//      }
//
//      tags = metadataTags?.groupBy { it.rasterEntry }


      def endtime = System.currentTimeMillis()
      def user = authenticateService.principal()?.username

      def logData = [
          TYPE: "raster_search",
          START: new Date(starttime),
          END: new Date(endtime),
          ELAPSE_TIME_MILLIS: endtime - starttime,
          USER: user,
          PARAMS: params
      ]

      log.info(logData)

      println logData
    }

    render(view: 'results', model: [
        rasterEntries: rasterEntries,
        //tags: tags,
        tagNameList: tagNameList,
        tagHeaderList: tagHeaderList,
        queryParams: queryParams
    ])

  }


  public void afterPropertiesSet()
  {
    baseWMS = grailsApplication.config.wms.base
    dataWMS = grailsApplication.config.wms.data.raster
  }
}
