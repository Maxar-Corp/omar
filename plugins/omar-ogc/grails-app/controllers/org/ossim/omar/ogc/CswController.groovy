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
      def cswParams = new CaseInsensitiveMap( params )

      cswCmd = new CswCommand()
      bindData( cswCmd, cswParams )
      break
    case "POST":
      println new StreamingMarkupBuilder().bind { mkp.yield request.XML }.toString()
      cswCmd = CswCommand.fromXML( request?.XML )
      break
    }

    println cswCmd

    def results = null

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
    }

    render contentType: 'application/xml', text: results
  }
}
