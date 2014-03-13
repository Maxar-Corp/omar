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
  // WMS Parameters
  Boolean transparent

  Integer height
  Integer width

  String bbox
  String format
  String layers
  String request
  String service
  String srs
  String styles
  String version

  /*
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
      if ( !val )
      {
        message = "WIDTH parameter not found.  You are required to specify WIDTH, HEIGHT."
      }
      if ( val )
      {
        try
        {
          def test = Integer.parseInt( val )
          if ( test < 1 )
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
    height( nullable: true, validator: { val, obj ->
      def message = true
      def tempRequest = obj.request?.toLowerCase()
      if ( !val )
      {
        message = "HEIGHT parameter not found.  You are required to specify WIDTH, HEIGHT."
      }
      if ( val )
      {
        try
        {
          def test = Integer.parseInt( val )
          if ( test < 1 )
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
  }
  */
}
