package org.ossim.omar

import org.ossim.omar.Repository
import com.vividsolutions.jts.geom.Geometry
import com.vividsolutions.jts.io.WKTReader
import org.joda.time.DateTime
class VideoDataSet
{
  String filename
  long width
  long height

  static hasMany = [fileObjects: VideoFile]
  Repository repository

  Geometry groundGeom

//  DateTime startDate
//  DateTime endDate
  Date startDate
  Date endDate


  String otherTagsXml

  static transients = ["otherTagsMap"]

  Map<String, String> otherTagsMap = [:]
  String indexId
  BigInteger styleId

  static mapping = {
    columns {
      indexId index: 'video_data_set_index_id_idx'
      filename index: 'video_data_set_filename_idx'
      otherTagsXml type: 'text'//, index: 'video_data_set_metadata_other_tags_idx'
      startDate column: 'start_date', type: 'timestamp', index: 'video_data_set_start_date_idx,video_data_set_time_idx'
      endDate column: 'end_date', type: 'timestamp', index: 'video_data_set_end_date_idx,video_data_set_time_idx'
      groundGeom type: org.hibernatespatial.GeometryUserType
    }
  }

  static constraints = {
    width(min: 0L)
    height(min: 0L)
    filename(nullable: true)
    otherTagsXml(nullable: true, blank: false)
    startDate(nullable: true)
    endDate(nullable: true)
    groundGeom(nullable: true)
    indexId(nullable: false, blank: false, unique: true)
    styleId(nullable: true)
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
  def getFileFromObjects(def type = "main")
  {
    return fileObjects?.find { it.type == type }
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

    def defaultGeometry;
    if ( videoDataSetNode?.groundGeom?.toString() )
    {
     videoDataSet.groundGeom = initGroundGeom(videoDataSetNode?.groundGeom)
    }
    else if ( videoDataSetNode?.spatialMetadata?.toString() )
    {
       def srsId = 4326;
	   videoDataSetNode?.spatialMetadata?.groundGeom?.each { groundGeomNode ->
         def sensorDistance = groundGeomNode?.@sensorDistance?.toString().trim()
         def elevation      = groundGeomNode.@elevation?.toString().trim()
         // just in case we will make sure that we have at least one goemetry
         if(!defaultGeometry)
         {
            defaultGeometry = initGroundGeom(groundGeomNode)
         }
         if(sensorDistance&&elevation)
         {
           double ratio = (sensorDistance as Double)/(elevation as Double);
           if(ratio < 20)
           {
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
	   }
    }
    if(!videoDataSet.groundGeom)
    {
      videoDataSet.groundGeom = defaultGeometry
    }
    def metadataNode = videoDataSetNode?.metadata
    initVideoDataSetMetadata(metadataNode, videoDataSet)
    initVideoDataSetOtherTagsXml(videoDataSet)
    def mainFile = videoDataSet.getFileFromObjects("main")
    def filename
    if(mainFile)
    {
      filename = mainFile.name
    }
    if(!videoDataSet.filename&&filename)
    {
      videoDataSet.filename = (filename as File).getName()
    }
    if ( !videoDataSet.indexId )
    {
      if(filename)
      {
        def tempFilename = filename.replaceAll("/|\\\\", "_")
        videoDataSet.indexId = "${filename}".encodeAsSHA256()
      }
    }
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
