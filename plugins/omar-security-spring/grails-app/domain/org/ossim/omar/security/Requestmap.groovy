package org.ossim.omar.security

class Requestmap
{

  String url
  String configAttribute

  static mapping = {
    cache true
    columns {
      url index: 'requestmap_url_idx'
    }
  }

  static constraints = {
    url blank: false, unique: true
    configAttribute blank: false
  }
}
