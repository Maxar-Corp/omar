package org.ossim.omar.raster

import grails.validation.Validateable
import groovy.transform.ToString
import org.grails.databinding.BindUsing

/**
 * Created by sbortman on 7/6/15.
 */
@ToString( includeNames = true )
@Validateable
@BindUsing( GetMapRequestBindingHelper )
class GetMapRequest
{
  String service
  String version
  String request

  Integer width
  Integer height
  String format
  Boolean transparent

  String layers
  String styles

  String srs
  String bbox

  String filter
}
