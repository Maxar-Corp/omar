import org.apache.commons.collections.map.CaseInsensitiveMap

class Utility
{
  static void removeEmptyParams(def params)
  {
    def nullMap = params?.findAll { entry ->(entry.value == "" || entry.value == "null")}
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

    return tempParams.subMap(["version","request","layers","styles",
                              "srs","crs","bbox", "width", "height", "format",
                              "transparent", "bgcolor", "exceptions", "time",
                              "elevation", "updatesequence", "query_layers",
                              "info_format", "i", "j"
                              ])
  }
  static Map keepOnlyGetMapWMSParams(def map, def additionalParamList)
  {
    def tempParams = new CaseInsensitiveMap()
    map.each { tempParams.put(it.key, it.value)}
    def listOfParams = ["version","request","layers","styles",
                              "srs","bbox", "width", "height", "format",
                              "transparent", "bgcolor", "exceptions", "time",
                              "elevation"
                              ]
    if(additionalParam)
    {
      additionalParamters.each{
        listOfParams.add(it) 
      }
    }

    return tempParams.subMap()
  }
}