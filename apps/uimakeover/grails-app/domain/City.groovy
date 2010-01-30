class City
{
  String name
  Integer population
  Boolean capital
  Double latitude
  Double longitude

  static belongsTo = [country: Country]

  static constraints = {
    name()
    population()
    capital()
    latitude()
    longitude()
    country()
  }
}
