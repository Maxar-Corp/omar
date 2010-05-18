package org.ossim.omar

import org.ossim.omar.VideoDataSet

//import org.ossim.postgis.Geometry
import com.vividsolutions.jts.geom.Geometry
import com.vividsolutions.jts.io.WKTReader


class VideoDataSetMetadata
{
  Geometry groundGeom
 
  Date startDate
  Date endDate
  String otherTagsXml

  static transients = ["otherTagsMap"]

  Map<String, String> otherTagsMap = [:]

  VideoDataSet videoDataSet

  static mapping = {
    columns {
      otherTagsXml type: 'text'//, index: 'video_data_set_metadata_other_tags_idx'
      startDate column: 'start_date', type: 'timestamp', index: 'video_data_set_metadata_start_date_idx,video_data_set_time_idx'
      endDate column: 'end_date', type: 'timestamp', index: 'video_data_set_metadata_end_date_idx,video_data_set_time_idx'
      groundGeom type: org.hibernatespatial.GeometryUserType
    }
  }

  static constraints = {
    otherTagsXml(nullable: true, blank: false)
    videoDataSet(nullable: true)
    startDate(nullable: true)
    endDate(nullable: true)
    groundGeom(nullable: true)
  }

  static initVideoDataSetMetadata(def metadataNode, def videoDataSet)
  {
    if ( !videoDataSet.metadata )
    {
      videoDataSet.metadata = new VideoDataSetMetadata()
      videoDataSet.metadata.videoDataSet = videoDataSet
    }

    metadataNode.children().each {tagNode ->

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
              videoDataSet.metadata.otherTagsMap[name] = value
          }
        }
      }
    }
  }

  static def initVideoDataSetOtherTagsXml(VideoDataSetMetadata videoDataSetMetadata)
  {
    if ( videoDataSetMetadata )
    {
      def builder = new groovy.xml.StreamingMarkupBuilder().bind {
        metadata {
          videoDataSetMetadata.otherTagsMap.each {k, v ->
            "${k}"(v)
          }
        }
      }

      videoDataSetMetadata.otherTagsXml = builder.toString()
    }
  }

  static VideoDataSetMetadata initVideoDataSetMetadata(def videoDataSetNode)
  {
    VideoDataSetMetadata videoDataSetMetadata = new VideoDataSetMetadata()

    def start = videoDataSetNode?.TimeSpan?.begin?.toString()
    def end = videoDataSetNode?.TimeSpan?.end?.toString()

    videoDataSetMetadata.startDate = DateUtil.parseDate(start)
    videoDataSetMetadata.endDate = DateUtil.parseDate(end)


    if ( videoDataSetNode?.groundGeom?.size() != 0 )
    {
//      println   videoDataSetNode?.groundGeom
      videoDataSetMetadata.groundGeom = initGroundGeom(videoDataSetNode?.groundGeom)
    }
    else if ( videoDataSetNode?.spatialMetadata )
    {
	//println "HERE"
       def srsId = 4326;
	   videoDataSetNode?.spatialMetadata?.groundGeom?.each { groundGeomNode ->
		  if ( videoDataSetMetadata.groundGeom == null )
		  {
			videoDataSetMetadata.groundGeom = initGroundGeom(groundGeomNode)
            srsId =   videoDataSetMetadata.groundGeom?.getSRID()
		  }
		  else
		  { 
			videoDataSetMetadata.groundGeom =  videoDataSetMetadata.groundGeom.union(initGroundGeom(groundGeomNode))
            videoDataSetMetadata.groundGeom?.setSRID(srsId);
		  }
	   }
    }
    //println videoDataSetMetadata.groundGeom

    return videoDataSetMetadata
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
