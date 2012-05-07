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
      name index: 'city_name_idx'
      country index: 'city_country_idx'
      population index: 'city_population_idx'
      groundGeom type: org.hibernatespatial.GeometryUserType
    }
  }
}
