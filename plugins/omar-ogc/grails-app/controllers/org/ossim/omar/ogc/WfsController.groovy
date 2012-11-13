package org.ossim.omar.ogc

import org.apache.commons.collections.map.CaseInsensitiveMap

import groovy.xml.StreamingMarkupBuilder
import groovy.xml.XmlUtil

class WfsController
{
  def webFeatureService

  def index()
  {

    //println params

    def wfsCommand = new WfsCommand()

    switch ( request.method.toUpperCase() )
    {
    case "POST":

      wfsCommand.service = request.XML.@service
      wfsCommand.version = request.XML.@version
      wfsCommand.request = request.XML.name()

      if ( wfsCommand.request?.toUpperCase() == "DESCRIBEFEATURETYPE" )
      {
        wfsCommand.typeName = request.XML.TypeName.text()
      }
      else if ( wfsCommand.request?.toUpperCase() == "GETFEATURE" )
      {
        wfsCommand.typeName = request.XML.Query.@typeName.text()
        wfsCommand.filter = XmlUtil.serialize( request.XML.Query[0].Filter[0] )
      }
      break

    case "GET":
      def wfsParams = new CaseInsensitiveMap( params ).subMap(
          ['service', 'version', 'request', 'typeName', 'filter']
      )

      bindData( wfsCommand, wfsParams )

      break
    }


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
