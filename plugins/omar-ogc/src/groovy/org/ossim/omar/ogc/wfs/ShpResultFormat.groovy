package org.ossim.omar.ogc.wfs

/**
 * Created with IntelliJ IDEA.
 * User: sbortman
 * Date: 2/25/13
 * Time: 9:20 AM
 * To change this template use File | Settings | File Templates.
 */
class ShpResultFormat implements ResultFormat
{
  def name = "SHAPE-ZIP"
  def contentType = "application/octet-stream"

  def getFeature(def wfsRequest, def workspace)
  {
    ['', contentType]
  }
}
