package org.ossim.omar

class WmsLayers
{
  String url
  String name
  Map params = [:]
  Map options = [:]

  static constraints = {
    name(blank: false)
    url(blank: false)
    params(nullable: true)
    options(nullable: true)
  }
}
