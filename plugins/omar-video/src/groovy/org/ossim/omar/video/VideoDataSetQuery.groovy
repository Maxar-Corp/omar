package org.ossim.omar.video
/**
 * Created by IntelliJ IDEA.
 * User: sbortman
 * Date: Jun 12, 2008
 * Time: 1:48:33 PM
 * To change this template use File | Settings | File Templates.
 */

import org.hibernate.criterion.Restrictions
import org.hibernate.criterion.Criterion
import org.ossim.omar.ogc.BaseQuery
import org.ossim.omar.core.Utility

class VideoDataSetQuery extends BaseQuery
{
  VideoDataSetQuery()
  {
    super()
    filterTypeMap = Utility.createTypeMap(VideoDataSet.class)
  }
  def createClause()
  {
    def baseClause = super.createClause()

    def result = null
    if(baseClause instanceof org.hibernate.criterion.Conjunction)
    {
      result = baseClause
    }
    else
    {
      result =  Restrictions.conjunction();
      if(baseClause)
      {
        result.add(baseClause)
      }
    }
    Criterion intersects = createIntersection()
    Criterion range      = createDateRange()

    if ( intersects )
    {
      result.add(intersects)
    }
    if ( range )
    {
      result.add(range)
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
          result.add(Restrictions.ilike(results["property"], results['value'], MatchMode.ANYWHERE))
        }
      }
    }
    result
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
}