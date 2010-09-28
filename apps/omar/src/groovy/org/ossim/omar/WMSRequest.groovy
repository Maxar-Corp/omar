package org.ossim.omar
/**
 * Created by IntelliJ IDEA.
 * User: sbortman
 * Date: Aug 12, 2008
 * Time: 12:42:19 PM
 * To change this template use File | Settings | File Templates.
 */

import java.awt.Color
import org.hibernate.criterion.*
import org.apache.commons.collections.map.CaseInsensitiveMap

class WMSRequest
{
  String bbox
  String width
  String height
  String format
  String layers
  String srs
  String service
  String version
  String request
  String transparent
  String bgcolor
  String styles
  String stretch_mode
  String stretch_mode_region
  String sharpen_mode
  String sharpen_width
  String sharpen_sigma
  String quicklook
  String null_flip
  String exception
  String bands
  String time
  def toMap()
  {
     return [bbox: bbox, width: width, height: height, format: format, layers: layers, srs: srs, service: service,
            version: version, request: request, transparent: transparent, bgcolor: bgcolor, styles: styles,
            stretch_mode: stretch_mode, stretch_mode_region: stretch_mode_region, sharpen_mode: sharpen_mode,
            sharpen_width: sharpen_width, sharpen_sigma: sharpen_sigma, time: time, null_flip: null_flip,
            exception: exception, quicklook: quicklook].sort { it.key }
  }
  /**
   * This is a query param to control the max results when building the criteria
   */
  public String toString()
  {
    return toMap()
  }

  String[] getDates()
  {
    return (time) ? time.split(",") : []
  }

  def getDateRange()
  {
    def result = []
    def dates = this.dates

    if ( dates )
    {
      (0..<dates.size()).each {
        def range = ISO8601DateParser.getDateRange(dates[it])

        if ( range.size() > 0 )
        {
          result.add(range[0])
          if ( range.size() > 1 )
          {
            result.add(range[1])
          }
        }
      }
    }

    return result
  }

  def getBackgroundColor()
  {
    def result = new Color(0, 0, 0)
    if ( bgcolor )
    {
      if ( bgcolor.size() == 8 )
      {
        // skip 0x
        result = new Color(Integer.decode("0x" + bgcolor[2] + bgcolor[3]),
                Integer.decode("0x" + bgcolor[4] + bgcolor[5]),
                Integer.decode("0x" + bgcolor[6] + bgcolor[7]))
      }
    }

    return result
  }

  def getTransparentFlag()
  {
    def result = false;
    if ( transparent )
    {
      result = Boolean.toBoolean(transparent)
    }
    return result
  }

  def getFormat()
  {
    switch ( format?.toLowerCase() )
    {
    case "jpeg":
    case "jpg":
    case "image/jpeg":
    case "image/jpg":
      if ( transparent?.equalsIgnoreCase("true") )
      {
        format = "image/png"
      }
      else
      {
        format = "image/png"
      }
      break
    case "png":
    case "image/png":
      format = "image/png"
      break
    case "gif":
    case "image/gif":
      format = "image/gif"
      break
    default:
      format = "image/png"
      break
    }

    return format
  }
  /*
  def createDateRangeRestriction()
  {
    def dateColumnName = "acquisitionDate"
    def disj = Restrictions.disjunction();

    def intervals = ISO8601DateParser.parseWMSIntervals(time)
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
  def createClause()
  {
    def names = []
    if(layers)
    {
      layers.split(',').each
      {
        names.add(it)
      }
    }
    def  result = Restrictions.conjunction()
    RasterEntryQuery rasterQuery = new RasterEntryQuery()
    if ( bbox )
    {
      def bounds = bbox.split(',')
      rasterQuery.aoiMinLon = bounds[0]
      rasterQuery.aoiMinLat = bounds[1]
      rasterQuery.aoiMaxLon = bounds[2]
      rasterQuery.aoiMaxLat = bounds[3]
    }

    result.add(rasterQuery.createIntersection())
    
    def disj = Restrictions.disjunction();
    names.each() {name ->
      try
      {
        def value = java.lang.Long.valueOf(name)
        disj.add(Restrictions.eq('id', value))
      }
      catch (java.lang.Exception e)
      {
        disj.add(Restrictions.eq('title', name))
        disj.add(Restrictions.eq('imageId', name))
      }
    }
    result.add(disj)
    result.add(createDateRangeRestriction())
    return result
  }
  */
}