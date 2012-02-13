package geodata

import com.vividsolutions.jts.geom.Point

class City
{
// CITY_NAME,COUNTRY,POP,CAP,LONGITUDE,LATITUDE

  String name
  String country
  Integer population
  Boolean capital
  Double longitude
  Double latitude

  Point groundGeom

  static constraints = {
    name()
    country()
    population()
    capital()
    longitude()
    latitude()
  }

  static mapping = {
    columns {
      groundGeom type: org.hibernatespatial.GeometryUserType
    }
  }
}
