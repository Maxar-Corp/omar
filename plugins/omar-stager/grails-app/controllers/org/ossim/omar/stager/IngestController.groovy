package org.ossim.omar.stager

class IngestController
{
  def ingestService
  def parserPool

  def dataInfo( )
  {
    def xml = request.XML
    def parser = parserPool.borrowObject()
    def oms = new XmlSlurper(parser).parseText( xml )

    parserPool.returnObject(parser)

    def (status, message) = ingestService.ingest( oms )

    response.status = status
    response.contentType = 'text/plain'
    response.outputStream << message
  }
}