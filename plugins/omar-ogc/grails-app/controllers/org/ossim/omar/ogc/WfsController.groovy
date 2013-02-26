package org.ossim.omar.ogc

import org.apache.commons.collections.map.CaseInsensitiveMap

import groovy.xml.StreamingMarkupBuilder

import geoscript.filter.Filter

class WfsController
{
  def webFeatureService

  def index()
  {
    def results, contentType
    def wfsCommand = new WfsCommand()

    try
    {
      //println request.method

      switch ( request.method.toUpperCase() )
      {
      case "POST":
        wfsCommand = WfsCommand.fromXML( request.XML )
        break

      case "GET":
        wfsCommand = new WfsCommand()

        def wfsParams = new CaseInsensitiveMap( params ).subMap(
            wfsCommand.properties.keySet()
        )

        bindData( wfsCommand, wfsParams )


        if ( ( !wfsCommand.maxFeatures ) || ( wfsCommand.maxFeatures.toInteger() > 1000 ) )
        {
          wfsCommand.maxFeatures = 1000
        }

        break
      }

      //println wfsCommand

      switch ( wfsCommand.request?.toUpperCase() )
      {
      case "GETCAPABILITIES":
        (results, contentType) = webFeatureService.getCapabilities( wfsCommand )
        break
      case "DESCRIBEFEATURETYPE":
        (results, contentType) = webFeatureService.describeFeatureType( wfsCommand )
        break
      case "GETFEATURE":
        (results, contentType) = webFeatureService.getFeature( wfsCommand );
        if ( params.callback )
        {
          results = "${params.callback}(${results});";
        }
        break
      default:
        throw new Exception( "Unsupported Operation: ${ wfsCommand.request }" )
      }

      if ( !results )
      {
        throw new Exception( "Unknown Exception: ${ wfsCommand.request }" )
      }

    }
    catch ( Exception e )
    {
      results = new StreamingMarkupBuilder().bind() {
        mkp.xmlDeclaration()
        mkp.declareNamespace( xsi: "http://www.w3.org/2001/XMLSchema-instance" )
        ServiceExceptionReport( version: "1.2.0", xmlns: "http://www.opengis.net/ogc",
            'xsi:schemaLocation': "http://www.opengis.net/ogc http://schemas.opengis.net/wfs/1.0.0/OGC-exception.xsd" ) {
          ServiceException( code: "GeneralException", e.message )
        }
      }.toString()
      //contentType = 'application/vnd.ogc.se_xml'
      contentType = 'application/xml'
    }
    finally
    {
      def attachment = "WFS-${wfsCommand.outputFormat}"
      response.setHeader( "Content-disposition", "attachment; ${attachment}" )
      // println "contentType: ${contentType}, text: ${results}"
      render contentType: contentType, text: results
    }
  }
}
