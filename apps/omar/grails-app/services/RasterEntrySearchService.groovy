//import javax.jws.WebParam


class RasterEntrySearchService
{
  def grailsApplication

  //static expose = ['xfire']

  static transactional = false

  List<RasterEntry> runQuery(
  /*@WebParam (name = "rasterEntryQuery", header = true)*/
  RasterEntryQuery rasterEntryQuery,
  /*@WebParam (name = "params", header = true)*/
  Map<String, String> params)
  {
    def clause = rasterEntryQuery.createClause()

    def rasterEntries = RasterEntry.createCriteria().list(params) {
      def searches = [:]

      rasterEntryQuery?.searchTagNames?.eachWithIndex {name, i ->
        searches[name] = rasterEntryQuery?.searchTagValues[i]
      }

      // This may be true of site, but not necessarily outside
      //isNotNull("acquisitionDate")



      switch ( grailsApplication.config.rasterEntry.queryObject )
      {
        case "metadataXml":
          createAlias("metadataXml", "m")

          searches?.each {name, value ->

            String namevalue

            switch ( name )
            {
              case "custom":
                def pair = value?.split("=");

                if ( pair?.size() == 2 )
                {
                  name = pair[0].trim()
                  value = pair[1].trim()
                  namevalue = "%<${name}>%${value}%</${name}>%" as String
                }

                break
              default:
                namevalue = "%<${name}>%${value}%</${name}>%" as String
                break
            }

            if ( name && value && name != "null" )
            {
              ilike("m.namevalue", namevalue)
            }
          }
          break
        case "metadata":
          createAlias("metadata", "m")
          searches?.each {name, value ->
            if ( name && value )
            {
              ilike("m.${name}", "%${value}%")
            }
          }
          break
      }

      if ( clause )
      {
        addToCriteria(clause)
      }
    }

    switch ( grailsApplication.config.rasterEntry.queryObject )
    {
      case "metadataXml":
        // HACK to force eager loading
        rasterEntries?.each {
          it.metadataTags?.size()
          it.rasterDataSet?.fileObjects?.size()
        }
        break
    }

    return rasterEntries
  }
}
