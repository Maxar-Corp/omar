package org.ossim.omar
//import javax.jws.WebParam

import org.hibernate.FetchMode as FM
import org.hibernate.CacheMode as CM
import org.ossim.omar.RasterEntryMetadata
import org.hibernate.FetchMode
import org.hibernate.CacheMode

import org.ossim.postgis.Geometry

class RasterEntrySearchService
{
  def grailsApplication

  //static expose = ['xfire']

  static transactional = false


  List<RasterEntryQuery> runQuery(RasterEntryQuery rasterEntryQuery, Map<String, String> params)
  {
    def x = {
      if ( rasterEntryQuery?.groundGeom )
      {
        addToCriteria(rasterEntryQuery.createIntersection("groundGeom"))
      }
      if ( rasterEntryQuery?.startDate || rasterEntryQuery?.endDate )
      {
        addToCriteria(rasterEntryQuery.createDateRange("acquisitionDate"))
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
            sortColumn = params?.sort
            break
        }
        if ( sortColumn )
        {
          order(sortColumn, params?.order)
        }
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
      fetchMode("rasterEntry", FetchMode.JOIN)
    }

    def metadata = RasterEntryMetadata.createCriteria().list(x)
    def rasterEntries = metadata?.collect {it.rasterEntry}
//    def rasterEntries = metadata?.rasterEntry

    //rasterEntries?.each { it.mainFile }

    return rasterEntries
  }



  List<Geometry> getGeometries(RasterEntryQuery rasterEntryQuery, Map<String, String> params)
  {
    def x = {
      projections { property("groundGeom") }
      if ( rasterEntryQuery?.groundGeom )
      {
        addToCriteria(rasterEntryQuery.createIntersection("groundGeom"))
      }
      if ( rasterEntryQuery?.startDate || rasterEntryQuery?.endDate )
      {
        addToCriteria(rasterEntryQuery.createDateRange("acquisitionDate"))
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

    def geometries = RasterEntryMetadata.createCriteria().list(x)

    return geometries
  }

  int getCount(RasterEntryQuery rasterEntryQuery)
  {
    def totalCount = RasterEntryMetadata.createCriteria().get {
      projections { rowCount() }
      if ( rasterEntryQuery?.groundGeom )
      {
        addToCriteria(rasterEntryQuery.createIntersection("groundGeom"))
      }
      if ( rasterEntryQuery?.startDate || rasterEntryQuery?.endDate )
      {
        addToCriteria(rasterEntryQuery.createDateRange("acquisitionDate"))
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
            def searchString = "%<${tag}>%${content}%</${tag}>%"
            ilike("otherTagsXml", searchString)
          }
          else
          {
            def searchString = "%${results['value']}%"
            ilike(results["property"], searchString)
          }
        }
      }
    }

    return totalCount
  }
}
