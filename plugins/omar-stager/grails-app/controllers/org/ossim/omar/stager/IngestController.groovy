package org.ossim.omar.stager

import org.ossim.omar.core.Repository

class IngestController
{

  def dataInfo( )
  {
    def message

//    def xml =  request.reader.text
//
//    println xml

//    new File( '/tmp/foo.xml' ).write(xml)
//
//    def oms = new XmlSlurper(  ).parseText(xml)


    def oms = request.XML


    if ( oms )
    {
      def omsInfoParsers = applicationContext.getBeansOfType( OmsInfoParser.class )
      def repository = Repository.findByBaseDir('/')

      omsInfoParsers?.each { name, value ->

        def dataSets = value.processDataSets( oms, repository )

        dataSets?.each {dataSet ->
          if ( dataSet.save() )
          {
            message = "Accepted"
          }
          else
          {
            message = "Rejected"
          }
        }
      }
    }

    response.status = 200
    response.contentType = 'text/plain'
    response.outputStream << message
  }
}