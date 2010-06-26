package cstars.footprints

class ImageCollection
{
  Date timeStamp
  Sensor sensor

  static hasMany = [footprints: ImageFootprint]

  static constraints = {    
  }
}
