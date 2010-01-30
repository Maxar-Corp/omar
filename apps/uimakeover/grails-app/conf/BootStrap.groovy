class BootStrap
{

  def init = {servletContext ->
    def citiesCsvFile = new File("cities.csv")
    def records = citiesCsvFile.readLines()

    for ( x in (1..(records.size())) )
    {
      def record = records[x]?.split(',')


      if ( record )
      {
        //println record

        def cityName = record[0]
        def countryName = record[1]
        def population = record[2] as Integer
        def capital = (record[3] == 'Y') ? true : false
        def longitude = record[4] as Double
        def latitude = record[5] as Double

        def country = Country.findByName(countryName)

        if ( !country )
        {
          country = new Country(name: countryName).save()
        }

        def city = new City(
            name: cityName,
            population: population,
            capital: capital,
            latitude: latitude,
            longitude: longitude
        )

        country.addToCities(city)
      }
    }
  }

  def destroy = {
  }
} 