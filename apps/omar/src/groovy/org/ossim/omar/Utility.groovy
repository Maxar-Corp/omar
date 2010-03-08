package org.ossim.omar

import org.apache.commons.collections.map.CaseInsensitiveMap

class Utility
{
  static void removeEmptyParams(def params)
  {
    def nullMap = params?.findAll {entry -> (entry.value == "" || entry.value == "null")}
    nullMap?.each {params?.remove(it.key)}
  }
  /**
   * This will extract all WMS paramters this includes GetMap, GetCapabilities, and
   * GetFeatureInfo REQUESTS.  It will do this in a case insensitive way and return
   * a new map
   */
  static Map keepOnlyWMSParams(def map)
  {
    def tempParams = new CaseInsensitiveMap()
    map.each { tempParams.put(it.key, it.value)}

    return tempParams.subMap(["version", "request", "layers", "styles",
        "srs", "crs", "bbox", "width", "height", "format",
        "transparent", "bgcolor", "exceptions", "time",
        "elevation", "updatesequence", "query_layers",
        "info_format", "i", "j"
    ])
  }

  static Map keepOnlyParams(def map, def params)
  {
    def tempParams = new CaseInsensitiveMap()
    map.each { tempParams.put(it.key, it.value)}
    def listOfParams = ["version", "request", "layers", "styles",
        "srs", "bbox", "width", "height", "format",
        "transparent", "bgcolor", "exceptions", "time",
        "elevation"
    ]
    if ( params )
    {
      params.each {
//        println "adding ${it}"
        listOfParams.add(it)
      }
    }

    return tempParams.subMap(listOfParams)
  }

  public static def parseSearchTag(def name, def value)
  {
    def nameValue = "${name}${value}"

    def pattern = /(.*\.)?(.*)=(.*)/
    def matcher = nameValue =~ pattern

    def results = [:]

    if ( matcher )
    {
      if ( matcher[0][1] )
      {
        results["property"] = matcher[0][1] - "."
        results["tag"] = matcher[0][2]
        results["content"] = matcher[0][3]
      }
      else
      {
        results["property"] = matcher[0][2]
        results["value"] = matcher[0][3]
      }
    }
    else
    {
      results["property"] = name
      results["value"] = value
    }

    return results
  }

}