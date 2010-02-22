/**
 * Created by IntelliJ IDEA.
 * User: sbortman
 * Date: Jun 6, 2008
 * Time: 5:00:09 PM
 * To change this template use File | Settings | File Templates.
 */

class Geometry implements Serializable
{
  org.postgis.Geometry geom

  public String toString()
  {
    return geom.toString()
  }

  public static Geometry fromString(String s)
  {
    return new Geometry(geom: org.postgis.PGgeometry.geomFromString(s))
  }

  def getBounds()
  {
    def pts = []

    (0..<geom.numPoints()).each { pts << geom.getPoint(it) }

    def bounds = [
        minLon: pts.x.min(),
        minLat: pts.y.min(),
        maxLon: pts.x.max(),
        maxLat: pts.y.max()
    ]

    return bounds
  }

  public static def createPolygon(def minLon, def minLat, def maxLon, def maxLat)
  {
    return "POLYGON((${minLon} ${minLat}, ${minLon} ${maxLat}, ${maxLon} ${maxLat}, ${maxLon} ${minLat}, ${minLon} ${minLat}))"
  }


  public static def createPoint(def lon, def lat)
  {
    return "POINT(${lon} ${lat})"
  }


  def getWKT() {
    def buffer = new StringBuffer()
    geom?.outerWKT(buffer)
    return buffer.toString()
  }

  def getSRS() {
    return geom?.getSrid()
  }
}