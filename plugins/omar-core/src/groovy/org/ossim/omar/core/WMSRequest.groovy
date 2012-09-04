package org.ossim.omar.core
/**
 * Created by IntelliJ IDEA.
 * User: sbortman
 * Date: Aug 12, 2008
 * Time: 12:42:19 PM
 * To change this template use File | Settings | File Templates.
 */

import java.awt.Color

class WMSRequest
{
  String bbox
  Integer width
  Integer height
  String format
  String layers
  String srs
  String service
  String version
  String request
  Boolean transparent
  String bgcolor
  String styles
  String stretch_mode
  String stretch_mode_region
  String sharpen_mode
  String sharpen_width
  String sharpen_sigma
  String rotate
  String quicklook
  String null_flip
  String exception
  String bands
  String time
  String filter
  String brightness
  String contrast
  String interpolation

  def toMap()
  {
    return [bbox: bbox, width: width as Integer, height: height as Integer, format: format, layers: layers, srs: srs, service: service,
        version: version, request: request, transparent: transparent, bgcolor: bgcolor, styles: styles,
        stretch_mode: stretch_mode, stretch_mode_region: stretch_mode_region, sharpen_mode: sharpen_mode,
        sharpen_width: sharpen_width as Double, sharpen_sigma: sharpen_sigma as Double, rotate: rotate as Double,
        time: time, null_flip: null_flip, bands: bands, exception: exception, filter: filter,
        quicklook: quicklook, brightness: brightness, contrast: contrast, interpolation: interpolation].sort { it.key }
  }

  def customParametersToMap()
  {
    [bands: bands, stretch_mode: stretch_mode, stretch_mode_region: stretch_mode_region, sharpen_mode: sharpen_mode,
        sharpen_width: sharpen_width as Double, sharpen_sigma: sharpen_sigma as Double, rotate: rotate as Double,
        time: time, null_flip: null_flip, exception: exception, filter: filter, quicklook: quicklook,
        brightness: brightness, contrast: contrast, interpolation: interpolation].sort() { it.key }
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
    return ( time ) ? time.split( "," ) : []
  }

  def getBounds()
  {
    def result = null
    if ( bbox )
    {
      try
      {
        def (minx, miny, maxx, maxy) = bbox.split( "," )?.collect { it as double }

        result = [
            minx: minx,
            miny: miny,
            maxx: maxx,
            maxy: maxy,
            width: width,
            height: height
        ]
      }
      catch ( Exception e )
      {
        result = null
      }
    }
    result
  }

  def getDateRange()
  {
    def result = []
    def dates = this.dates

    if ( dates )
    {
      ( 0..<dates.size() ).each {
        def range = ISO8601DateParser.getDateRange( dates[it] )

        if ( range.size() > 0 )
        {
          result.add( range[0] )
          if ( range.size() > 1 )
          {
            result.add( range[1] )
          }
        }
      }
    }

    return result
  }

  def getBackgroundColor()
  {
    def result = new Color( 0, 0, 0 )
    if ( bgcolor )
    {
      if ( bgcolor.size() == 8 )
      {
        // skip 0x
        result = new Color( Integer.decode( "0x" + bgcolor[2] + bgcolor[3] ),
            Integer.decode( "0x" + bgcolor[4] + bgcolor[5] ),
            Integer.decode( "0x" + bgcolor[6] + bgcolor[7] ) )
      }
    }

    return result
  }

  def getTransparentFlag()
  {
    def result = false;
    if ( transparent )
    {
      result = Boolean.toBoolean( transparent )
    }
    return result
  }

//  def getFormat()
//  {
//    switch ( format?.toLowerCase() )
//    {
//    case "jpeg":
//    case "jpg":
//    case "image/jpeg":
//    case "image/jpg":
//      if ( transparent )
//      {
//        format = "image/png"
//      }
//      else
//      {
//        format = "image/jpeg"
//      }
//      break
//    case "png":
//    case "image/png":
//      format = "image/png"
//      break
//    case "gif":
//    case "image/gif":
//      format = "image/gif"
//      break
//    default:
//      format = "image/png"
//      break
//    }
//
//    format
//  }
}