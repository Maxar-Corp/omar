package org.ossim.omar
/**
 * Created by IntelliJ IDEA.
 * User: sbortman
 * Date: Jun 12, 2008
 * Time: 1:48:33 PM
 * To change this template use File | Settings | File Templates.
 */

import org.springframework.context.ApplicationContextAware
import org.springframework.context.ApplicationContext
import org.springframework.beans.factory.InitializingBean
import java.text.SimpleDateFormat;
import org.hibernate.criterion.Restrictions
import org.hibernate.criterion.Criterion
import org.apache.commons.collections.map.CaseInsensitiveMap

//import org.ossim.postgis.Geometry
//import org.ossim.postgis.IntersectsExpression

import com.vividsolutions.jts.geom.Polygon
import com.vividsolutions.jts.io.WKTReader
import org.hibernatespatial.criterion.SpatialFilter
import com.vividsolutions.jts.geom.Coordinate
import com.vividsolutions.jts.geom.Geometry
import com.vividsolutions.jts.geom.GeometryFactory
import com.vividsolutions.jts.geom.PrecisionModel

class RasterEntryQuery
{
  def grailsApplication
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

  String niirs

  List<String> searchTagNames = ["", "", "", "", "", "", "", ""]
  List<String> searchTagValues = ["", "", "", "", "", "", "", ""]

  def createClause()
  {
    Criterion intersects = createIntersection()
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

  void caseInsensitiveBind(def params)
  {
    def keys = properties.keySet()
    def tempParams = new CaseInsensitiveMap(params)

    keys.each {
      def value = tempParams.get(it)
      if ( value )
      {
        setProperty("${it}", value)
      }
    }
    // now check the lists
    def idx = 0
    def value = tempParams.get("searchTagNames[${idx}]")
    while ( value )
    {
      searchTagNames[idx] = value
      ++idx
      value = tempParams.get("searchTagNames[${idx}]")
    }
    idx = 0
    value = tempParams.get("searchTagValues[${idx}]")
    while ( value )
    {
      searchTagValues[idx] = value
      ++idx
      value = tempParams.get("searchTagValues[${idx}]")
    }
  }

  Criterion createDateRange(String dateColumnName = "acquisitionDate")
  {
    def range = null

    if ( startDate && endDate )
    {
      range = Restrictions.and(
              Restrictions.ge(dateColumnName, startDate),
              Restrictions.le(dateColumnName, endDate)
      )
    }
    else
    {
      if ( startDate )
      {
        range = Restrictions.ge(dateColumnName, startDate)
      }
      else if ( endDate )
      {
        range = Restrictions.le(dateColumnName, endDate)
      }
    }

    return range
  }

  Criterion createIntersection(String geomColumnName = "groundGeom")
  {
    def intersects = null

    Geometry groundGeom = getGroundGeom()

    if ( groundGeom )
    {
      //intersects = new IntersectsExpression(geomColumnName, groundGeom)
      intersects = new SpatialFilter(geomColumnName, groundGeom)
    }

    return intersects
  }

  void addToCriteria(def criteria)
  {
    if ( groundGeom )
    {
      criteria.add(createIntersection("groundGeom"))
    }
    if ( startDate || endDate )
    {
      criteria.add(createDateRange("acquisitionDate"))
    }
    // we will support 2 ways to populate certain fields.  We will support array
    // or direct.  niirs will be direct field or an array
    if ( niirs )
    {
      criteria.ge("niirs", niirs as double)
    }
    searchTagNames?.size()?.times {i ->
      String name = searchTagNames[i]
      String value = searchTagValues[i]

      if ( name && value )
      {
        def results = Utility.parseSearchTag(name, value)

        if ( results["property"] == "otherTagsXml" )
        {
          String tag = results["tag"].trim()
          String content = results["content"].trim()
          criteria.add(criteria.ilike("otherTagsXml", "%<${tag}>%${content}%</${tag}>%"))
        }
        else
        {
          String prop = results["property"]
          prop = prop.toLowerCase()
          if ( prop == "niirs" && !niirs )
          {
            criteria.ge("niirs", results['value'] as double)
          }
          else
          {
            criteria.ilike(results["property"], "%${results['value']}%")
          }
        }
      }
    }
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
      // only do a bounds if one exists
      //
      if ( minLat && maxLat && minLon && maxLon )
      {
        def coordinateConversionService = new CoordinateConversionService()

        maxLat = coordinateConversionService.convertToDecimalDegrees(maxLat)
        maxLon = coordinateConversionService.convertToDecimalDegrees(maxLon)
        minLat = coordinateConversionService.convertToDecimalDegrees(minLat)
        minLon = coordinateConversionService.convertToDecimalDegrees(minLon)

        //wkt = Geometry.createPolygon(
        //    minLon,
        //    minLat,
        //    maxLon,
        //    maxLat
        //)

        def geometryFactory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), 4326)

        minLon = Double.parseDouble(minLon)
        minLat = Double.parseDouble(minLat)
        maxLon = Double.parseDouble(maxLon)
        maxLat = Double.parseDouble(maxLat)

        def coords = [
                new Coordinate(minLon, minLat), new Coordinate(minLon, maxLat), new Coordinate(maxLon, maxLat), new Coordinate(maxLon, minLat), new Coordinate(minLon, minLat)
        ] as Coordinate[]

        def polygon = geometryFactory.createPolygon(geometryFactory.createLinearRing(coords), null)


        wkt = polygon.toText()
      }

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
      //bounds = Geometry.fromString("SRID=${srs};${wkt}")
      bounds = new WKTReader().read(wkt)
      bounds?.setSRID(Integer.parseInt(srs))
      //println bounds
    }

    return bounds
  }

  def toMap()
  {
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    String startDateText = (startDate) ? formatter.format(startDate) : null;
    String endDateText = (endDate) ? formatter.format(endDate) : null;

    def data = [
            aoiMaxLat: aoiMaxLat, aoiMinLon: aoiMinLon, aoiMinLat: aoiMinLat, aoiMaxLon: aoiMaxLon,
            startDate: startDateText, endDate: endDateText,
            centerLat: centerLat, centerLon: centerLon, aoiRadius: aoiRadius, searchMethod: searchMethod,
            viewMaxLat: viewMaxLat, viewMinLon: viewMinLon, viewMinLat: viewMinLat, viewMaxLon: viewMaxLon,
            niirs: niirs
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