package org.ossim.omar.core

import com.vividsolutions.jts.geom.Coordinate
import com.vividsolutions.jts.geom.Geometry
import com.vividsolutions.jts.geom.GeometryFactory
import com.vividsolutions.jts.geom.PrecisionModel
import com.vividsolutions.jts.io.WKTReader
import org.hibernate.criterion.Criterion
import org.hibernatespatial.criterion.SpatialFilter
import java.text.SimpleDateFormat
import org.apache.commons.collections.map.CaseInsensitiveMap

import org.apache.log4j.Logger
import org.ossim.omar.oms.CoordinateConversionService
import org.hibernate.criterion.Restrictions

/**
 * Created by IntelliJ IDEA.
 * User: gpotts
 * Date: Aug 3, 2010
 * Time: 12:31:03 PM
 * To change this template use File | Settings | File Templates.
 */
class BaseQuery
{
  private static log = Logger.getLogger(BaseQuery.class)
  def grailsApplication

  public static final String RADIUS_SEARCH = "RADIUS"
  public static final String BBOX_SEARCH = "BBOX"
  String searchMethod = BBOX_SEARCH

  String aoiMaxLat
  String aoiMinLon
  String aoiMinLat
  String aoiMaxLon
  String centerLon
  String centerLat

  String viewMaxLat
  String viewMinLon
  String viewMinLat
  String viewMaxLon

  String aoiRadius

  Date startDate
  Date endDate

  List<String> searchTagNames = ["", "", "", "", "", "", "", "", ""]
  List<String> searchTagValues = ["", "", "", "", "", "", "", "", ""]

  String filter

  /**
   * Set via a call to org.ossim.omar.core.Utility.createTypeMap(Foo.class)  where
   * Foo.class can be replaced with any class in the system.
   */
  def filterTypeMap

  def order
  def sort
  def time
  Boolean spatialSearchFlag = true

  BaseQuery()
  {
//    log =   org.apache.log4j.LogManager.getLogger("grails.app.omar.BaseQuery")
  }

  Criterion createIntersection(String geomColumnName = "groundGeom")
  {
    def intersects = null
    if ( spatialSearchFlag )
    {
      Geometry groundGeom = getGroundGeom()

      if ( groundGeom )
      {
        //intersects = new IntersectsExpression(geomColumnName, groundGeom)
        intersects = new SpatialFilter(geomColumnName, groundGeom)
      }
    }

    return intersects
  }

  Geometry getGroundGeom()
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
        def maxLatDD = Double.valueOf(coordinateConversionService.convertToDecimalDegrees(maxLat))
        def maxLonDD = Double.valueOf(coordinateConversionService.convertToDecimalDegrees(maxLon))
        def minLatDD = Double.valueOf(coordinateConversionService.convertToDecimalDegrees(minLat))
        def minLonDD = Double.valueOf(coordinateConversionService.convertToDecimalDegrees(minLon))
        //wkt = Geometry.createPolygon(
        //    minLon,
        //    minLat,
        //    maxLon,
        //    maxLat
        //)

        def geometryFactory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), 4326)


        def coords = [
                new Coordinate(minLonDD, minLatDD),
                new Coordinate(minLonDD, maxLatDD),
                new Coordinate(maxLonDD, maxLatDD),
                new Coordinate(maxLonDD, minLatDD),
                new Coordinate(minLonDD, minLatDD)
        ] as Coordinate[]

        def polygon = geometryFactory.createPolygon(geometryFactory.createLinearRing(coords), null)


        wkt = polygon.toText()
      }

      break

    case RADIUS_SEARCH:
      def defaultRadius = aoiRadius ?: "0"
      if ( centerLon && centerLat )
      {
        def coordinateConversionService = new CoordinateConversionService()

        centerLat = coordinateConversionService.convertToDecimalDegrees(centerLat)
        centerLon = coordinateConversionService.convertToDecimalDegrees(centerLon)

        wkt = coordinateConversionService.computePointRadiusWKT(centerLon, centerLat, defaultRadius)
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

  def caseInsensitiveBind(def params)
  {
    def keys = properties.keySet()
    def tempParams = new CaseInsensitiveMap(params)

    keys.each {
      def value = tempParams.get(it)
      if ( value )
      {
        if ( ("${it}" == "startDate") ||
                ("${it}" == "endDate") )
        {
          setProperty("${it}", DateUtil.initializeDate("${it}", tempParams))
        }
        else if ( "${it}" == "time" )
        {
          setProperty("${it}", value)
          def intervals = ISO8601DateParser.parseOgcTimeStartEndPairs(value)
          if ( intervals )
          {
            if ( intervals.size() == 1 )
            {
              if ( intervals[0].start )
              {
                startDate = new Date(intervals[0].start.getMillis())
              }
              if ( intervals[0].end )
              {
                endDate = new Date(intervals[0].end.getMillis())
              }
            }
          }
        }
        else
        {
          setProperty("${it}", value)
        }
      }
    }
    searchTagNames?.size()?.times {i ->
      searchTagNames[i] = tempParams.get("searchTagNames[${i}]")
    }
    searchTagValues?.size()?.times {i ->
      searchTagValues[i] = tempParams.get("searchTagValues[${i}]");
    }

    return this
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
            viewMaxLat: viewMaxLat, viewMinLon: viewMinLon, viewMinLat: viewMinLat, viewMaxLon: viewMaxLon,
            filter: filter, time: time, spatialSearchFlag: spatialSearchFlag
    ]

    (0..<searchTagValues.size()).each {
      data["searchTagNames[${it}]"] = searchTagNames[it]
      data["searchTagValues[${it}]"] = searchTagValues[it]
    }

    data.sort { it.key }
  }

  def createClause()
  {
    def clause = createIntersection()
    if ( filter )
    {
      try
      {

        def filterClause = GeoQueryUtil.createClauseFromOgcFilter(filterTypeMap, filter)

        if(clause)
        {
          def temp = Restrictions.conjunction();
          temp.add(clause)
          clause = temp
          clause.add(filterClause);
        }
        clause = filterClause;
      }
      catch (Exception e)
      {
        log.error(e)
        clause = null
      }
    }
    clause
  }
}
