package org.ossim.omar

import org.ossim.omar.VideoDataSet
import org.hibernate.CacheMode as CM
import org.hibernate.CacheMode
import org.hibernate.FetchMode as FM
import org.hibernate.FetchMode

//import javax.jws.WebParam

//import org.ossim.postgis.Geometry

import com.vividsolutions.jts.geom.Geometry
import org.hibernate.ScrollableResults
import org.springframework.beans.factory.InitializingBean

class VideoDataSetSearchService implements InitializingBean
{
  //static expose = ['xfire']

  static transactional = true

  def grailsApplication
  def propertyNames

  List<VideoDataSet> runQuery(
  /*@WebParam (name = "videoDataSetQuery", header = true)*/
  VideoDataSetQuery videoDataSetQuery,
  /*@WebParam (name = "params", header = true)*/
  Map<String, String> params)
  {
    def x = {
      if ( videoDataSetQuery?.groundGeom )
      {
        addToCriteria(videoDataSetQuery.createIntersection("groundGeom"))
      }

      if ( videoDataSetQuery?.startDate || videoDataSetQuery?.endDate )
      {
        addToCriteria(videoDataSetQuery.createDateRange("startDate", "endDate"))
      }

      if ( params?.max )
      {
        maxResults(params.max as Integer)
      }

      if ( params?.offset )
      {
        firstResult(params.offset as Integer)
      }

      if ( params?.sort && params?.order )
      {
        if ( params?.sort == "id" || params?.sort in propertyNames )
        {
          order(params?.sort, params?.order)
        }
      }

      videoDataSetQuery.searchTagNames?.size()?.times {i ->
        String name = videoDataSetQuery.searchTagNames[i]
        String value = videoDataSetQuery.searchTagValues[i]

        if ( name && value )
        {
          def results = Utility.parseSearchTag(name, value)

          if ( results["property"] == "otherTagsXml" )
          {
            String tag = results["tag"].trim()
            String content = results["content"].trim()
            ilike("otherTagsXml", "%<${tag}>%${content}%</${tag}>%")
          }
          else
          {
            ilike(results["property"], "%${results['value']}%")
          }
        }
      }
    }


    return VideoDataSet.createCriteria().list(x)
  }


  List<Geometry> getGeometries(VideoDataSetQuery videoDataSetQuery, Map<String, String> params)
  {
    def x = {
      projections { property("groundGeom") }
      if ( videoDataSetQuery?.groundGeom )
      {
        addToCriteria(videoDataSetQuery.createIntersection("groundGeom"))
      }
      if ( videoDataSetQuery?.startDate || videoDataSetQuery?.endDate )
      {
        addToCriteria(videoDataSetQuery.createDateRange("startDate", "endDate"))
      }
      if ( params?.max )
      {
        maxResults(params.max as Integer)
      }
      if ( params?.offset )
      {
        firstResult(params.offset as Integer)
      }
      videoDataSetQuery.searchTagNames?.size()?.times {i ->
        String name = videoDataSetQuery.searchTagNames[i]
        String value = videoDataSetQuery.searchTagValues[i]

        if ( name && value )
        {
          def results = Utility.parseSearchTag(name, value)

          if ( results["property"] == "otherTagsXml" )
          {
            String tag = results["tag"].trim()
            String content = results["content"].trim()
            ilike("otherTagsXml", "%<${tag}>%${content}%</${tag}>%")
          }
          else
          {
            ilike(results["property"], "%${results['value']}%")
          }
        }
      }
      cacheMode(CacheMode.GET)
    }

    return VideoDataSet.createCriteria().list(x)
    //def geometries = VideoDataSetMetadata.createCriteria().list(x)
    //return geometries
  }

  void scrollGeometries(VideoDataSetQuery videoDataSetQuery, Map<String, String> params, Closure closure)
  {
    def criteriaBuilder = VideoDataSet.createCriteria();

    def x = {
      projections { property("groundGeom") }

      if ( params?.max )
      {
        maxResults(params.max as Integer)
      }

      if ( params?.offset )
      {
        firstResult(params.offset as Integer)
      }
      cacheMode(CacheMode.GET)
    }

    def criteria = criteriaBuilder.buildCriteria(x)

    criteria.add(videoDataSetQuery?.createClause())

    def results = criteria.scroll()
    def status = results.first()

    while ( status )
    {
      def geom = results.get(0)

      closure.call(geom)

      status = results.next()
    }

    results.close()
  }


  int getCount(VideoDataSetQuery videoDataSetQuery)
  {
    def totalCount = VideoDataSet.createCriteria().get {
      projections { rowCount() }
      if ( videoDataSetQuery?.groundGeom )
      {
        addToCriteria(videoDataSetQuery.createIntersection("groundGeom"))
      }
      if ( videoDataSetQuery?.startDate || videoDataSetQuery?.endDate )
      {
        addToCriteria(videoDataSetQuery.createDateRange("startDate", "endDate"))
      }
      videoDataSetQuery.searchTagNames?.size()?.times {i ->
        String name = videoDataSetQuery.searchTagNames[i]
        String value = videoDataSetQuery.searchTagValues[i]

        if ( name && value )
        {
          def results = Utility.parseSearchTag(name, value)

          if ( results["property"] == "otherTagsXml" )
          {
            String tag = results["tag"].trim()
            String content = results["content"].trim()
            ilike("otherTagsXml", "%<${tag}>%${content}%</${tag}>%")
          }
          else
          {
            ilike(results["property"], "%${results['value']}%")
          }
        }
      }
    }

    return totalCount
  }

  void afterPropertiesSet()
  {
    propertyNames = grailsApplication.getDomainClass("org.ossim.omar.VideoDataSet")?.properties.name
  }
}
