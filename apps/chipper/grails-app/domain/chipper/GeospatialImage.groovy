package chipper

import com.vividsolutions.jts.geom.Polygon
import org.hibernatespatial.GeometryUserType

class GeospatialImage
{
  String filename
  String entry
  Polygon geometry

  static constraints = {
    filename()
    geometry()
  }

  static mapping = {
    geometry type: GeometryUserType
  }
}
