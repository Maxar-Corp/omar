package org.ossim.omar.video

import org.hibernate.CacheMode

import org.hibernate.criterion.*
//import javax.jws.WebParam

//import org.ossim.postgis.Geometry

import com.vividsolutions.jts.geom.Geometry

import org.springframework.beans.factory.InitializingBean
import org.ossim.omar.core.Utility

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
        def max = null;
        if(params?.max!=null)  max = (params.max as Integer);
        if(max<1) return null;
        def criteriaBuilder = VideoDataSet.createCriteria();
        def x = {
          if ( max )
          {
            setMaxResults(max)
          }
          if ( params?.offset )
          {
            setFirstResult(params.offset as Integer)
          }
          if ( params?.sort && params?.order )
          {
            if ( params?.sort == "id" || params?.sort in propertyNames )
            {
              def sortColumn = params?.sort
              def order = params?.order
              def ordering = (order == "asc") ? Order.asc(sortColumn) : Order.desc(sortColumn)

              addOrder(ordering)
            }

            //setFetchMode("rasterEntry", FetchMode.JOIN)
          }
        }

        def criteria = criteriaBuilder.buildCriteria(x)
        def clause = videoDataSetQuery?.createClause()
        if(clause)
        {
          criteria.add(clause)
        }

        criteria.list()
    }

  List<VideoDataSet> runQueryOld(
  /*@WebParam (name = "videoDataSetQuery", header = true)*/
  VideoDataSetQuery videoDataSetQuery,
  /*@WebParam (name = "params", header = true)*/
  Map<String, String> params)
  {
      def max = null;
      if(params?.max!=null)  max = (params.max as Integer);
      if(max<1) return null;
    def x = {
      if ( videoDataSetQuery?.groundGeom )
      {
        addToCriteria(videoDataSetQuery.createIntersection("groundGeom"))
      }

      if ( videoDataSetQuery?.startDate || videoDataSetQuery?.endDate )
      {
        addToCriteria(videoDataSetQuery.createDateRange("startDate", "endDate"))
      }

      if ( max )
      {
        setMaxResults(max)
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

  void scrollFeatures(VideoDataSetQuery videoDataSetQuery, Map<String, String> params, Closure closure)
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
    criteria.setReadOnly(true)

    def results = criteria.scroll(/*ScrollMode.FORWARD_ONLY*/)
    def status = results.first()

    while ( status )
    {
      def geom = results.get(0)

      closure.call([groundGeom: geom])

      status = results.next()
    }

    results.close()
  }


  int getCount(VideoDataSetQuery videoDataSetQuery)
  {

      def criteriaBuilder = VideoDataSet.createCriteria();
      def x =
      {
        projections { rowCount()}
      }
      def criteria = criteriaBuilder.buildCriteria(x)
      criteria.add(videoDataSetQuery?.createClause())
      def totalCount = criteria.list().get(0) as int
      return totalCount

  }

  void afterPropertiesSet()
  {
    propertyNames = grailsApplication.getDomainClass("org.ossim.omar.video.VideoDataSet")?.properties.name
  }
}
