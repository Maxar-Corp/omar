package org.ossim.omar.ogc

import groovy.xml.StreamingMarkupBuilder
import org.apache.commons.collections.map.CaseInsensitiveMap

class CswController
{
  def catalogWebService

  def index()
  {
    def cswCmd = null

    switch ( request.method.toUpperCase() )
    {
    case "GET":
      cswCmd = new CswCommand()

      def cswParams = new CaseInsensitiveMap( params ).subMap(
          cswCmd.properties.keySet() )

      bindData( cswCmd, cswParams )
      break
    case "POST":
      println new StreamingMarkupBuilder().bind { mkp.yield request.XML }.toString()
      cswCmd = CswCommand.fromXML( request?.XML )
      break
    }

    println cswCmd

    def results = null
    def contentType = 'application/xml'

    try
    {
      switch ( cswCmd?.request?.toLowerCase() )
      {
      case "getcapabilities":
        results = catalogWebService.getCapabiltiies( cswCmd )
        break
      case "describerecord":
        results = catalogWebService.describeRecord( cswCmd )
        break
      case "getrecordbyid":
        results = catalogWebService.getRecordById( cswCmd )
        break
      case "getrecords":
        results = catalogWebService.getRecords( cswCmd )
        break
      default:
        results = catalogWebService.getCapabiltiies( cswCmd )
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
      contentType = 'application/vnd.ogc.se_xml'
    }
    finally
    {
      render contentType: contentType, text: results
    }
  }
}
