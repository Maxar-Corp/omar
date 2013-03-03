package org.ossim.omar.raster
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
import org.ossim.omar.ogc.BaseQuery
import org.ossim.omar.core.Utility
import org.ossim.omar.core.ISO8601DateParser

class RasterEntryQuery extends BaseQuery
{
  String niirs
  String rasterDataSetId
  String id
  String indexId
  String imageId
  String title
  RasterEntryQuery()
  {
    super()
    filterTypeMap = Utility.createTypeMap(org.ossim.omar.raster.RasterEntry.class)
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
    def createDateRangeTime(def columnName="acquisitionDate")
    {
        def dateColumnName = columnName
        def disj = null

        if(time)
        {
            disj = Restrictions.disjunction();

            def intervals = ISO8601DateParser.parseOgcTimeIntervals(time)
            if(intervals)
            {
                intervals.each{interval->
                  def startDate = new Date(interval.getStart().getMillis());
                  def endDate   = new Date(interval.getEnd().getMillis());
                  if(interval.toDurationMillis() == 0)
                  {
                    def range = null

                    if ( startDate && endDate )
                    {
                      disj.add(Restrictions.eq(dateColumnName, startDate))
                    }
                  }
                  else
                  {
                    disj.add(Restrictions.and(Restrictions.ge(dateColumnName, startDate),
                                              Restrictions.le(dateColumnName, endDate)
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
    else if(time)
    {
        def criterion = createDateRangeTime("acquisitionDate")
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
    if(id)
    {
        if(id.contains(","))
        {
            def idArray = id.split(",");
            def disjunct =  Restrictions.disjunction();

            for(idTemp in idArray)
            {
                disjunct.add(Restrictions.eq("id", idTemp as Long))
            }

            result.add(disjunct)
        }
        else
        {
            result.add(Restrictions.eq("id", id as Long))
        }
    }
    if(indexId)
    {
        if(indexId.contains(","))
        {
            def indexIdArray = indexId.split(",");
            def disjunct =  Restrictions.disjunction();

            for(indexIdTemp in indexIdArray)
            {
                disjunct.add(Restrictions.eq("indexId", indexIdTemp))
            }

            result.add(disjunct)
        }
        else
        {
            result.add(Restrictions.eq("indexId", indexId))
        }
    }
    if(imageId)
    {
        if(imageId.contains(","))
        {
            def idArray = imageId.split(",")
            def disjunct =  Restrictions.disjunction();
            for(idTemp in idArray)
            {
                disjunct.add(Restrictions.ilike("imageId", "%${idTemp}%"))
            }
            result.add(disjunct)
        }
        else
        {
            result.add(Restrictions.ilike("imageId", "%${imageId}%"))
        }
    }
    if(title)
    {
        if(title.contains(","))
        {
            def titleArray = title.split(",")
            def disjunct =  Restrictions.disjunction();
            for(titleTemp in titleArray)
            {
                disjunct.add(Restrictions.ilike("title", "%${titleTemp}%"))
            }
            result.add(disjunct)
        }
        else
        {
            result.add(Restrictions.eq("title", title))
        }
    }
    if(rasterDataSetId)
    {
        def rasterDataSet = RasterDataSet.get( rasterDataSetId )
        result.add(Restrictions.eq("rasterDataSet", rasterDataSet ))
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
    return result;
  }
}