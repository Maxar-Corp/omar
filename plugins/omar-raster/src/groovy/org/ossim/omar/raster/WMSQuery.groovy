package org.ossim.omar.raster

import org.hibernate.criterion.Restrictions
import org.hibernate.criterion.Criterion
import org.ossim.omar.core.Utility
import org.ossim.omar.core.ISO8601DateParser
import org.ossim.omar.core.BaseQuery

/**
 * Created by IntelliJ IDEA.
 * User: gpotts
 * Date: Aug 16, 2010
 * Time: 10:11:18 AM
 * To change this template use File | Settings | File Templates.
 */
class WMSQuery extends BaseQuery
{
  def niirs
  def bbox
  def layers
  def max

  WMSQuery()
  {
    super()
    filterTypeMap = Utility.createTypeMap( org.ossim.omar.raster.RasterEntry.class )
  }

  Criterion createDateRange(String dateColumnName = "acquisitionDate")
  {
    def range = null

    if ( startDate && endDate )
    {
      range = Restrictions.and(
          Restrictions.ge( dateColumnName, startDate ),
          Restrictions.le( dateColumnName, endDate )
      )
    }
    else
    {
      if ( startDate )
      {
        range = Restrictions.ge( dateColumnName, startDate )
      }
      else if ( endDate )
      {
        range = Restrictions.le( dateColumnName, endDate )
      }
    }

    return range
  }

  def createDateRangeTime(def columnName = "acquisitionDate")
  {
    def dateColumnName = columnName
    def disj = null

    if ( time )
    {
      disj = Restrictions.disjunction();

      def intervals = ISO8601DateParser.parseOgcTimeIntervals( time )
      if ( intervals )
      {
        intervals.each {interval ->
          def startDate = new Date( interval.getStart().getMillis() );
          def endDate = new Date( interval.getEnd().getMillis() );
          if ( interval.toDurationMillis() == 0 )
          {
            def range = null

            if ( startDate && endDate )
            {
              disj.add( Restrictions.eq( dateColumnName, startDate ) )
            }
          }
          else
          {
            disj.add( Restrictions.and( Restrictions.ge( dateColumnName, startDate ),
                Restrictions.le( dateColumnName, endDate )
            )
            )
          }
        }
      }
      else
      {
        disj = null
      }
    }
    disj
  }

  def createClause()
  {
    def baseClause = super.createClause()

    def result = null
    if ( baseClause instanceof org.hibernate.criterion.Conjunction )
    {
      result = baseClause
    }
    else
    {
      result = Restrictions.conjunction();
      if ( baseClause )
      {
        result.add( baseClause )
      }
    }


    if ( groundGeom )
    {
      def criterion = createIntersection( "groundGeom" )
      if ( criterion )
      {
        result.add( criterion )
      }
    }

    if ( startDate || endDate )
    {
      def criterion = createDateRange( "acquisitionDate" )
      if ( criterion )
      {
        result.add( criterion )
      }
    }
    else if ( time )
    {
      def criterion = createDateRangeTime( "acquisitionDate" )
      if ( criterion )
      {
        result.add( criterion )
      }
    }

    // we will support 2 ways to populate certain fields.  We will support array
    // or direct.  niirs will be direct field or an array
    if ( niirs )
    {
      result.add( Restrictions.ge( "niirs", niirs as double ) )
    }

    searchTagNames?.size()?.times {i ->
      String name = searchTagNames[i]
      String value = searchTagValues[i]

      if ( name && value )
      {
        def results = Utility.parseSearchTag( name, value )

        if ( results["property"] == "otherTagsXml" )
        {
          String tag = results["tag"].trim()
          String content = results["content"].trim()
          result.add( Restrictions.ilike( "otherTagsXml", "%<${ tag }>%${ content }%</${ tag }>%" ) )
        }
        else
        {
          String prop = results["property"]
          prop = prop.toLowerCase()
          if ( prop == "niirs" && !niirs )
          {
            result.add( Restrictions.ge( "niirs", results['value'] as double ) )
          }
          else
          {
            //result.add(Restrictions.like(results["property"], results['value'], MatchMode.ANYWHERE))
            result.add( Restrictions.ilike( results["property"], results['value'], MatchMode.ANYWHERE ) )
          }
        }
      }
    }
    return result;
  }

  Criterion createIntersection(String geomColumnName = "groundGeom")
  {
    if ( bbox )
    {
      def bounds = bbox.split( ',' )
      aoiMinLon = bounds[0]
      aoiMinLat = bounds[1]
      aoiMaxLon = bounds[2]
      aoiMaxLat = bounds[3]
      searchMethod = BBOX_SEARCH
    }

    return super.createIntersection( geomColumnName )
  }

  def caseInsensitiveBind(def params)
  {
    super.caseInsensitiveBind( params )

    if ( time )
    {
      startDate = null
      endDate = null
    }
  }
}
