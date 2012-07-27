import org.ossim.omar.core.Repository
import org.codehaus.groovy.grails.plugins.DomainClassGrailsPlugin

def inputFile = ( System.properties['inputFile'] ?: "raster_file_list.txt" ) as File
def reader = inputFile.newReader()

filename = null
index = 0
batchSize = ctx.grailsApplication.config.hibernate.jdbc.batch_size as int

repository = Repository.findByBaseDir( "/" )
rasterInfoParser = ctx.rasterInfoParser
sessionFactory = ctx.sessionFactory

session = null

if ( true )
{
  session = sessionFactory.currentSession
}
else
{
  session = sessionFactory.openStatelessSession()
}


propertyInstanceMap = DomainClassGrailsPlugin.PROPERTY_INSTANCE_MAP

filesLog = new File( "/tmp/files.txt" ).newWriter()
rejectsLog = new File( "/tmp/rejects.txt" ).newWriter()
dataInfoService = ctx.dataInfoService

while ( ( filename = reader.readLine() ) != null )
{
  processFile( filename )
}

reader?.close()

filesLog.flush()
filesLog.close()

rejectsLog.flush()
rejectsLog.close()

def processFile( def filename )
{
  def xml = dataInfoService.getInfo( filename )

  if ( xml )
  {
    def oms = new XmlSlurper().parseText( xml )

    def dataSets = rasterInfoParser.processDataSets( oms, repository )

    dataSets?.each {dataSet ->
      if ( dataSet.save() )
      {
        filesLog.println filename
      }
      else
      {
        rejectsLog.println filename
      }
    }
  }

  if ( ++index % 100 == 0 )
  {
    cleanUpGorm()
  }
}


def cleanUpGorm( )
{
  //def session = sessionFactory.currentSession
  if ( true )
  {
    session.flush()
    session.clear()
  }
  propertyInstanceMap.get().clear()
}
