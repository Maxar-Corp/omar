package org.ossim.omar.ogc

import org.ossim.omar.core.ISO8601DateParser

//import org.codehaus.groovy.grails.validation.Validateable
import grails.validation.Validateable
/**
 * Created by IntelliJ IDEA.
 * User: gpotts
 * Date: 5/16/11
 * Time: 11:02 AM
 * To change this template use File | Settings | File Templates.
 */
@Validateable
class WcsCommand
{
  static final def OUTPUT_FORMATS = ["jpeg", "image/jpeg", "png", "image/png", "png_uint8",
          "image/tiff", "geotiff", "geotiff_uint8", "geojp2_uint8",
          "geojp2"]
  String bbox
  String width
  String height
  String depth
  String resx
  String resy
  String resz
  String format
  String coverage
  String crs
  String response_crs
  String service
  String version
  String request
  String stretch_mode
  String stretch_mode_region
  String sharpen_mode
  String sharpen_width
  String sharpen_sigma
  String quicklook
  String null_flip
  String exceptions
  String bands
  String time
  String interpolation
  String filter
  String max
  String brightness
  String contrast

  static constraints = {
    bbox( validator: {val, obj ->
      def message = true
      if ( obj.request?.toLowerCase() == "getcoverage" )
      {
        if ( !val )
        {
          message = "BBOX parameter not found.  This is a required parameter."
        }
        else
        {
          def box = val.split( "," );
          if ( box.length != 4 )
          {
            message = "BBOX parameter invalid.  Must be formatted with 4 parameters separated by commas and matching the form minx,miny,maxx,maxy in \n"
            message += "the units of the crs code"
          }
          else
          {
            try
            {
              Double.parseDouble( box[0] )
              Double.parseDouble( box[1] )
              Double.parseDouble( box[2] )
              Double.parseDouble( box[3] )
            }
            catch ( Exception e )
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
    width( validator: {val, obj ->
      def message = true
      if ( obj.request?.toLowerCase() == "getcoverage" )
      {
        if ( ( !val ) && ( ( !obj.resx ) && ( !obj.resy ) ) )
        {
          message = "WIDTH parameter not found.  You are required to specify WIDTH, HEIGHT pair or RESX, RESY pair."
        }
        if ( val )
        {
          try
          {
            Integer.parseInt( val )
          }
          catch ( Exception e )
          {
            message = "WIDTH parameter invalid.  The tested value is not a number or exceeds the range of an integer."
          }
        }
      }
      message
    } )
    height( validator: {val, obj ->
      def message = true
      if ( obj.request?.toLowerCase() == "getcoverage" )
      {
        if ( ( !val ) && ( ( !obj.resx ) && ( !obj.resy ) ) )
        {
          message = "HEIGHT parameter not found.  You are required to specify WIDTH, HEIGHT pair or RESX, RESY pair."
        }
        if ( val )
        {
          try
          {
            Integer.parseInt( val )
          }
          catch ( Exception e )
          {
            message = "HEIGHT parameter invalid.  The tested value is not a number or exceeds the range of an integer."
          }
        }
      }
      message
    } )
    depth( nullable: true )
    resx( nullable: true, validator: {val, obj ->
      def message = true
      if ( obj.request?.toLowerCase() == "getcoverage" )
      {
        if ( val )
        {
          try
          {
            Double.parseDouble( val )
          }
          catch ( Exception e )
          {
            message = "RESX parameter invalid. The value specified is not a valid decimal number."
          }
        }
        else if ( ( !obj.width ) && ( !obj.height ) )
        {
          message = "RESX parameter not found.  You are required to specify WIDTH, HEIGHT pair or RESX, RESY pair."
        }
      }
      message
    } )
    resy( nullable: true,
            validator: {val, obj ->
              def message = true
              if ( obj.request?.toLowerCase() == "getcoverage" )
              {
                if ( val )
                {
                  try
                  {
                    Double.parseDouble( val )
                  }
                  catch ( Exception e )
                  {
                    message = "RESY parameter invalid. The value specified is not a valid decimal number."
                  }
                }
                else if ( ( !obj.width ) && ( !obj.height ) )
                {
                  message = "RESY parameter not found.  You are required to specify WIDTH, HEIGHT pair or RESX, RESY pair."
                }
              }
              message
            } )
    resz( nullable: true )
    format( validator: {val, obj ->
      def message = true
      if ( obj.request?.toLowerCase() == "getcoverage" )
      {
        if ( !val )
        {
          message = "FORMAT parameter not found.  This is a required parameter."
        }
        else if ( !( val.toLowerCase() in OUTPUT_FORMATS ) )
        {
          message = "FORMAT parameter ${val} not supported.  Values can only be ${OUTPUT_FORMATS.join( ', ' )}"
        }
      }

      message
    } )
    coverage( validator: {val, obj ->
      def message = true
      if ( obj.request?.toLowerCase() == "getcoverage" )
      {
        if ( !val )
        {
          message = "COVERAGE parameter not found.  This is a required parameter."
        }
      }
      message
    } )
    crs( validator: {val, obj ->
      def message = true
      if ( obj.request?.toLowerCase() == "getcoverage" )
      {
        if ( !val )
        {
          message = "CRS parameter not found.  This is a required parameter."
        }
        else
        {
          if ( val.toLowerCase().trim() != "epsg:4326" )
          {
            message = "CRS parameter ${val} not supported.  We only support value of EPSG:4326"
          }
        }
      }
      message
    } )
    response_crs(
            nullable: true,
            validator: {val, obj ->
              def message = true
              if ( obj.request?.toLowerCase() == "getcoverage" )
              {
                if ( val && ( val != obj.crs ) )
                {
                  message = "RESPONSE_CRS is specified and is not equal to the CRS parameter.  We currently do not support changing the response crs."
                }
              }
              message
            } )
    service( nullable: true,
            validator: {val, obj ->
              true
            } )
    version( nullable: true,
            validator: {val, obj ->
              true
            } )
    request( validator: {val, obj ->
      def message = true

      if ( !val )
      {
        message = "REQUEST parameter not found.  Values can be getcoverage"
      }
      //           else if(!(val.toLowerCase() in ["getcoverage", "getcapabilities", "describecoverage"]))
      else if ( val.toLowerCase() in ["getcapabilities", "describecoverage"] )
      {
        message = "REQUEST parameter ${val} is currently not supported, value can only be getcoverage"
      }
      else if ( val.toLowerCase() != "getcoverage" )
      {
        message = "REQUEST parameter ${val} is invalid, value can only be getcoverage"
      }
      message
    } )
    stretch_mode( nullable: true )
    stretch_mode_region( nullable: true )
    sharpen_mode( nullable: true )
    sharpen_width( nullable: true )
    sharpen_sigma( nullable: true )
    quicklook( nullable: true )
    null_flip( nullable: true )
    exceptions( nullable: true )
    bands( nullable: true )
    time( nullable: true )
    interpolation( nullable: true )
    filter( nullable: true )
    max( nullable: true )
    brightness( nullable: true )
    contrast( nullable: true )
  }

  def toMap( )
  {
    return [bbox: bbox, bands: bands, width: width as Integer, height: height as Integer,
            format: format, resx: resx as Double, resy: resy as Double,
            coverage: coverage, crs: crs, response_crs: response_crs, service: service,
            version: version, request: request, stretch_mode: stretch_mode, interpolation: interpolation,
            stretch_mode_region: stretch_mode_region, sharpen_mode: sharpen_mode,
            sharpen_width: sharpen_width as Double, sharpen_sigma: sharpen_sigma as Double,
            time: time, null_flip: null_flip, exceptions: exceptions, filter: filter,
            quicklook: quicklook, max: max, brightness: brightness, contrast: contrast].sort { it.key }
  }

  String[] getDates( )
  {
    return ( time ) ? time.split( "," ) : []
  }

  def getBounds( )
  {
    def result = null
    if ( bbox )
    {
      def splitBbox = bbox.split( "," )
      try
      {
        def minx = splitBbox[0] as Double
        def miny = splitBbox[1] as Double
        def maxx = splitBbox[2] as Double
        def maxy = splitBbox[3] as Double
        def w = width
        def h = height
        if ( ( !( w && h ) ) && ( resx && resy ) )
        {
          def rx = resx as Double
          def ry = resy as Double
          w = ( maxx - minx ) / rx
          h = ( maxy - miny ) / ry
        }
        result = [minx: minx,
                miny: miny,
                maxx: maxx,
                maxy: maxy,
                width: w as Integer,
                height: h as Integer]
      }
      catch ( Exception e )
      {
        result = null
      }
    }
    result
  }

  def getDateRange( )
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

  def createErrorPairs( )
  {
    def result = [[:]]
    errors?.each {err ->
      def field = "${err.fieldError.arguments[0]}"
      def code = err.getFieldError( field )?.code
      result << [field: field, code: code]
    }
    result
  }

  def createErrorString( )
  {
    def errorString = ""
    def errorPairs = createErrorPairs()
    errorPairs.each {pair ->
      if ( pair.code )
      {
        errorString += ( "${pair.field}: ${pair.code}\n" )
      }
    }

    errorString
  }
}
