import org.springframework.beans.factory.InitializingBean
import groovy.xml.StreamingMarkupBuilder

import org.grails.plugins.springsecurity.service.AuthenticateService

//import grails.converters.*

class VideoDataSetController implements InitializingBean
{

  public static final List tagHeaderList = []
  public static final List tagNameList = []

  def baseWMS
  def dataWMS

  def index = { redirect(action: list, params: params) }

  AuthenticateService authenticateService
  def videoDataSetSearchService

  // the delete, save and update actions only accept POST requests
  def static allowedMethods = [delete: 'POST', save: 'POST', update: 'POST']

  def list = {
    if ( !params.max )
    params.max = 10

    def videoDataSetList = null

    if ( params.repositoryId )
    {
      def repository = Repository.get(params.repositoryId)

      videoDataSetList = VideoDataSet.createCriteria().list(params) {
        eq("repository", repository)
      }
    }
    else
    {
      videoDataSetList = VideoDataSet.createCriteria().list(params) {}
    }

    [videoDataSetList: videoDataSetList]
    /*
    withFormat {
      html { [ videoDataSetList: videoDataSetList ] }
      xml { render videoDataSetList as XML }
      json { render videoDataSetList as JSON }
    }
    */
  }

  def show = {
    def videoDataSet = VideoDataSet.get(params.id)

    if ( !videoDataSet )
    {
      flash.message = "VideoDataSet not found with id ${params.id}"
      redirect(action: list)
    }
    else
    { return [videoDataSet: videoDataSet] }
  }

  def delete = {
    def videoDataSet = VideoDataSet.get(params.id)
    if ( videoDataSet )
    {
      videoDataSet.delete()
      flash.message = "VideoDataSet ${params.id} deleted"
      redirect(action: list)
    }
    else
    {
      flash.message = "VideoDataSet not found with id ${params.id}"
      redirect(action: list)
    }
  }

  def edit = {
    def videoDataSet = VideoDataSet.get(params.id)

    if ( !videoDataSet )
    {
      flash.message = "VideoDataSet not found with id ${params.id}"
      redirect(action: list)
    }
    else
    {
      return [videoDataSet: videoDataSet]
    }
  }

  def update = {
    def videoDataSet = VideoDataSet.get(params.id)
    if ( videoDataSet )
    {
      videoDataSet.properties = params
      if ( !videoDataSet.hasErrors() && videoDataSet.save() )
      {
        flash.message = "VideoDataSet ${params.id} updated"
        redirect(action: show, id: videoDataSet.id)
      }
      else
      {
        render(view: 'edit', model: [videoDataSet: videoDataSet])
      }
    }
    else
    {
      flash.message = "VideoDataSet not found with id ${params.id}"
      redirect(action: edit, id: params.id)
    }
  }

  def create = {
    def videoDataSet = new VideoDataSet()
    videoDataSet.properties = params
    return ['videoDataSet': videoDataSet]
  }

  def save = {
    def videoDataSet = new VideoDataSet(params)
    if ( !videoDataSet.hasErrors() && videoDataSet.save() )
    {
      flash.message = "VideoDataSet ${videoDataSet.id} created"
      redirect(action: show, id: videoDataSet.id)
    }
    else
    {
      render(view: 'create', model: [videoDataSet: videoDataSet])
    }
  }

  def search = {

    //println "=== search start ==="

    if ( !params.max )
    {
      params.max = 10;
    }

    //println "\nparams: ${params?.sort { it.key }}"

    def queryParams = initVideoDataSetQuery(params)

    //println "\nqueryParams: ${queryParams?.toMap()?.sort { it.key } }"

    if ( request.method == 'POST' )
    {
      if ( !params.max || !(params.max =~ /\d+$/) || (params.max as Integer) > 100 )
      {
        params.max = 10
      }

      params.order = 'desc'
      params.sort = 'startDate'

      //println "queryParams: ${queryParams}"

      def user = authenticateService.principal().username
      def starttime = System.currentTimeMillis()

      def videoDataSets = videoDataSetSearchService.runQuery(queryParams, params)
      def totalCount = videoDataSetSearchService.getCount(queryParams)

      def videoFiles = VideoFile.createCriteria().list {
        eq("type", "main")
        inList("videoDataSet", videoDataSets)
      }



      def endtime = System.currentTimeMillis()

      def logData = [
          TYPE: "video_search",
          START: new Date(starttime),
          END: new Date(endtime),
          ELAPSE_TIME_MILLIS: endtime - starttime,
          USER: user,
          PARAMS: params
      ]

      log.info(logData)

      //println logData

      //println "=== search end ==="

      chain(action: "results", model: [videoDataSets: videoDataSets, totalCount: totalCount, videoFiles: videoFiles], params: params)
    }
    else
    {
      //println "=== search end ==="

      return [queryParams: queryParams, baseWMS: baseWMS, dataWMS: dataWMS]
    }
  }

  private def initVideoDataSetQuery(Map params)
  {
    def queryParams = new VideoDataSetQuery()

    bindData(queryParams, params)

    initializeDate(queryParams, "startDate", params)
    initializeDate(queryParams, "endDate", params)

    return queryParams
  }

  private def initializeDate(VideoDataSetQuery queryParams, String dateField, Map params)
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

    //println "=== results start ==="

    def starttime = System.currentTimeMillis()

    if ( !params.max || !(params.max =~ /\d+$/) || (params.max as Integer) > 100 )
    {
      params.max = 10
    }

    def videoDataSets = null
    def totalCount = null
    def videoFiles = null

    def queryParams = initVideoDataSetQuery(params)

    if ( chainModel )
    {
      videoDataSets = chainModel.videoDataSets
      totalCount = chainModel.totalCount
      videoFiles = chainModel.videoFiles
    }
    else
    {
      videoDataSets = videoDataSetSearchService.runQuery(queryParams, params)
      totalCount = videoDataSetSearchService.getCount(queryParams)

      videoFiles = VideoFile.createCriteria().list {
        eq("type", "main")
        inList("videoDataSet", videoDataSets)
      }


      def endtime = System.currentTimeMillis()
      def user = authenticateService.principal()?.username

      def logData = [
          TYPE: "video_search",
          START: new Date(starttime),
          END: new Date(endtime),
          ELAPSE_TIME_MILLIS: endtime - starttime,
          USER: user,
          PARAMS: params
      ]


      //println "\nparams: ${params?.sort { it.key }}"
      //println "\nqueryParams: ${queryParams?.toMap()?.sort { it.key } }"

      log.info(logData)

      //println logData
    }

    //println "=== results end ==="

    render(view: 'results', model: [
        videoDataSets: videoDataSets,
        videoFiles: videoFiles,
        totalCount: totalCount,
        tagNameList: tagNameList,
        tagHeaderList: tagHeaderList,
        queryParams: queryParams
    ])

  }

  def kmlnetworklink = {
    def kmlbuilder = new StreamingMarkupBuilder()

    kmlbuilder.encoding = "UTF-8"


    params.remove("_action_kmlnetworklink")

    params.dateSort = "false"

    def serviceAddress = createLink(absolute: true, controller: "kmlQuery", action: "getVideosKml", params: params)

    def kmlnode = {
      mkp.xmlDeclaration()
      kml("xmlns": "http://earth.google.com/kml/2.1") {
        Folder() {
          name("Videos")
          visibility("1")
          open("1")
          description("")
          NetworkLink() {
            name("Video Query")
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
    kmlbuilder.bind(kmlnode)
    response.setHeader("Content-disposition", "attachment; filename=singleRequestTopVideos.kml")
    render(contentType: "application/vnd.google-earth.kml+xml", text: kmlbuilder.bind(kmlnode).toString(), encoding: "UTF-8")

  }

  public void afterPropertiesSet()
  {
    baseWMS = grailsApplication.config.wms.base
    dataWMS = grailsApplication.config.wms.data.video
  }
}
