import org.ossim.omar.stager.StagerUtil
import org.ossim.omar.core.Repository
import org.codehaus.groovy.grails.plugins.DomainClassGrailsPlugin

def inputFile = "raster_file_list.txt" as File
def reader = inputFile.newReader()

filename = null
index = 0

repository = Repository.findByBaseDir( "/" )
rasterInfoParser = ctx.rasterInfoParser
sessionFactory = ctx.sessionFactory
propertyInstanceMap = DomainClassGrailsPlugin.PROPERTY_INSTANCE_MAP

filesLog = "/tmp/files.txt" as File
rejectsLog = "/tmp/rejects.txt" as File
dataInfoService = ctx.dataInfoService

while ( ( filename = reader.readLine() ) != null )
{
  processFile( filename )
}

reader?.close()

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
        filesLog.append( "${filename}\n" )
      }
      else
      {
        rejectsLog.append( "${filename}\n" )
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
  def session = sessionFactory.currentSession
  session.flush()
  session.clear()
  propertyInstanceMap.get().clear()
}
