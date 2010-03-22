package org.ossim.omar
/**
 * Created by IntelliJ IDEA.
 * User: sbortman
 * Date: Jun 12, 2008
 * Time: 1:48:33 PM
 * To change this template use File | Settings | File Templates.
 */

import java.text.SimpleDateFormat;
import org.hibernate.criterion.Restrictions
import org.hibernate.criterion.Criterion

import org.ossim.postgis.IntersectsExpression
import org.ossim.postgis.Geometry
import org.apache.commons.collections.map.CaseInsensitiveMap

class VideoDataSetQuery
{
  public static final String RADIUS_SEARCH = "RADIUS"
  public static final String BBOX_SEARCH = "BBOX"

  String searchMethod = RasterEntryQuery.BBOX_SEARCH

  String aoiMaxLat
  String aoiMinLon
  String aoiMinLat
  String aoiMaxLon

  Date startDate
  Date endDate

  String centerLon
  String centerLat

  String viewMaxLat
  String viewMinLon
  String viewMinLat
  String viewMaxLon

  String aoiRadius


  List<String> searchTagNames = ["", "", "", "", "", "", "", ""]
  List<String> searchTagValues = ["", "", "", "", "", "", "", ""]

  def createClause()
  {
    IntersectsExpression intersects = createIntersection()
    Criterion range = createDateRange()

    def clause = null

    if ( intersects && range )
    {
      clause = Restrictions.and(intersects, range)
    }
    else
    {
      if ( intersects )
      {
        clause = intersects
      }
      else if ( range )
      {
        clause = range
      }
    }

    return clause
  }

  Criterion createDateRange(String startDateColumnName = "startDate", String endDateColumnName = "endDate")
  {
    def range = null

    if ( startDate && endDate )
    {
      range = Restrictions.or(
          Restrictions.and(
              Restrictions.ge(startDateColumnName, startDate),
              Restrictions.le(startDateColumnName, endDate)),
          Restrictions.and(
              Restrictions.ge(endDateColumnName, startDate),
              Restrictions.le(endDateColumnName, endDate))
      )
    }
    else
    {
      if ( startDate )
      {
        range = Restrictions.ge(endDateColumnName, startDate)
      }
      else if ( endDate )
      {
        range = Restrictions.le(startDateColumnName, endDate)
      }
    }

    return range
  }

  IntersectsExpression createIntersection(String geomColumnName = "groundGeom")
  {
    def intersects = null

    Geometry groundGeom = getGroundGeom()

    if ( groundGeom )
    {
      intersects = new IntersectsExpression(geomColumnName, groundGeom)
    }

    return intersects
  }

  def getGroundGeom()
  {
    def srs = "4326"
    def wkt = null
    def bounds = null

    switch ( searchMethod )
    {
      case BBOX_SEARCH:
        def minLat, minLon, maxLat, maxLon
        if ( aoiMaxLat && aoiMinLon && aoiMinLat && aoiMaxLon )
        {
          minLat = aoiMinLat
          minLon = aoiMinLon
          maxLat = aoiMaxLat
          maxLon = aoiMaxLon
        }
        else
        {
          minLat = viewMinLat
          minLon = viewMinLon
          maxLat = viewMaxLat
          maxLon = viewMaxLon
        }

        def coordinateConversionService = new CoordinateConversionService()

        maxLat = coordinateConversionService.convertToDecimalDegrees(maxLat)
        maxLon = coordinateConversionService.convertToDecimalDegrees(maxLon)
        minLat = coordinateConversionService.convertToDecimalDegrees(minLat)
        minLon = coordinateConversionService.convertToDecimalDegrees(minLon)

        wkt = Geometry.createPolygon(
            minLon,
            minLat,
            maxLon,
            maxLat
        )
        break

      case RADIUS_SEARCH:
        if ( centerLon && centerLat && aoiRadius )
        {
          def coordinateConversionService = new CoordinateConversionService()

          centerLat = coordinateConversionService.convertToDecimalDegrees(centerLat)
          centerLon = coordinateConversionService.convertToDecimalDegrees(centerLon)

          wkt = coordinateConversionService.computePointRadiusWKT(centerLon, centerLat, aoiRadius)
        }
        break
    }

    if ( wkt )
    {
      //println wkt
      bounds = Geometry.fromString("SRID=${srs};${wkt}")
      //println bounds
    }

    return bounds
  }


  void caseInsensitiveBind(def params)
  {
    def keys = properties.keySet()
    def tempParams = new CaseInsensitiveMap()
    params.each { tempParams.put(it.key, it.value)}

    keys.each{
      def value = tempParams.get(it)
      if(value)
      {
        setProperty("${it}", value)
      }
    }
    // now check the lists
    def idx = 0
    def value = tempParams.get("searchTagNames[${idx}]")
    while(value)
    {
      searchTagNames[idx] = value
      ++idx
      value = tempParams.get("searchTagNames[${idx}]")
    }
    idx = 0
    value = tempParams.get("searchTagValues[${idx}]")
    while(value)
    {
      searchTagValues[idx] = value
      ++idx
      value = tempParams.get("searchTagValues[${idx}]")
    }
  }
  

  def toMap()
  {
    SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    String startDateText = (startDate) ? formatter.format(startDate) : "";
    String endDateText = (endDate) ? formatter.format(endDate) : "";

    def data = [
        aoiMaxLat: aoiMaxLat, aoiMinLon: aoiMinLon, aoiMinLat: aoiMinLat, aoiMaxLon: aoiMaxLon,
        startDate: startDateText, endDate: endDateText,
        centerLat: centerLat, centerLon: centerLon, aoiRadius: aoiRadius, searchMethod: searchMethod,
        viewMaxLat: viewMaxLat, viewMinLon: viewMinLon, viewMinLat: viewMinLat, viewMaxLon: viewMaxLon
    ]

    (0..<searchTagValues.size()).each {
      data["searchTagNames[${it}]"] = searchTagNames[it]
      data["searchTagValues[${it}]"] = searchTagValues[it]
    }

    data.sort { it.key }
  }

  def toMap2()
  {
    def map = toMap()

    map?.each {k, v ->
      if ( !v )
      {
        map[k] = ""
      }
    }

    return map
  }

}