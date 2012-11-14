package org.ossim.omar.ogc

import org.apache.commons.collections.map.CaseInsensitiveMap

import groovy.xml.StreamingMarkupBuilder
import groovy.xml.XmlUtil

class WfsController
{
  def webFeatureService

  def index()
  {
    def wfsCommand = new WfsCommand()

    //println request.method

    switch ( request.method.toUpperCase() )
    {
    case "POST":
      def builder = new StreamingMarkupBuilder()

      //println "POST: ${ builder.bind { mkp.yield request.XML } }"

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

        if ( request.XML.Query[0].Filter )
        {
          wfsCommand.filter = builder.bind { mkp.yield request.XML.Query[0].Filter[0] }
        }
      }
      break

    case "GET":
      //println "GET: ${ params }"

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
      //println wfsCommand
      results = webFeatureService.getFeature( wfsCommand )
      //println results
      break
    default:
      println "ERROR"
    }

    render contentType: 'application/xml', text: results
  }
}
