package cstars.footprints

class Sensor
{
  String name

  static constraints = {
    name(unique: true)
  }
}
