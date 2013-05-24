package org.ossim.omar.ogc

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
      cswCmd = CswCommand.fromXML( request?.XML )
      break
    }

    println cswCmd

    def results = null

    switch ( cswCmd?.request?.toLowerCase() )
    {
    case "getcapabilities":
      results = catalogWebService.getCapabiltiies()
      break
    case "describerecord":
      results = catalogWebService.describeRecord()
      break
    case "getrecordbyid":
      results = catalogWebService.getRecordById()
      break
    case "getrecords":
      results = catalogWebService.getRecords()
      break
    }

    render contentType: 'application/xml', text: results
  }
}
