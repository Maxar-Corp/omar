package org.ossim.omar.video

import com.vividsolutions.jts.geom.Geometry
import com.vividsolutions.jts.geom.GeometryCollection
import com.vividsolutions.jts.geom.MultiPolygon
import com.vividsolutions.jts.geom.Polygon
import com.vividsolutions.jts.geom.PrecisionModel


import com.vividsolutions.jts.io.WKTReader

import org.ossim.omar.core.DateUtil
import org.ossim.omar.core.Repository

class VideoDataSet
{
  String filename
  Long width
  Long height

  static hasMany = [fileObjects: VideoFile]
  Collection fileObjects

  Repository repository

  MultiPolygon groundGeom

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
    width( min: 0L )
    height( min: 0L )
    filename( nullable: true )
    otherTagsXml( nullable: true, blank: false )
    startDate( nullable: true )
    endDate( nullable: true )
    groundGeom( nullable: true )
    indexId( nullable: false, blank: false, unique: true )
    styleId( nullable: true )
//    metadata(nullable: true)
  }

  def getMainFile( )
  {
    def mainFile = null

    if ( !mainFile )
    {
      //mainFile = org.ossim.omar.video.VideoFile.findByVideoDataSetAndType(videoDataSet, "main")

      mainFile = VideoFile.createCriteria().get {
        eq( "type", "main" )
        createAlias( "videoDataSet", "d" )
        eq( "videoDataSet", this )
      }

    }

    return mainFile
  }

  def getFileFromObjects( def type = "main" )
  {
    return fileObjects?.find { it.type == type }
  }

  static VideoDataSet initVideoDataSet( def videoDataSetNode, VideoDataSet videoDataSet = null )
  {
    if ( !videoDataSet )
    {
      videoDataSet = new VideoDataSet()
    }

    for ( def videoFileNode in videoDataSetNode.fileObjects.VideoFile )
    {
      VideoFile videoFile = VideoFile.initVideoFile( videoFileNode )

      videoDataSet.addToFileObjects( videoFile )
    }

    videoDataSet.width = videoDataSetNode?.width?.toLong()
    videoDataSet.height = videoDataSetNode?.height?.toLong()

    def start = videoDataSetNode?.TimeSpan?.begin?.toString()
    def end = videoDataSetNode?.TimeSpan?.end?.toString()
    videoDataSet.startDate = DateUtil.parseDate( start )
    videoDataSet.endDate = DateUtil.parseDate( end )

    def defaultGeometry;
    if ( videoDataSetNode?.groundGeom?.toString() )
    {
      videoDataSet.groundGeom = initGroundGeom( videoDataSetNode?.groundGeom )
    }
    else if ( videoDataSetNode?.spatialMetadata?.toString() )
    {
      def srsId = 4326;

      for ( def groundGeomNode in videoDataSetNode?.spatialMetadata?.groundGeom )
      {
        def sensorDistance = groundGeomNode?.@sensorDistance?.toString().trim()
        def elevation = groundGeomNode.@elevation?.toString().trim()
        // just in case we will make sure that we have at least one geometry
        if ( !defaultGeometry )
        {
          defaultGeometry = initGroundGeom( groundGeomNode )
        }
        if ( sensorDistance && elevation )
        {
          double ratio = ( sensorDistance as Double ) / ( elevation as Double );
          if ( ratio < 20 )
          {
            if ( videoDataSet.groundGeom == null )
            {
              videoDataSet.groundGeom = initGroundGeom( groundGeomNode )
              srsId = videoDataSet.groundGeom?.getSRID()
            }
            else
            {
              def x = initGroundGeom( groundGeomNode )
              def y = videoDataSet.groundGeom.union( x )
              def z = null

              switch ( y )
              {
              case Polygon:
                z = convertPolyToMultiPoly( y )
                break
              case GeometryCollection:
                if ( y.isEmpty() )
                {
                  z = new MultiPolygon( [] as Polygon[], new PrecisionModel( PrecisionModel.FLOATING ), 4326 )
                }
                break
              default:
                z = y
              }

              z?.setSRID( srsId );
              videoDataSet.groundGeom = z
            }
          }
        }
      }
    }


    if ( !videoDataSet.groundGeom )
    {
      videoDataSet.groundGeom = defaultGeometry
    }


    def metadataNode = videoDataSetNode?.metadata
    initVideoDataSetMetadata( metadataNode, videoDataSet )
    initVideoDataSetOtherTagsXml( videoDataSet )
    def mainFile = videoDataSet.getFileFromObjects( "main" )
    def filename
    if ( mainFile )
    {
      filename = mainFile.name
    }
    if ( !videoDataSet.filename && filename )
    {
      videoDataSet.filename = ( filename as File )
    }
    if ( !videoDataSet.indexId )
    {
      if ( filename )
      {
        def tempFilename = filename.replaceAll( "/|\\\\", "_" )
        videoDataSet.indexId = "${filename}".encodeAsSHA256()
      }
    }
    return videoDataSet
  }

  static def initVideoDataSetOtherTagsXml( VideoDataSet videoDataSet )
  {
    if ( videoDataSet )
    {
      def builder = new groovy.xml.StreamingMarkupBuilder().bind {
        metadata {
          for ( def entry in videoDataSet.otherTagsMap )
          {
            "${entry.key}"( entry.value )
          }
        }
      }

      videoDataSet.otherTagsXml = builder.toString()
    }
  }

  static void initVideoDataSetMetadata( def node, VideoDataSet videoDataSet )
  {
    if ( !videoDataSet ) return;

    for ( def tagNode in node.children() )
    {

      if ( tagNode.children().size() > 0 )
      {
        def name = tagNode.name().toString().toUpperCase()

        switch ( name )
        {
        default:
          initVideoDataSetMetadata( tagNode, videoDataSet )
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
          case "filename":
            videoDataSet.filename = value as File
            break
          default:
            videoDataSet.otherTagsMap[name] = value
          }
        }
      }
    }
  }

  static MultiPolygon initGroundGeom( def groundGeomNode )
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
        groundGeom = new WKTReader().read( wkt )
        groundGeom.setSRID( Integer.parseInt( srs ) )
//        println "GROUND GEOM ============= ${groundGeom}"
      }
      catch ( Exception e )
      {
        System.err.println( "Cannt create geom for: srs=${srs} wkt=${wkt}" )
      }
    }

    if ( groundGeom instanceof Polygon )
    {
      groundGeom = convertPolyToMultiPoly( groundGeom )
    }

    return groundGeom
  }

  static MultiPolygon convertPolyToMultiPoly( Polygon poly )
  {
    return new MultiPolygon(
            [poly] as Polygon[],
            new PrecisionModel( PrecisionModel.FLOATING ),
            poly.getSRID() )

  }
}
