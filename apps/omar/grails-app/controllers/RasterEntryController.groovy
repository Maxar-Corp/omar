import org.springframework.beans.factory.InitializingBean
import groovy.xml.StreamingMarkupBuilder

class RasterEntryController implements InitializingBean
{
  def grailsApplication
  def authenticateService
  def baseWMS
  def dataWMS

  def rasterEntrySearchService

  public static final List tagHeaderList
  public static final List tagNameList

  def includeCount = true

  def index = { redirect(action: list, params: params) }

  // the delete, save and update actions only accept POST requests
  def static allowedMethods = [delete: 'POST', save: 'POST', update: 'POST']

  def list = {
    if ( !params.max )
    params.max = 10

    def rasterEntryList = RasterEntry.createCriteria().list(params) {}

    if ( params.rasterDataSetId )
    {
      def rasterDataSet = RasterDataSet.get(params.rasterDataSetId)

      rasterEntryList = RasterEntry.createCriteria().list(params) {
        eq("rasterDataSet", rasterDataSet)
      }
    }
    else
    {
      rasterEntryList = RasterEntry.createCriteria().list(params) {}
    }

    [rasterEntryList: rasterEntryList]
  }

  def show = {

    def rasterEntry = RasterEntry.get(params.id)


    if ( !rasterEntry )
    {
      flash.message = "RasterEntry not found with id ${params.id}"
      redirect(action: list)
    }
    else
    { return [rasterEntry: rasterEntry] }
  }

  def delete = {
    def rasterEntry = RasterEntry.get(params.id)
    if ( rasterEntry )
    {
      rasterEntry.delete()
      flash.message = "RasterEntry ${params.id} deleted"
      redirect(action: list)
    }
    else
    {
      flash.message = "RasterEntry not found with id ${params.id}"
      redirect(action: list)
    }
  }

  def edit = {
    def rasterEntry = RasterEntry.get(params.id)

    if ( !rasterEntry )
    {
      flash.message = "RasterEntry not found with id ${params.id}"
      redirect(action: list)
    }
    else
    {
      return [rasterEntry: rasterEntry]
    }
  }

  def update = {
    def rasterEntry = RasterEntry.get(params.id)
    if ( rasterEntry )
    {
      rasterEntry.properties = params
      if ( !rasterEntry.hasErrors() && rasterEntry.save() )
      {
        flash.message = "RasterEntry ${params.id} updated"
        redirect(action: show, id: rasterEntry.id)
      }
      else
      {
        render(view: 'edit', model: [rasterEntry: rasterEntry])
      }
    }
    else
    {
      flash.message = "RasterEntry not found with id ${params.id}"
      redirect(action: edit, id: params.id)
    }
  }

  def create = {
    def rasterEntry = new RasterEntry()
    rasterEntry.properties = params
    return ['rasterEntry': rasterEntry]
  }

  def save = {
    def rasterEntry = new RasterEntry(params)
    if ( !rasterEntry.hasErrors() && rasterEntry.save() )
    {
      flash.message = "RasterEntry ${rasterEntry.id} created"
      redirect(action: show, id: rasterEntry.id)
    }
    else
    {
      render(view: 'create', model: [rasterEntry: rasterEntry])
    }
  }


  def search = {

    //println "=== search start ==="

    if ( !params.max )
    {
      params.max = 10;
    }

    //println "\nparams: ${params?.sort { it.key }}"

    def queryParams = initRasterEntryQuery(params)

    //println "\nqueryParams: ${queryParams?.toMap()?.sort { it.key } }"

    if ( request.method == 'POST' )
    {
      if ( !params.max || !(params.max =~ /\d+$/) || (params.max as Integer) > 100 )
      {
        params.max = 10
      }

      params.order = 'desc'
      params.sort = 'acquisitionDate'

      //println "queryParams: ${queryParams}"

      def user = authenticateService.principal().username
      def starttime = System.currentTimeMillis()

      def xxx = rasterEntrySearchService.runQuery(queryParams, params, includeCount)
      def rasterEntries = xxx?.rasterEntries
      def totalCount = xxx?.totalCount


      def rasterFiles = []

      if ( rasterEntries )
      {
        rasterFiles = RasterFile.createCriteria().list {
          eq("type", "main")
          inList("rasterDataSet", rasterEntries.rasterDataSet)
        }
      }

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

      //println logData

      //println "=== search end ==="

      chain(action: "results", model: [rasterEntries: rasterEntries, totalCount: totalCount, rasterFiles: rasterFiles], params: params)
    }
    else
    {
      //println "=== search end ==="

      return [queryParams: queryParams, baseWMS: baseWMS, dataWMS: dataWMS, sidebar: "rasterSearch"]
    }
  }

  def search2 = {

    //println "=== search start ==="

    if ( !params.max )
    {
      params.max = 10;
    }

    //println "\nparams: ${params?.sort { it.key }}"

    def queryParams = initRasterEntryQuery(params)

    //println "\nqueryParams: ${queryParams?.toMap()?.sort { it.key } }"

    if ( request.method == 'POST' )
    {
      if ( !params.max || !(params.max =~ /\d+$/) || (params.max as Integer) > 100 )
      {
        params.max = 10
      }

      params.order = 'desc'
      params.sort = 'acquisitionDate'

      //println "queryParams: ${queryParams}"

      def user = authenticateService.principal().username
      def starttime = System.currentTimeMillis()

      def xxx = rasterEntrySearchService.runQuery(queryParams, params, includeCount)
      def rasterEntries = xxx?.rasterEntries
      def totalCount = xxx?.totalCount

      def rasterFiles = []

      if ( rasterEntries )
      {
        rasterFiles = RasterFile.createCriteria().list {
          eq("type", "main")
          inList("rasterDataSet", rasterEntries.rasterDataSet)
        }
      }

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

      //println logData

      //println "=== search end ==="

      chain(action: "results", model: [rasterEntries: rasterEntries, totalCount: totalCount, rasterFiles: rasterFiles], params: params)
    }
    else
    {
      //println "=== search end ==="

      return [queryParams: queryParams, baseWMS: baseWMS, dataWMS: dataWMS, sidebar: "rasterSearch"]
    }
  }

  private def initRasterEntryQuery(Map params)
  {
    def queryParams = new RasterEntryQuery()

    bindData(queryParams, params)

    initializeDate(queryParams, "startDate", params)
    initializeDate(queryParams, "endDate", params)

    return queryParams
  }

  private def initializeDate(RasterEntryQuery queryParams, String dateField, Map params)
  {

    def dateFormats = [
        "MM/dd/yyyy HH:mm:ss"
    ]

    if ( queryParams."${dateField}" == null && params."${dateField}" != null &&
        params."${dateField}" != "struct" && params."${dateField}" != "" )
    {
      dateFormats.each {dateFormat ->
        try
        {
          queryParams."${dateField}" = Date.parse(dateFormat, params."${dateField}")
        }
        catch (Exception e)
        {
          println "Cannot parse ${dateField}: ${params."${dateField}"} using ${dateFormat}"
        }
      }
    }
  }

  def results = {

//    println "=== results start ==="

    def starttime = System.currentTimeMillis()

    if ( !params.max || !(params.max =~ /\d+$/) || (params.max as Integer) > 100 )
    {
      params.max = 10
    }

    def rasterEntries = null
    def totalCount = null
    def rasterFiles = null

    def queryParams = initRasterEntryQuery(params)

    if ( chainModel )
    {
      rasterEntries = chainModel.rasterEntries
      totalCount = chainModel.totalCount
      rasterFiles = chainModel.rasterFiles
    }
    else
    {
      def xxx = rasterEntrySearchService.runQuery(queryParams, params, includeCount)

      rasterEntries = xxx?.rasterEntries
      totalCount = xxx?.totalCount

      if ( rasterEntries )
      {
        rasterFiles = RasterFile.createCriteria().list {
          eq("type", "main")
          inList("rasterDataSet", rasterEntries?.rasterDataSet)
        }
      }

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

      //println "\nparams: ${params?.sort { it.key }}"
      //println "\nqueryParams: ${queryParams?.toMap()?.sort { it.key } }"

      log.info(logData)

//      println logData
    }

//    println "=== results end ==="

    render(view: 'results', model: [
        rasterEntries: rasterEntries,
        totalCount: totalCount,
        rasterFiles: rasterFiles,
        tagNameList: tagNameList,
        tagHeaderList: tagHeaderList,
        queryParams: queryParams
    ])

  }

  def getKML = {

    def rasterEntry = RasterEntry.get(params.rasterEntryIds)

    if ( !rasterEntry )
    {
      flash.message = "RasterEntry not found with id ${params.rasterEntryIds}"
      redirect(action: list)
    }
    else
    {
      def kmlFile = RasterEntryFile.findByTypeAndRasterEntry("kml", rasterEntry)

      if ( kmlFile )
      {
        def kml = new File(kmlFile?.name)?.text
        response.setHeader("Content-disposition", "attachment; filename=foo.kml")
        render(contentType: "application/vnd.google-earth.kml+xml", text: kml, encoding: "UTF-8")
      }
    }
  }

  def kmlnetworklink = {
    def kmlbuilder = new StreamingMarkupBuilder()

    kmlbuilder.encoding = "UTF-8"

    // Google Earth hates the following parameters?

//    8.times { params.remove("searchTagNames[${it}]") }
    params.remove("_action_kmlnetworklink")

    params.dateSort = "false"

    def serviceAddress = createLink(absolute: true, controller: "kmlQuery", action: "getkml", params: params)

    def kmlnode = {
      mkp.xmlDeclaration()
      kml("xmlns": "http://earth.google.com/kml/2.1") {
        Folder() {
          name("Images")
          visibility("1")
          open("1")
          description("")
          NetworkLink() {
            name("Image query")
            visibility("1")
            open("1")
            description("")
            refreshVisibility("0")
            flyToView("0")
            Link() {
              href() {
                mkp.yieldUnescaped("<![CDATA[${serviceAddress}]]>")
              }
              refreshInterval("2000")
              refreshMode("onRequest")
              refreshTime("200")
            }
          }
        }
      }
    }

    def kmlwriter = new StringWriter()

    kmlwriter << kmlbuilder.bind(kmlnode)
    response.setHeader("Content-disposition", "attachment; filename=singleRequestTopImages.kml")
    render(contentType: "application/vnd.google-earth.kml+xml", text: kmlwriter.buffer, encoding: "UTF-8")

  }

  public void afterPropertiesSet()
  {
    baseWMS = grailsApplication.config.wms.base
    dataWMS = grailsApplication.config.wms.data.raster
    tagHeaderList = grailsApplication.config.rasterEntry.metadata.tagHeaderList
    tagNameList = grailsApplication.config.rasterEntry.metadata.tagNameList
  }
}
