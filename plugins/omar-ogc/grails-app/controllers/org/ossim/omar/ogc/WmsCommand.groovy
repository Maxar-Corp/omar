package org.ossim.omar.ogc

import java.awt.Color
import org.ossim.omar.core.ISO8601DateParser

/**
 * Created by IntelliJ IDEA.
 * User: gpotts
 * Date: 5/23/11
 * Time: 1:27 PM
 * To change this template use File | Settings | File Templates.
 */
@grails.validation.Validateable
class WmsCommand
{
  String bbox
  String width
  String height
  String format
  String layers
  String styles = ""
  String srs
  String service
  String version
  String request
  String transparent
  String bgcolor
  String time
  String stretch_mode
  String stretch_mode_region
  String sharpen_mode
  String sharpen_width
  String sharpen_sigma
  String rotate
  String quicklook
  String null_flip
  String exceptions
  String bands
  String filter
  String brightness
  String contrast
  String interpolation

  // getfeatureinfo additions
  String x
  String y
  String query_layers
  String info_format
  String feature_count


  static constraints = {
    bbox(nullable: true,validator: {val, obj ->
      def message = true
      def requestLowerCase =  obj.request?.toLowerCase()
      if ( (requestLowerCase == "getmap") ||
           (requestLowerCase == "getfeatureinfo" ))
      {
        if ( !val )
        {
          message = "BBOX parameter not found.  This is a required parameter."
        }
        else
        {
          def box = val.split(",");
          if ( box.length != 4 )
          {
            message = "BBOX parameter invalid.  Must be formatted with 4 parameters separated by commas and matching the form minx,miny,maxx,maxy in \n"
            message += "the units of the srs code"
          }
          else
          {
            try
            {
              Double.parseDouble(box[0])
              Double.parseDouble(box[1])
              Double.parseDouble(box[2])
              Double.parseDouble(box[3])
            }
            catch (Exception e)
            {
              message = "BBOX parameter invalid. 1 or more of the paramters is an invalid decimal number."
            }
          }
        }
      }
      else
      {

      }
      message
    }
    )
    width(nullable: true,validator: {val, obj ->
      def message = true
      def tempRequest = obj.request?.toLowerCase()
      if ( tempRequest == "getmap")
      {
        if ( !val )
        {
          message = "WIDTH parameter not found.  You are required to specify WIDTH, HEIGHT."
        }
        if ( val )
        {
          try
          {
            def test = Integer.parseInt(val)
            if ( test < 1 )
            {
              message = "WIDTH parameter invalid.  WIDTH is smaller than 1"
            }
          }
          catch (Exception e)
          {
            message = "WIDTH parameter invalid.  The tested value is not a number or exceeds the range of an integer."
          }
        }
      }
      message
    })
    height(nullable: true,validator: {val, obj ->
      def message = true
      def tempRequest = obj.request?.toLowerCase()
      if (tempRequest == "getmap")
      {
        if ( !val )
        {
          message = "HEIGHT parameter not found.  You are required to specify WIDTH, HEIGHT."
        }
        if ( val )
        {
          try
          {
            def test = Integer.parseInt(val)
            if ( test < 1 )
            {
              message = "HEIGHT parameter invalid.  HEIGHT is smaller than 1"
            }
          }
          catch (Exception e)
          {
            message = "HEIGHT parameter invalid.  The tested value is not a number or exceeds the range of an integer."
          }
        }
      }
      message
    })
    format(nullable: true,validator: {val, obj ->
      def message = true
      if ( obj.request?.toLowerCase() == "getmap" )
      {
        if ( !val )
        {
          message = "FORMAT parameter not found.  This is a required parameter."
        }
        else
        {
          def formatTemp = val.toLowerCase()
          if ( !(formatTemp == "image/png" ||
                  formatTemp == "image/jpeg" ||
                  formatTemp == "image/gif") )
          {
            message = "FORMAT parameter invalid.  Values can only be image/jpeg, image/png, or image/gif"
          }
        }
      }
      message
    })
    layers(nullable: true,validator: {val, obj ->
      def message = true
      if ( obj.request?.toLowerCase() == "getmap" )
      {
        if ( !val )
        {
          message = "LAYERS parameter not found.  This is a required parameter."
        }
      }
      message
    })
    styles(nullable: true)//,validator: {val, obj ->
      //def message = true
      //if ( val == null )
      //{
      //  message = "STYLES parameter not found.  This is a required parameter."
      //}
      //message
    //})
    srs(nullable: true,validator: {val, obj ->
      def message = true
      if ( (obj.request?.toLowerCase() == "getmap") ||
           (obj.request?.toLowerCase() == "getfeatureinfo" ))
      {
        if ( !val )
        {
          message = "SRS parameter not found.  This is a required parameter."
        }
        else
        {
          if ( val.toLowerCase().trim() != "epsg:4326" )
          {
            message = "SRS parameter ${val} not supported.  We only support value of EPSG:4326."
          }
        }
      }
      message
    })
    service(nullable: true, validator: {val, obj ->
      true
    })
    version(nullable: true,validator: {val, obj ->
      true
    })
    request(validator: {val, obj ->
      def message = true
      if ( !val )
      {
        message = "REQUEST parameter not found.  Values can be getmap, getcapabilities"
      }
      else if ( !(val.toLowerCase() in ["getmap", "getfeatureinfo", "getcapabilities", "getkml", "getkmz"]) )
      {
        message = "REQUEST parameter ${val} is not valid, value can only be GetMap, GetFeatureInfo, GetCapabilities or GetKml."
      }
      message
    })
    transparent(nullable: true)
    bgcolor(nullable: true,
      validator: {val, obj ->
      def message = true
      if ( val )
      {
        if ( obj.request?.toLowerCase() == "getmap" )
        {
          if ( !val.startsWith("0x") )
          {
            message = "BGCOLOR parameter invalid.  Value must start with 0x"
          }
          else if ( val.size() != 8 )
          {
            message = "BGCOLOR parameter invalid.  Value must be 8 characters long: example 0xFFFFFF is a white background"
          }
          else
          {
            try
            {
              Integer.decode("0x" + val[2] + val[3])
              Integer.decode("0x" + val[4] + val[5])
              Integer.decode("0x" + val[6] + val[7])
            }
            catch (Exception e)
            {
              message = "BGCOLOR parameter invalid.  Individual values are not valid hex range of 00-FF"
            }
          }
        }
      }
      message
    })
    time(nullable: true,
      validator: {val, obj ->
      def message = true
      if ( val )
      {

      }
      message
    })
    stretch_mode(nullable: true,
            validator: {val, obj ->
      def message = true
      if ( val )
      {
      }
      message
    })
    stretch_mode_region(nullable: true,
            validator: {val, obj ->
      def message = true
      if ( obj.request?.toLowerCase() == "getmap" )
      {
        if ( val )
        {
          if ( !(val.toLowerCase() in ["global", "viewport"]) )
          {
            message = "STRETCH_MODE_REGION parameter invalid.  Values can be global or viewport"
          }
        }
      }
      message
    })
    rotate(nullable: true,
           validator: {val, obj ->
      def message = true
      if ( obj.request?.toLowerCase() == "getmap" )
      {
        if ( val )
        {
          try
          {
            Double.parseDouble(val)
          }
          catch (Exception e)
          {
            message = "ROTATE parameter invalid.  Value does not appear to be a valid floating number."
          }
        }
      }
      message
    })
    quicklook(nullable:  true)
    null_flip(nullable:  true)
    exceptions(nullable:  true)
    bands(nullable:  true)
    filter(nullable:  true)
    brightness(nullable:  true)
    contrast(nullable:  true)
    interpolation(nullable:  true)
    x(nullable:  true, validator: { val, obj->
        def message = true
        if ( obj.request?.toLowerCase() == "getfeatureinfo" )
        {
        }

        message
     })
    y(nullable:  true)
    query_layers(nullable: true,
            validator:  {val, obj ->
        def message = true

        if ( obj.request?.toLowerCase() == "getfeatureinfo" )
        {
        }

        message
    })
    info_format(nullable: true)
    feature_count(nullable: true)
  }

  def toMap()
  {
    return [bbox: bbox, width: width, height: height, format: format, layers: layers, srs: srs, service: service,
            version: version, request: request, transparent: transparent, bgcolor: bgcolor, styles: styles,
            stretch_mode: stretch_mode, stretch_mode_region: stretch_mode_region, sharpen_mode: sharpen_mode,
            sharpen_width: sharpen_width, sharpen_sigma: sharpen_sigma, rotate: rotate,
            time: time, null_flip: null_flip, bands: bands, exceptions: exceptions, filter: filter,
            quicklook: quicklook, brightness: brightness, contrast: contrast, interpolation: interpolation].sort { it.key }
  }

  def customParametersToMap()
  {
    [bands: bands, stretch_mode: stretch_mode, stretch_mode_region: stretch_mode_region, sharpen_mode: sharpen_mode,
            sharpen_width: sharpen_width, sharpen_sigma: sharpen_sigma, rotate: rotate,
            time: time, null_flip: null_flip, exceptions: exceptions, filter: filter, quicklook: quicklook,
            brightness: brightness, contrast: contrast, interpolation: interpolation].sort() {it.key}
  }

  public String toString()
  {
    return toMap()
  }

  String[] getDates()
  {
    return (time) ? time.split(",") : []
  }

  def getBounds()
  {
    def result = null
    if ( bbox )
    {
      def splitBbox = bbox.split(",")
      try
      {
        def minx = splitBbox[0] as Double
        def miny = splitBbox[1] as Double
        def maxx = splitBbox[2] as Double
        def maxy = splitBbox[3] as Double
        def w = width
        def h = height
        result = [minx: minx,
                miny: miny,
                maxx: maxx,
                maxy: maxy,
                width: w as Integer,
                height: h as Integer]
      }
      catch (Exception e)
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

  def createErrorPairs()
  {
    def result = [[:]]
    errors?.each {err ->
      def field = "${err.fieldError.arguments[0]}"
      def code = err.getFieldError(field)?.code
      result << [field: field, code: code]
    }
    result
  }

  def createErrorString()
  {
    def errorString = ""
    def errorPairs = createErrorPairs()
    errorPairs.each {pair ->
      if ( pair.code )
      {
          errorString +=  ("${pair.field}: ${pair.code}\n")
      }
    }

    errorString
  }
}
