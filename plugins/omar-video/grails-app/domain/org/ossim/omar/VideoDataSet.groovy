package org.ossim.omar

import org.ossim.omar.Repository
import com.vividsolutions.jts.geom.Geometry
import com.vividsolutions.jts.io.WKTReader

class VideoDataSet
{

  long width
  long height

  static hasMany = [fileObjects: VideoFile]
  Repository repository

//  static hasOne = [metadata: VideoDataSetMetadata]
  Geometry groundGeom

  Date startDate
  Date endDate
  String otherTagsXml

  static transients = ["otherTagsMap"]

  Map<String, String> otherTagsMap = [:]

  static mapping = {
    columns {
      otherTagsXml type: 'text'//, index: 'video_data_set_metadata_other_tags_idx'
      startDate column: 'start_date', type: 'timestamp', index: 'video_data_set_start_date_idx,video_data_set_time_idx'
      endDate column: 'end_date', type: 'timestamp', index: 'video_data_set_end_date_idx,video_data_set_time_idx'
      groundGeom type: org.hibernatespatial.GeometryUserType
    }
  }

  static constraints = {
    width(min: 0L)
    height(min: 0L)
    otherTagsXml(nullable: true, blank: false)
    startDate(nullable: true)
    endDate(nullable: true)
    groundGeom(nullable: true)
//    metadata(nullable: true)
  }

  def getMainFile()
  {
    def mainFile = null

    if ( !mainFile )
    {
      //mainFile = org.ossim.omar.VideoFile.findByVideoDataSetAndType(videoDataSet, "main")

      mainFile = VideoFile.createCriteria().get {
        eq("type", "main")
        createAlias("videoDataSet", "d")
        eq("videoDataSet", this)
      }

    }

    return mainFile
  }

  static VideoDataSet initVideoDataSet(def videoDataSetNode, VideoDataSet videoDataSet=null)
  {
    if(!videoDataSet)
    {
        videoDataSet = new VideoDataSet()
    }

    videoDataSetNode.fileObjects.VideoFile.each {videoFileNode ->
      VideoFile videoFile = VideoFile.initVideoFile(videoFileNode)

      videoDataSet.addToFileObjects(videoFile)
    }

    videoDataSet.width  = videoDataSetNode?.width?.toLong()
    videoDataSet.height = videoDataSetNode?.height?.toLong()

    def start = videoDataSetNode?.TimeSpan?.begin?.toString()
    def end = videoDataSetNode?.TimeSpan?.end?.toString()
    videoDataSet.startDate = DateUtil.parseDate(start)
    videoDataSet.endDate = DateUtil.parseDate(end)


    if ( videoDataSetNode?.groundGeom?.toString() )
    {
     videoDataSet.groundGeom = initGroundGeom(videoDataSetNode?.groundGeom)
    }
    else if ( videoDataSetNode?.spatialMetadata?.toString() )
    {
       def srsId = 4326;
	   videoDataSetNode?.spatialMetadata?.groundGeom?.each { groundGeomNode ->
		  if ( videoDataSet.groundGeom == null )
		  {
			videoDataSet.groundGeom = initGroundGeom(groundGeomNode)
            srsId =   videoDataSet.groundGeom?.getSRID()
		  }
		  else
		  {
			videoDataSet.groundGeom =  videoDataSet.groundGeom.union(initGroundGeom(groundGeomNode))
            videoDataSet.groundGeom?.setSRID(srsId);
		  }
	   }
    }

    def metadataNode = videoDataSetNode?.metadata
    initVideoDataSetMetadata(metadataNode, videoDataSet)
    initVideoDataSetOtherTagsXml(videoDataSet)
    
    return videoDataSet
  }
  static def initVideoDataSetOtherTagsXml(VideoDataSet videoDataSet)
  {
    if ( videoDataSet )
    {
      def builder = new groovy.xml.StreamingMarkupBuilder().bind {
        metadata {
          videoDataSet.otherTagsMap.each {k, v ->
            "${k}"(v)
          }
        }
      }

      videoDataSet.otherTagsXml = builder.toString()
    }
  }
  static void initVideoDataSetMetadata(def node, VideoDataSet videoDataSet)
  {
    if(!videoDataSet) return;
    
    node.children().each {tagNode ->

      if ( tagNode.children().size() > 0 )
      {
        def name = tagNode.name().toString().toUpperCase()

        switch ( name )
        {
          default:
            initVideoDataSetMetadata(tagNode, videoDataSet)
        }
      }
      else
      {
        def name = tagNode.name().toString().trim()
        def value = tagNode.text().toString().trim()

        if ( name && value )
        {
          switch ( name.toLowerCase() )
          {
            default:
              videoDataSet.otherTagsMap[name] = value
          }
        }
      }
    }
  }

  static Geometry initGroundGeom(def groundGeomNode)
  {
    def wkt = groundGeomNode?.toString().trim()
    def srs = groundGeomNode?.@srs?.toString().trim()
    def groundGeom = null

    if ( wkt && srs )
    {
      try
      {
        srs -= "epsg:"

//        def geomString = "SRID=${srs};${wkt}"

        //groundGeom = Geometry.fromString(geomString)
        groundGeom = new WKTReader().read(wkt)
		groundGeom.setSRID(Integer.parseInt(srs))
//        println "GROUND GEOM ============= ${groundGeom}"
      }
      catch (Exception e)
      {
        System.err.println("Cannt create geom for: srs=${srs} wkt=${wkt}")
      }
    }

    return groundGeom
  }
}
