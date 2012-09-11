package org.ossim.omar.raster

import org.springframework.beans.factory.InitializingBean
import groovy.xml.StreamingMarkupBuilder

import org.ossim.omar.core.DateUtil
import org.ossim.omar.core.WMSRequest

class RasterEntryController implements InitializingBean
{
  def grailsApplication
/*
  def authenticateService
*/
  def springSecurityService
  def baseWMS
  def dataWMS
  def webMappingService
  def rasterEntrySearchService

  def tagHeaderList
  def tagNameList

  def index( )
  { redirect( action: 'list', params: params ) }

  // the delete, save and update actions only accept POST requests
  def static allowedMethods = [delete: 'POST', save: 'POST', update: 'POST']

  /*
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

    if ( !session.rasterEntryListCurrentTab && ("${session.rasterEntryListCurrentTab}" != "0") )
    {
      session["rasterEntryListCurrentTab"] = "0"
    }

    [rasterEntryList: rasterEntryList,
            tagNameList: tagNameList,
            tagHeaderList: tagHeaderList,
            sessionAction: "updateSession",
            sessionController: "session",
            rasterEntryListCurrentTab: session.rasterEntryListCurrentTab
    ]
  }
   */

  def list( )
  {

    def starttime = System.currentTimeMillis()
    def max = null;
    if ( !params.max || !( params.max =~ /\d+$/ ) )
    {
      max = 10
      params.max = 10
    }
    else
    {
      max = params.max as Integer
      if ( max > 100 )
      {
        max = 100
        params.max = 100;
      }
    }
    if ( !session.rasterEntryResultCurrentTab && ( "${session.rasterEntryResultCurrentTab}" != "0" ) )
    {
      session["rasterEntryResultCurrentTab"] = "0"
    }

    def rasterEntries = null
    def totalCount = null
    def rasterFiles = null
 /*
    if ( params.rasterDataSetId )
    {
      def rasterDataSet = RasterDataSet.get( params.rasterDataSetId )

      rasterEntries = RasterEntry.createCriteria().list( params ) {
        eq( "rasterDataSet", rasterDataSet )
      }
    }
    else
    {
      rasterEntries = RasterEntry.createCriteria().list( params ) {}
    }
*/
    def queryParams = initRasterEntryQuery( params )
    rasterEntries = rasterEntrySearchService.runQuery( queryParams, params )

    totalCount = max > 0 ? rasterEntrySearchService.getCount( queryParams ) : 0

    if ( rasterEntries )
    {
      rasterFiles = RasterFile.createCriteria().list {
        eq( "type", "main" )
        inList( "rasterDataSet", rasterEntries?.rasterDataSet )
      }
    }

    def endtime = System.currentTimeMillis()

/*
    def user = authenticateService.principal()?.username
*/

    def user = springSecurityService.principal.username


    def logData = [
            TYPE: "raster_list",
            START: new Date( starttime ),
            END: new Date( endtime ),
            ELAPSE_TIME_MILLIS: endtime - starttime,
            USER: user,
            PARAMS: params
    ]


    log.info( logData )

    render( view: 'results', model: [
            rasterEntries: rasterEntries,
            totalCount: totalCount,
            rasterFiles: rasterFiles,
            tagNameList: tagNameList,
            tagHeaderList: tagHeaderList,
            queryParams: queryParams,
            sessionAction: "updateSession",
            sessionController: "session",
            rasterEntryResultCurrentTab: session.rasterEntryResultCurrentTab
    ] )

  }

  def list_mobile( )
  {
    if ( !params.max )
      params.max = 10

    def rasterEntryList = RasterEntry.createCriteria().list( params ) {}

    if ( params.rasterDataSetId )
    {
      def rasterDataSet = RasterDataSet.get( params.rasterDataSetId )

      rasterEntryList = RasterEntry.createCriteria().list( params ) {
        eq( "rasterDataSet", rasterDataSet )
      }
    }
    else
    {
      rasterEntryList = RasterEntry.createCriteria().list( params ) {}
    }

    [rasterEntryList: rasterEntryList]
  }

  def show( )
  {

    def rasterEntry = RasterEntry.findByIndexId( params.id ) ?: RasterEntry.get( params.id );


    if ( !rasterEntry )
    {
      flash.message = "RasterEntry not found with id ${params.id}"
      redirect( action: 'list' )
    }
    else
    { return [rasterEntry: rasterEntry] }
  }

  def delete( )
  {
    def rasterEntry = RasterEntry.findByIndexId( params.id ) ?: RasterEntry.get( params.id );
    if ( rasterEntry )
    {
      rasterEntry.delete()
      flash.message = "RasterEntry ${params.id} deleted"
      redirect( action: 'list' )
    }
    else
    {
      flash.message = "RasterEntry not found with id ${params.id}"
      redirect( action: 'list' )
    }
  }

  def edit( )
  {
    def rasterEntry = RasterEntry.findByIndexId( params.id ) ?: RasterEntry.get( params.id );

    if ( !rasterEntry )
    {
      flash.message = "RasterEntry not found with id ${params.id}"
      redirect( action: 'list' )
    }
    else
    {
      return [rasterEntry: rasterEntry]
    }
  }

  def update( )
  {
    def rasterEntry = RasterEntry.findByIndexId( params.id ) ?: RasterEntry.get( params.id );
    if ( rasterEntry )
    {
      rasterEntry.properties = params
      if ( !rasterEntry.hasErrors() && rasterEntry.save() )
      {
        flash.message = "RasterEntry ${params.id} updated"
        redirect( action: 'show', id: rasterEntry.id )
      }
      else
      {
        render( view: 'edit', model: [rasterEntry: rasterEntry] )
      }
    }
    else
    {
      flash.message = "RasterEntry not found with id ${params.id}"
      redirect( action: 'edit', id: params.id )
    }
  }

  def create( )
  {
    def rasterEntry = new RasterEntry()
    rasterEntry.properties = params
    return ['rasterEntry': rasterEntry]
  }

  def save( )
  {
    def rasterEntry = new RasterEntry( params )
    if ( !rasterEntry.hasErrors() && rasterEntry.save() )
    {
      flash.message = "RasterEntry ${rasterEntry.id} created"
      redirect( action: 'show', id: rasterEntry.id )
    }
    else
    {
      render( view: 'create', model: [rasterEntry: rasterEntry] )
    }
  }

  def search( )
  {

//    println "=== search start ==="
    def max = null;
    if ( !params.max || !( params.max =~ /\d+$/ ) )
    {
      max = 10
      params.max = 10
    }
    else
    {
      max = params.max as Integer
      if ( max > 100 )
      {
        max = 100
        params.max = 100;
      }
    }
//    println "\nparams: ${params?.sort { it.key }}"

    if ( !session.rasterEntrySearchCurrentTab1 )
    {
      session["rasterEntrySearchCurrentTab1"] = "1"
    }

    if ( !session.rasterEntrySearchCurrentTab2 )
    {
      session["rasterEntrySearchCurrentTab2"] = "0"
    }


    def queryParams = initRasterEntryQuery( params )

//    println "\nqueryParams: ${queryParams?.toMap()?.sort { it.key } }"

    def searchLabelList = []
    def searchNameList = []
    def searchTags = RasterEntrySearchTag.list();
    searchTags?.each {searchTag ->
      searchLabelList << searchTag.description
      searchNameList << searchTag.name
    }
//    println labelList
//    println nameList
    if ( request.method == 'POST' )
    {
      if ( !params.max || !( params.max =~ /\d+$/ ) || ( params.max as Integer ) > 100 )
      {
        params.max = 10
      }

      params.order = 'desc'
      params.sort = 'acquisitionDate'

      //println "queryParams: ${queryParams}"

/*
      def user = authenticateService.principal().username
*/
      def user = springSecurityService.principal.username

      def starttime = System.currentTimeMillis()

      def rasterEntries = rasterEntrySearchService.runQuery( queryParams, params )
      def totalCount = max > 0 ? rasterEntrySearchService.getCount( queryParams ) : 0


      def rasterFiles = []

      if ( rasterEntries )
      {
        rasterFiles = RasterFile.createCriteria().list {
          eq( "type", "main" )
          inList( "rasterDataSet", rasterEntries.rasterDataSet )
        }
      }

      def endtime = System.currentTimeMillis()

      def logData = [
              TYPE: "raster_search",
              START: new Date( starttime ),
              END: new Date( endtime ),
              ELAPSE_TIME_MILLIS: endtime - starttime,
              USER: user,
              PARAMS: params
      ]

      log.info( logData )

      //println logData

//      def ogcFilterQueryFields =  Utility.generateMapForOgcFilterQuery(grailsApplication.getArtefact("Domain",
//                                                                        org.ossim.omar.raster.RasterEntry.name),
//                                                                        searchNameList,
//                                                                        null,
//                                                                        null)
//      chain(action: "results", model: [ogcFilterQueryFields:ogcFilterQueryFields, rasterEntries: rasterEntries, totalCount: totalCount, rasterFiles: rasterFiles], params: params)
      chain( action: "results", model: [session: session, rasterEntries: rasterEntries, totalCount: totalCount, rasterFiles: rasterFiles], params: params )
    }
    else
    {
//      def ogcFilterQueryFields =  Utility.generateMapForOgcFilterQuery(grailsApplication.getArtefact("Domain",
//                                                                        org.ossim.omar.raster.RasterEntry.name),
//                                                                        searchNameList,
      //                                                                       null,
      //                                                                      null)

//      return [ogcFilterQueryFields:ogcFilterQueryFields, queryParams: queryParams, baseWMS: baseWMS, dataWMS: dataWMS]
      return [
         action: "results",
          session: session,
          queryParams: queryParams,
          baseWMS: baseWMS,
          dataWMS: dataWMS,
          sessionAction: "updateSession",
          sessionController: "session",
          rasterEntrySearchCurrentTab1: session.rasterEntrySearchCurrentTab1,
          rasterEntrySearchCurrentTab2: session.rasterEntrySearchCurrentTab2,
          footprintStyle: applicationContext.getBean(dataWMS?.options?.styles)
      ]
    }
  }

  def search_mobile( )
  {

//    println "=== search start ==="

    if ( !params.max || !( params.max =~ /\d+$/ ) )
    {
      params.max = 10
    }
    else if ( ( params.max as Integer ) > 100 )
    {
      params.max = 100;
    }

//    println "\nparams: ${params?.sort { it.key }}"

    def queryParams = initRasterEntryQuery( params )

//    println "\nqueryParams: ${queryParams?.toMap()?.sort { it.key } }"

    if ( request.method == 'POST' )
    {
      params.order = 'desc'
      params.sort = 'acquisitionDate'

      //println "queryParams: ${queryParams}"
/*
      def user = authenticateService.principal().username
*/
      def user = springSecurityService.principal.username

      def starttime = System.currentTimeMillis()

      def rasterEntries = rasterEntrySearchService.runQuery( queryParams, params )
      def totalCount = rasterEntrySearchService.getCount( queryParams )


      def rasterFiles = []

      if ( rasterEntries )
      {
        rasterFiles = RasterFile.createCriteria().list {
          eq( "type", "main" )
          inList( "rasterDataSet", rasterEntries.rasterDataSet )
        }
      }

      def endtime = System.currentTimeMillis()

      def logData = [
              TYPE: "raster_search",
              START: new Date( starttime ),
              END: new Date( endtime ),
              ELAPSE_TIME_MILLIS: endtime - starttime,
              USER: user,
              PARAMS: params
      ]

      log.info( logData )

      //println logData

//      println "=== search end ==="

      chain( action: "results_mobile", model: [rasterEntries: rasterEntries, totalCount: totalCount, rasterFiles: rasterFiles], params: params )
    }
    else
    {
//      println "=== search end ==="

      return [queryParams: queryParams, baseWMS: baseWMS, dataWMS: dataWMS]
    }
  }

  private def initRasterEntryQuery( Map params )
  {
    def queryParams = new RasterEntryQuery()

    bindData( queryParams, params )

    queryParams.startDate = DateUtil.initializeDate( "startDate", params )
    queryParams.endDate = DateUtil.initializeDate( "endDate", params )

//    println "params: ${params}"
//    println "startDate: ${queryParams.startDate}"
//    println "endDate: ${queryParams.endDate}"


    return queryParams
  }

  def results( )
  {

    def starttime = System.currentTimeMillis()

    if ( !params.max || !( params.max =~ /\d+$/ ) )
    {
      params.max = 10
    }
    else
    {
      def max = params.max as Integer
      if ( max > 100 )
      {
        params.max = 100;
      }
    }

    if ( params?.queryParams )
    {
      def serialized = params?.queryParams - "{" - "}";
      def paramsArray = serialized?.split( ',' )
      params.remove( "queryParams" )
      params.remove( "totalCount" )

      paramsArray?.each {param ->
        def temp = param?.split( '=' );

        if ( temp.size() == 2 )
        {
          if ( temp[1] == "null" )
          {
            temp[1] = ""
          }
          params.put( temp[0].trim(), temp[1].trim() )
        }
        else if ( temp.size() == 1 )
        {
          params.put( temp[0].trim(), "" )
        }
      }
    }

    if ( !session.rasterEntryResultCurrentTab )
    {
      session["rasterEntryResultCurrentTab"] = "1"
    }

    def rasterEntries = null
    def totalCount = null
    def rasterFiles = null

    def queryParams = initRasterEntryQuery( params )
    if ( chainModel )
    {
      rasterEntries = chainModel.rasterEntries
      totalCount = chainModel.totalCount
      rasterFiles = chainModel.rasterFiles
    }
    else
    {
      rasterEntries = rasterEntrySearchService.runQuery( queryParams, params )
      totalCount = rasterEntrySearchService.getCount( queryParams )

      if ( rasterEntries )
      {
        rasterFiles = RasterFile.createCriteria().list {
          eq( "type", "main" )
          inList( "rasterDataSet", rasterEntries?.rasterDataSet )
        }
      }
      else
      {
        totalCount = 0
      }
      def endtime = System.currentTimeMillis()

/*
      def user = authenticateService.principal()?.username
*/
      def user = springSecurityService.principal.username

      def logData = [
              TYPE: "raster_search",
              START: new Date( starttime ),
              END: new Date( endtime ),
              ELAPSE_TIME_MILLIS: endtime - starttime,
              USER: user,
              PARAMS: params
      ]

//      println "\nparams: ${params?.sort { it.key }}"
//      println "\nqueryParams: ${queryParams?.toMap()}"

      log.info( logData )

//      println logData
    }

//    println "=== results end ==="

    render( view: 'results', model: [
            rasterEntries: rasterEntries,
            totalCount: totalCount,
            rasterFiles: rasterFiles,
            tagNameList: tagNameList,
            tagHeaderList: tagHeaderList,
            queryParams: queryParams,
            sessionAction: "updateSession",
            sessionController: "session",
            rasterEntryResultCurrentTab: session.rasterEntryResultCurrentTab
    ] )

  }

  def results_mobile( )
  {

//    println "=== results start ==="

    def starttime = System.currentTimeMillis()
    if ( !params.max || !( params.max =~ /\d+$/ ) )
    {
      params.max = 10
    }
    else
    {
      def max = params.max as Integer
      if ( max > 100 )
      {
        params.max = 100;
      }
    }
    if ( !session.rasterEntryResultCurrentTab && ( "${session.rasterEntryResultCurrentTab}" != "0" ) )
    {
      session["rasterEntryResultCurrentTab"] = "0"
    }
    def rasterEntries = null
    def totalCount = null
    def rasterFiles = null

    def queryParams = initRasterEntryQuery( params )
    if ( chainModel )
    {
      rasterEntries = chainModel.rasterEntries
      totalCount = chainModel.totalCount
      rasterFiles = chainModel.rasterFiles
    }
    else
    {
      rasterEntries = rasterEntrySearchService.runQuery( queryParams, params )
      totalCount = rasterEntrySearchService.getCount( queryParams )

      if ( rasterEntries )
      {
        rasterFiles = RasterFile.createCriteria().list {
          eq( "type", "main" )
          inList( "rasterDataSet", rasterEntries?.rasterDataSet )
        }
      }

      def endtime = System.currentTimeMillis()
/*
      def user = authenticateService.principal()?.username
*/
      def user = springSecurityService.principal.username

      def logData = [
              TYPE: "raster_search",
              START: new Date( starttime ),
              END: new Date( endtime ),
              ELAPSE_TIME_MILLIS: endtime - starttime,
              USER: user,
              PARAMS: params
      ]

//      println "\nparams: ${params?.sort { it.key }}"
//      println "\nqueryParams: ${queryParams?.toMap()}"

      log.info( logData )

//      println logData
    }

//    println "=== results end ==="


    render( view: 'results_mobile', model: [
            rasterEntries: rasterEntries,
            totalCount: totalCount,
            rasterFiles: rasterFiles,
            tagNameList: tagNameList,
            tagHeaderList: tagHeaderList,
            queryParams: queryParams,
            sessionAction: "updateSession",
            sessionController: "session",
            rasterEntryResultCurrentTab: session.rasterEntryResultCurrentTab
    ] )

  }

  def getKML( )
  {

    def rasterEntry = RasterEntry.get( params.rasterEntryIds )

    if ( !rasterEntry )
    {
      flash.message = "RasterEntry not found with id ${params.rasterEntryIds}"
      redirect( action: 'list' )
    }
    else
    {
      def kmlFile = RasterEntryFile.findByTypeAndRasterEntry( "kml", rasterEntry )

      if ( kmlFile )
      {
        def kml = new File( kmlFile?.name )?.text
        response.setHeader( "Content-disposition", "attachment; filename=foo.kml" )
        render( contentType: "application/vnd.google-earth.kml+xml", text: kml, encoding: "UTF-8" )
      }
    }
  }

  def kmlnetworklink( )
  {
    def kmlbuilder = new StreamingMarkupBuilder()

    kmlbuilder.encoding = "UTF-8"

    // Google Earth hates the following parameters?

//    8.times { params.remove("searchTagNames[${it}]") }
    params.remove( "_action_kmlnetworklink" )

    params.dateSort = "false"
    WMSRequest request = new WMSRequest()

    def map = request.customParametersToMap()
    map.each {k, v ->
      if ( !params."${k}" )
      {
        params."${k}" = v
      }
    }

    def serviceAddress = createLink( absolute: true, base: "${grailsApplication.config.omar.serverURL}", controller: "rasterKmlQuery", action: "getImagesKml", params: params )

/*
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
              viewFormat("BBOX=[bboxWest],[bboxSouth],[bboxEast],[bboxNorth]")
              httpQuery("googleClientVersion=[clientVersion]")
            }
          }
        }
      }
    }
*/
    def kmlnode = {
      mkp.xmlDeclaration()
      kml( "xmlns", "http://earth.google.com/kml/2.1" ) {
        NetworkLink() {
          name( "OMAR Image Query Results" )
          Link() {
            href {
              mkp.yieldUnescaped( "<![CDATA[${serviceAddress}]]>" )
            }
            httpQuery( "googleClientVersion=[clientVersion];" )
            viewRefreshMode( "onRequest" )
          }
        }
      }
    }

    def kmlwriter = new StringWriter()

    kmlwriter << kmlbuilder.bind( kmlnode )
    response.setHeader( "Content-disposition", "attachment; filename=singleRequestTopImages.kml" )
    render( contentType: "application/vnd.google-earth.kml+xml", text: kmlwriter.buffer, encoding: "UTF-8" )

  }

  public void afterPropertiesSet( )
  {
    baseWMS = webMappingService.baseLayers

    dataWMS = grailsApplication.config.wms.data.raster
    tagHeaderList = grailsApplication.config.rasterEntry.tagHeaderList
    tagNameList = grailsApplication.config.rasterEntry.tagNameList
  }
}
