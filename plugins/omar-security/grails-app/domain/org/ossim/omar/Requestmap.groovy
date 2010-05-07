package org.ossim.omar
/**
 * Domain class for Request Map.
 */
class Requestmap {

	String url
	String configAttribute

	static constraints = {
		url(blank: false, unique: true)
		configAttribute(blank: false)
	}

  static mapping = {
    columns {
      url index: 'requestmap_url_idx'
    }
  }
}
