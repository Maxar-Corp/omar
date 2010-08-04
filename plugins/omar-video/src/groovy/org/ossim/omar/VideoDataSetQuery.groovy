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


import org.apache.commons.collections.map.CaseInsensitiveMap

class VideoDataSetQuery extends BaseQuery
{

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

  void caseInsensitiveBind(def params)
  {
    def keys = properties.keySet()
    def tempParams = new CaseInsensitiveMap(params)

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

}