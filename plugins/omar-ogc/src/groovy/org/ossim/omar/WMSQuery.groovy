package org.ossim.omar

import org.hibernate.criterion.Restrictions
import org.hibernate.criterion.Criterion
import org.hibernate.criterion.Order

/**
 * Created by IntelliJ IDEA.
 * User: gpotts
 * Date: Aug 16, 2010
 * Time: 10:11:18 AM
 * To change this template use File | Settings | File Templates.
 */
class WMSQuery extends BaseQuery
{
  def bbox
  def layers
  def max
  WMSQuery()
  {
    super()
    filterTypeMap = org.ossim.omar.Utility.createTypeMap(RasterEntry.class) +
                    org.ossim.omar.Utility.createTypeMap(VideoDataSet.class) 

  }
  def createDateRangeRestrictionRaster(def columnName="acquisitionDate")
  {
    def dateColumnName = columnName
    def disj = Restrictions.disjunction();

    def intervals = ISO8601DateParser.parseOgcTimeIntervals(time)
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
    return disj
  }
  def createVideoClause()
  {
    
  }
  Criterion createIntersection(String geomColumnName = "groundGeom")
  {
    if ( bbox )
    {
      def bounds = bbox.split(',')
      aoiMinLon = bounds[0]
      aoiMinLat = bounds[1]
      aoiMaxLon = bounds[2]
      aoiMaxLat = bounds[3]
      searchMethod = BBOX_SEARCH
    }

    return super.createIntersection(geomColumnName)
  }
  def createRasterClause()
  {
    def names = []
    if(layers)
    {
      layers.split(',').each
      {
        names.add(it)
      }
    }
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

    def geomIntersect = createIntersection()
    if(geomIntersect)
    {
      result.add(geomIntersect)
    }
    if(names.size()>0)
    {
      def disj = Restrictions.disjunction();
      names.each() {name ->
        try
        {
          def value = java.lang.Long.valueOf(name)
          disj.add(Restrictions.eq('id', value))
        }
        catch (java.lang.Exception e)
        {
          disj.add(Restrictions.eq('imageId', name))
          disj.add(Restrictions.eq('indexId', name))
        }
      }
      result.add(disj)
    }
    def dateIntersect = createDateRangeRestrictionRaster()
    if(dateIntersect)
    {
      result.add(dateIntersect)
    }

    return result
  }
  def getRasterEntriesAsList()
  {
    def names = layers?layers.split(","):[]
    def layersCopy = layers
    def tempMax = 10
    try
    {
      tempMax = Integer.valueOf(max);
    }
    catch(Exception e)
    {
      tempMax = 10;
    }
    def result = []
    if(names.size() > 0)
    {
      // we must query layer results individually
      names.each{name->
        layers = name
        def tempRasterEntry = RasterEntry.createCriteria().list{
                                      maxResults(tempMax)
                                      addToCriteria(createRasterClause())
                                      }
        if(tempRasterEntry)
        {
          tempRasterEntry.each{
            result.add(it)
            if(result.size() >= tempMax)
            {
              return result
            }
          }
        }
      }
    }
    else
    {
        def ordering = null
        if(order&&sort)
        {
            ordering = (order == "asc") ? Order.asc(sort) : Order.desc(sort)
        }
        def tempRasterEntry = RasterEntry.createCriteria().list{
                                      maxResults(tempMax)
                                      addToCriteria(createRasterClause())
                                      if(ordering) addOrder(ordering)
                                      }
        if(tempRasterEntry)
        {
          tempRasterEntry.each{
            result.add(it)
            if(result.size() >= tempMax)
            {
              return result
            }
          }
        }
      // we will add support for a general query that is similar to the last 10 or last N images.
    }
    layers = layersCopy

    return result;

  }
}
