package cstars.footprints

import com.vividsolutions.jts.geom.Geometry
import com.vividsolutions.jts.io.WKTReader


class ImageFootprint
{
  Geometry groundGeom

  static belongsTo = [parent: ImageCollection]

  static constraints = {
  }

  static mapping = {
    groundGeom type: org.hibernatespatial.GeometryUserType
  }
}
