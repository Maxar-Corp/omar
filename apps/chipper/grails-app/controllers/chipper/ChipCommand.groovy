package chipper

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
@Validateable()
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


  // OSSIM Parameters
  String bands
  String entry
}
