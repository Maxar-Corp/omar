package geodata

import com.vividsolutions.jts.geom.Coordinate
import com.vividsolutions.jts.geom.GeometryFactory
import com.vividsolutions.jts.geom.PrecisionModel

/**
 * Created by IntelliJ IDEA.
 * User: sbortman
 * Date: 5/2/11
 * Time: 7:09 PM
 * To change this template use File | Settings | File Templates.
 */
class CityData
{
  static def load()
  {
    def geometryFactory = new GeometryFactory(new PrecisionModel(), 4326)

    City.withTransaction {
      def istream = CityData.class.getResourceAsStream('cities.csv')

      istream?.toCsvReader([skipLines: 1]).eachLine {  tokens ->

        def city = new City()

        // CITY_NAME,COUNTRY,POP,CAP,LONGITUDE,LATITUDE

        city.with {
          name = tokens[0]
          country = tokens[1]
          population = tokens[2] as Integer
          capital = tokens[3] == 'Y' ? true : false
          longitude = tokens[4] as Double
          latitude = tokens[5] as Double
          groundGeom = geometryFactory.createPoint(new Coordinate(longitude, latitude))
        }

        city.save()
      }

      istream?.close()
    }
  }
}
