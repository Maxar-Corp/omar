package org.ossim.omar.chipper

import grails.validation.Validateable
import groovy.transform.ToString

/**
 * Created with IntelliJ IDEA.
 * User: sbortman
 * Date: 8/9/13
 * Time: 2:10 PM
 * To change this template use File | Settings | File Templates.
 */
@ToString( )
@Validateable( )
class ChipCommand
{
  String service
  String version
  String request
  // WMS Parameters
  Boolean transparent

  Integer height
  Integer width

  String bbox
  String format
  String layers
  String srs
  String styles


  static constraints = {
    bbox( nullable: false, validator: { val, obj ->
      def message = true
      def requestLowerCase = obj.request?.toLowerCase()
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
          message += "the units of the srs code"
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
            message = "BBOX parameter invalid. 1 or more of the parameters is an invalid decimal number."
          }
        }
      }
       message
    }
    )
    width( nullable: true, validator: { val, obj ->
      def message = true
      def tempRequest = obj.request?.toLowerCase()
      if ( val == null )
      {
        message = "WIDTH parameter not found.  You are required to specify WIDTH, HEIGHT."
      }
      else
      {
        try
        {
          if ( val < 1 )
          {
            message = "WIDTH parameter invalid.  WIDTH is smaller than 1"
          }
        }
        catch ( Exception e )
        {
          message = "WIDTH parameter invalid.  The tested value is not a number or exceeds the range of an integer."
        }
      }
      message
    } )
    height( nullable: false, validator: { val, obj ->
      def message = true
      def tempRequest = obj.request?.toLowerCase()
      if ( val == null )
      {
        message = "HEIGHT parameter not found.  You are required to specify WIDTH, HEIGHT."
      }
      else
      {
        try
        {
          if ( val < 1 )
          {
            message = "HEIGHT parameter invalid.  HEIGHT is smaller than 1"
          }
        }
        catch ( Exception e )
        {
          message = "HEIGHT parameter invalid.  The tested value is not a number or exceeds the range of an integer."
        }
      }
      message
    } )
    srs(nullable: false, validator: { val, obj ->
      def message = true

      if(!val)
      {
        message = "SRS parameter not found.  You are required to specify SRS."
      }
      else
      {
        // need to add the format test here and is it of the form EPSG:<number>
      }

      message
    })
  }

  def toMap()
  {
    return [bbox: bbox, width: width,
            height: height, format: format,
            layers: layers, srs: srs, service: service,
            version: version, request: request,
            styles:styles]
  }
  def createErrorPairs()
  {
    def result = [[:]]
    errors?.each { err ->
      def field = "${err.fieldError.arguments[0]}"
      def code = err.getFieldError( field )?.code
      result << [field: field, code: code]
    }
    result
  }

  def createErrorString()
  {
    def errorString = ""
    def errorPairs = createErrorPairs()
    errorPairs.each { pair ->
      if ( pair.code )
      {
        errorString += ( "${pair.field}: ${pair.code}\n" )
      }
    }

    errorString
  }
}
