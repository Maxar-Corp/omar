package org.ossim.omar.ogc.wfs

/**
 * Created with IntelliJ IDEA.
 * User: sbortman
 * Date: 2/25/13
 * Time: 9:14 AM
 * To change this template use File | Settings | File Templates.
 */
interface ResultFormat
{
  def getFeature(def WfsRequest, def workspace)
}
