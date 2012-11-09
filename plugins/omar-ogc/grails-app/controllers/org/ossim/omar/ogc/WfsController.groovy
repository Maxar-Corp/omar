package org.ossim.omar.ogc

import org.apache.commons.collections.map.CaseInsensitiveMap

class WfsController
{
  def webFeatureService

  def index()
  {
    //println params

    def wfsParams = new CaseInsensitiveMap( params ).subMap(
        ['service', 'version', 'request', 'typeName', 'filter']
    )
    def wfsCommand = new WfsCommand()

    bindData( wfsCommand, wfsParams )

    //println wfsCommand

    def results

    switch ( wfsCommand.request?.toUpperCase() )
    {
    case "GETCAPABILITIES":
      results = webFeatureService.getCapabilities( wfsCommand )
      break
    case "DESCRIBEFEATURETYPE":
      results = webFeatureService.describeFeatureType( wfsCommand )
      break
    case "GETFEATURE":
      results = webFeatureService.getFeature( wfsCommand )
      break
    default:
      println "ERROR"
    }

    render contentType: 'application/xml', text: results
  }
}
