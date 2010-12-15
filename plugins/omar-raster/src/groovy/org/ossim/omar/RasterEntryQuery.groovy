package org.ossim.omar
/**
 * Created by IntelliJ IDEA.
 * User: sbortman
 * Date: Jun 12, 2008
 * Time: 1:48:33 PM
 * To change this template use File | Settings | File Templates.
 */

import org.hibernate.criterion.Restrictions
import org.hibernate.criterion.Criterion
import org.hibernate.criterion.MatchMode

class RasterEntryQuery extends BaseQuery
{
  String niirs

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
      def criterion = createIntersection("groundGeom")
      if ( criterion )
      {
        result.add(criterion)
      }
    }

    if ( startDate || endDate )
    {
      def criterion = createDateRange("acquisitionDate")
      if ( criterion )
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
            //result.add(Restrictions.like(results["property"], results['value'], MatchMode.ANYWHERE))
            result.add(Restrictions.ilike(results["property"], results['value'], MatchMode.ANYWHERE))
          }
        }
      }
    }
    if(filter)
    {
      def clause = org.ossim.omar.GeoQueryUtil.createClauseFromOgcFilter(RasterEntry.class, filter)
      if(clause)
      {
        result.add(clause)
      }
    }
    return result;
  }
}