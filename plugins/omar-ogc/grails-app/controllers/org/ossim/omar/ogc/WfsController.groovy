package org.ossim.omar.ogc

import org.apache.commons.collections.map.CaseInsensitiveMap


class WfsController
{
  def webFeatureService

  def index( )
  {
    def wfsParams = new CaseInsensitiveMap( params ).subMap( [
            'service', 'version', 'request',
            'typeName', 'filter',
            'max', 'offset', 'sort', 'order'
    ] )

    def xml = null
    switch ( wfsParams.request?.toLowerCase() )
    {
    case "getcapabilities":
      xml = webFeatureService.capabilities
      break
    case "describefeaturetype":
      xml = webFeatureService.describeFeatureType( wfsParams.typeName )
      break
    case "getfeature":
      def pagination = [
              max: wfsParams.max?.toInteger() ?: 10,
              offset: wfsParams.offset?.toInteger() ?: 0,
              sort: wfsParams.sort,
              order: wfsParams.order
      ]

      //println '*' * 40
      //println pagination
      //println '*' * 40

      xml = webFeatureService.getFeature( wfsParams.typeName, wfsParams.filter, pagination )
      break
    default:
      xml = "ERROR"
    }

    render contentType: "text/xml", text: xml
  }
}
