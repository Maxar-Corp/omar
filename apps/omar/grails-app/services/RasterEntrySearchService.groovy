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

    // HACK to force eager loading
    switch ( grailsApplication.config.rasterEntry.queryObject )
    {
      case "metadataXml":
        rasterEntries?.each {
          it.metadataTags?.size()
          it.rasterDataSet?.fileObjects?.size()
        }
        break
      case "metadata":
        rasterEntries?.each {
          it.mainFile
        }
        break
    }

    return rasterEntries
  }


  Map<String, Object> method3(RasterEntryQuery rasterEntryQuery, Map<String, String> params)
  {

    def x = {
      createAlias("rasterEntry", "r")
      if ( rasterEntryQuery?.groundGeom )
      {
        addToCriteria(rasterEntryQuery.createIntersection("r.groundGeom"))
      }
      if ( rasterEntryQuery?.startDate || rasterEntryQuery?.endDate )
      {
        addToCriteria(rasterEntryQuery.createDateRange("r.acquisitionDate"))
      }
      if ( params?.max )
      {
        maxResults(params.max as Integer)
      }
//      if ( params?.sort && params.order )
//      {
//        order(params?.sort, params?.order)
//      }
      rasterEntryQuery.searchTagNames?.size()?.times {i ->
        if ( rasterEntryQuery.searchTagNames[i] && rasterEntryQuery.searchTagValues[i] )
        {
          ilike(rasterEntryQuery.searchTagNames[i], "%${rasterEntryQuery.searchTagValues[i]}%")
        }
      }
    }

    def metadata = RasterEntryMetadata.withCriteria(x)
    def c = RasterEntryMetadata.createCriteria()
    //def metadata =  c.get( x )
    //def metadata =  c.list( params, x )

/*

    def count = RasterEntryMetadata.withCriteria {
        projections { countDistinct("id") }
        createAlias("rasterEntry", "r")
        addToCriteria( new IntersectsExpression( "r.groundGeom", groundGeom ) )
    }


    def count = RasterEntry.withCriteria {
        projections { countDistinct("id") }
        createAlias("metadata", "m")
        addToCriteria( new IntersectsExpression( "groundGeom", groundGeom ) )
    }
*/


    def foo = metadata?.collect {it.rasterEntry}

    foo?.each { it.mainFile }

    return [count: c.count(x), rasterEntries: foo]


  }
}
