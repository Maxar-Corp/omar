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
    def criteriaBuilder = RasterEntry.createCriteria();
    def x = {
      if ( params?.max )
      {
        setMaxResults(params.max as Integer)
      }
      if ( params?.offset )
      {
        setFirstResult(params.offset as Integer)
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
          addOrder(ordering)
        }
        setFetchMode("rasterEntry", FetchMode.JOIN)
    }
    }
    def criteria = criteriaBuilder.buildCriteria(x)
    criteria.add(rasterEntryQuery?.createClause())
    return criteria.list()

    /*
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
    */
  }



  List<Polygon> getGeometries(RasterEntryQuery rasterEntryQuery, Map<String, String> params)
  {
    def criteriaBuilder = RasterEntry.createCriteria();
    def x =
    {
      projections { property("groundGeom") }
      firstResult(params.offset as Integer)
      maxResults(params.max as Integer)
      cacheMode(CacheMode.GET)
    }
    def criteria = criteriaBuilder.buildCriteria(x)
    criteria.add(rasterEntryQuery?.createClause())

    return  criteria.list()
    /*
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
    */
  }

  ScrollableResults scrollGeometries(RasterEntryQuery rasterEntryQuery, Map<String, String> params)
  {
    def criteriaBuilder = RasterEntry.createCriteria();
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
    criteria.add(rasterEntryQuery?.createClause())
    return criteria.scroll()
  }



  int getCount(RasterEntryQuery rasterEntryQuery)
  {
    def criteriaBuilder = RasterEntry.createCriteria();
    def x =
    {
      def queryObject = rasterEntryQuery
      projections { rowCount()}
    }
    def criteria = criteriaBuilder.buildCriteria(x)
    criteria.add(rasterEntryQuery?.createClause())
    def totalCount = criteria.list().get(0) as int
    return totalCount
  }
}
