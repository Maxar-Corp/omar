class Country
{
  String name

  static hasMany = [cities: City]

  static constraints = {
    name()
    cities()
  }
}
