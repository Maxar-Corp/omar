package org.ossim.omar.ogc

import com.vividsolutions.jts.geom.Geometry

/**
 * This domain holds the logging for all Wms requests.
 *
 * Units for the internalTime, renderTime and totalTime
 * are all in seconds.
 * 
 */
class WmsLog {

  Long   width
  Long   height
  String layers
  String styles
  String format
  String request
  String bbox
  Double internalTime
  Double renderTime
  Double totalTime
  Date   startDate
  Date   endDate
  String userName
  String ip
  String url
  Double meanGsd
  Geometry geometry

  static constraints =
  {
    width(nullable:true)
    height(nullable:true)
    layers(nullable:true)
    styles(nullable:true)
    format(nullable:true)
    request(nullable:true)
    bbox(nullable:true)
    internalTime(nullable:true)
    renderTime(nullable:true)
    totalTime(nullable:true)
    startDate(nullable:true)
    endDate(nullable:true)
    userName(nullable:true)
    ip(nullable:true)
    url(nullable:true)
    meanGsd(nullable:true)
    geometry(nullable:true)
  }
  static mapping = {
    version false
    columns
    {
      url type:'text'
      layers type: 'text'
      geometry type: org.hibernatespatial.GeometryUserType
    }
  }

}
