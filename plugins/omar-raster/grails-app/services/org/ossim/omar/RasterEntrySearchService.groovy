package org.ossim.omar
//import javax.jws.WebParam

import org.hibernate.FetchMode as FM
import org.hibernate.CacheMode as CM
//import org.ossim.omar.RasterEntryMetadata

import org.hibernate.FetchMode
import org.hibernate.CacheMode
import org.hibernate.criterion.*
//import org.ossim.postgis.Geometry

import com.vividsolutions.jts.geom.Polygon
import org.hibernate.ScrollableResults

class RasterEntrySearchService
{
  def grailsApplication

  //static expose = ['xfire']

  static transactional = false


  List<RasterEntryQuery> runQuery(RasterEntryQuery rasterEntryQuery, Map<String, String> params)
  {
    def criteria = RasterEntry.createCriteria();
    if ( params?.max )
    {
      criteria.setMaxResults(params.max as Integer)
    }
    if ( params?.offset )
    {
      criteria.setFirstResult(params.offset as Integer)
    }
    if ( params?.sort && params?.order )
    {
      def sortColumn = null
      // HACK:  Need to find a better way to do this
      switch ( params?.sort )
      {
      case "id":
      case "imageId":
      case "targetId":
      case "productId":
      case "sensorId":
      case "missionId":
      case "imageCategory":
      case "azimuthAngle":
      case "grazingAngle":
      case "securityClassification":
      case "title":
      case "organization":
      case "description":
      case "acquisitionDate":
      case "fileType":
      case "className":
      case "niirs":
        sortColumn = params?.sort
        break
      }
      if ( sortColumn )
      {
        def order = params?.order
        Order ordering = order == "asc" ? Order.asc(sortColumn) : Order.desc(sortColumn)
        criteria.addOrder(ordering)
      }
    }

    criteria.setFetchMode("rasterEntry", FetchMode.JOIN)
    rasterEntryQuery.addToCriteria(criteria)

    def rasterEntries = criteria.instance.list();

    return rasterEntries
  }



  List<Polygon> getGeometries(RasterEntryQuery rasterEntryQuery, Map<String, String> params)
  {
    def criteria = RasterEntry.createCriteria();

    criteria.setProjection(Projections.property("groundGeom"))

    if ( params?.offset )
    {
      criteria.setFirstResult(params.offset as Integer)
    }

    if ( params?.max )
    {
      criteria.setMaxResults(params.max as Integer)
    }

    criteria.setCacheMode(CacheMode.GET)
    rasterEntryQuery.addToCriteria(criteria)

    def geometries = criteria.instance.list()

    return geometries
  }

  ScrollableResults scrollGeometries(RasterEntryQuery rasterEntryQuery, Map<String, String> params)
  {
    def x = {
      projections { property("groundGeom") }

      if ( rasterEntryQuery?.groundGeom )
      {
        addToCriteria(rasterEntryQuery.createIntersection("groundGeom"))
      }

      if ( rasterEntryQuery?.startDate || rasterEntryQuery?.endDate )
      {
        addToCriteria(rasterEntryQuery.createDateRange("startDate", "endDate"))
      }

      if ( params?.max )
      {
        maxResults(params.max as Integer)
      }

      if ( params?.offset )
      {
        firstResult(params.offset as Integer)
      }

      rasterEntryQuery.searchTagNames?.size()?.times {i ->
        String name = rasterEntryQuery.searchTagNames[i]
        String value = rasterEntryQuery.searchTagValues[i]

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

    return RasterEntry.createCriteria().scroll(x)
  }



  int getCount(RasterEntryQuery rasterEntryQuery)
  {
    def criteria = RasterEntry.createCriteria();
    rasterEntryQuery.addToCriteria(criteria)
    criteria.setProjection(Projections.rowCount());
    def totalCount = criteria.instance.list().get(0) as int

    return totalCount
  }
}
