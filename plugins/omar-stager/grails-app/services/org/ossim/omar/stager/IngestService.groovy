package org.ossim.omar.stager

import org.ossim.omar.core.Repository
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware

class IngestService implements ApplicationContextAware
{
  static transactional = true

  def applicationContext


  def ingest( def oms, def baseDir = '/' )
  {
    def status
    def message

    if ( oms )
    {
      def omsInfoParsers = applicationContext.getBeansOfType( OmsInfoParser.class )
      def repository = Repository.findByBaseDir(baseDir.absolutePath)

      for ( def parser in omsInfoParsers?.values() )
      {
        def dataSets = parser.processDataSets( oms, repository )

        for ( def dataSet in dataSets )
        {
          if ( dataSet.save() )
          {
            status = 200
            message = "Accepted"
          }
          else
          {
            status = 500
            message = "Rejected"
          }
        }
      }
    }

    return [status, message]
  }

  void setApplicationContext( ApplicationContext applicationContext )
  {
    this.applicationContext = applicationContext
  }
}
