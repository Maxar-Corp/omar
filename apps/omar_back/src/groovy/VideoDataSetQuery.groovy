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

  private def createDateRange()
  {
    def range = null

    // Change the time portion of the end date
    // to be the end of the day. 23:59:59.999
    if ( endDate )
    {
      def cal = Calendar.instance
      cal.time = endDate
      cal.set(Calendar.HOUR, 23)
      cal.set(Calendar.MINUTE, 59)
      cal.set(Calendar.SECOND, 59)
      cal.set(Calendar.MILLISECOND, 999)
      endDate = cal.time
    }

    if ( startDate && endDate )
    {
      range = Restrictions.or(
          Restrictions.between("startDate", startDate, endDate),
          Restrictions.between("endDate", startDate, endDate)
      )
    }
    else
    {
      if ( startDate )
      {
        range = Restrictions.ge("endDate", startDate)
      }
      else if ( endDate )
      {
        range = Restrictions.le("startDate", endDate)
      }
    }

    return range
  }

  private IntersectsExpression createIntersection()
  {
    def intersects = null


    def srs = "4326"
    def wkt = null
    def bounds = null

    switch ( searchMethod )
    {
      case BBOX_SEARCH:
        if ( aoiMaxLat && aoiMinLon && aoiMinLat && aoiMaxLon )
        {
          def coordinateConversionService = new CoordinateConversionService()

          aoiMaxLat = coordinateConversionService.convertToDecimalDegrees(aoiMaxLat)
          aoiMaxLon = coordinateConversionService.convertToDecimalDegrees(aoiMaxLon)
          aoiMinLat = coordinateConversionService.convertToDecimalDegrees(aoiMinLat)
          aoiMinLon = coordinateConversionService.convertToDecimalDegrees(aoiMinLon)

          wkt = Geometry.createPolygon(
              aoiMinLon,
              aoiMinLat,
              aoiMaxLon,
              aoiMaxLat
          )
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
      bounds = Geometry.fromString("SRID=${srs};${wkt}")
      //println bounds
      intersects = new IntersectsExpression("groundGeom", bounds)
    }

    return intersects
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