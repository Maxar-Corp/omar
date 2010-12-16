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

class VideoDataSetQuery extends BaseQuery
{

  def createClause()
  {
    Criterion intersects = createIntersection()
    Criterion range = createDateRange()

    def result = Restrictions.conjunction();

    if ( intersects )
    {
      result.add(intersects)
    }
    if ( range )
    {
      result.add(range)
    }
    if(filter)
    {
      def clause = org.ossim.omar.GeoQueryUtil.createClauseFromOgcFilter(VideoDataSet.class, filter)
      if(clause)
      {
        result.add(clause)
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