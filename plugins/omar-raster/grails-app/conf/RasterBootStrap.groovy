import groovy.sql.Sql
import org.ossim.omar.ogc.WmsLog
import org.ossim.omar.raster.RasterDataSet

import javax.sql.DataSource

class RasterBootStrap
{
  def grailsApplication
  def dataSourceUnproxied

  def init = { servletContext ->

    def sql = new Sql( dataSourceUnproxied as DataSource )

    if ( RasterDataSet.count() == 0 )
    {
//      sql.execute( "ALTER TABLE raster_entry alter column access_date type timestamp with time zone" )
//      sql.execute( "ALTER TABLE raster_entry alter column acquisition_date type timestamp with time zone" )
//      sql.execute( "ALTER TABLE raster_entry alter column ingest_date type timestamp with time zone" )
//      sql.execute( "ALTER TABLE raster_entry alter column receive_date type timestamp with time zone" )
    }

    sql?.close()

    def foo = ['rasterEntry']

    foo.each { bar ->
      def searchTagDomainClass = grailsApplication.getArtefactByLogicalPropertyName( 'Domain', "${bar}SearchTag" ).clazz

      if ( searchTagDomainClass.count() == 0 )
      {
        def searchTagData = grailsApplication.config."${bar}".searchTagData

        searchTagData.each {
          searchTagDomainClass.findOrSaveWhere( name: it.name, description: it.description )
        }
      }
    }

  }
}
