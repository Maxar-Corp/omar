package chipper

import com.vividsolutions.jts.geom.Polygon
import org.hibernatespatial.GeometryUserType

class GeospatialImage
{
  String filename
  String entry
  Polygon geometry
  Date acquisitionDate
  String mission
  String sensor
  String fileType
  Integer width
  Integer height
  Integer numBands
  Integer numResLevels
  String dataType

  static constraints = {
    filename()
    entry()
    geometry()
    acquisitionDate( nullable: true )
    mission( nullable: true )
    sensor( nullable: true )
    fileType( nullable: true )
    numBands( nullable: true )
    dataType( nullable: true )
    numResLevels( nullable: true )
    width( nullable: true )
    height( nullable: true )
  }

  static mapping = {
    geometry type: GeometryUserType
  }
}
