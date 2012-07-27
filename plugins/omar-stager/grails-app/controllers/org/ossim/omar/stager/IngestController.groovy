package org.ossim.omar.stager

class IngestController
{
  def ingestService

  def dataInfo( )
  {
    def xml = request.XML
    def oms = new XmlSlurper().parseText( xml )
    def (status, message) = ingestService.ingest( oms )

    response.status = status
    response.contentType = 'text/plain'
    response.outputStream << message
  }
}