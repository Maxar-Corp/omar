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


class RasterEntryQuery extends BaseQuery
{
  String niirs
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

  def createClause()
  {
    def result = Restrictions.conjunction();
    if ( groundGeom )
    {
      def criterion =  createIntersection("groundGeom")
      if(criterion)
      {
        result.add(criterion)
      }
    }
    if ( startDate || endDate )
    {
      def criterion = createDateRange("acquisitionDate")
      if(criterion)
      {
        result.add(criterion)
      }
    }
    // we will support 2 ways to populate certain fields.  We will support array
    // or direct.  niirs will be direct field or an array
    if ( niirs )
    {
      result.add(Restrictions.ge("niirs", niirs as double))
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
          result.add(Restrictions.ilike("otherTagsXml", "%<${tag}>%${content}%</${tag}>%"))
        }
        else
        {
          String prop = results["property"]
          prop = prop.toLowerCase()
          if ( prop == "niirs" && !niirs )
          {
            result.add(Restrictions.ge("niirs", results['value'] as double))
          }
          else
          {
            result.add(Restrictions.ilike(results["property"], "%${results['value']}%"))
          }
        }
      }
    }

    return result;
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
}