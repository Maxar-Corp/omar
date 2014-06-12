import groovy.sql.Sql
import org.ossim.omar.video.VideoDataSet

import javax.sql.DataSource

class VideoBootStrap
{
  def grailsApplication
  def dataSourceUnproxied

  def init = { servletContext ->

    def sql = new Sql( dataSourceUnproxied as DataSource )

    if ( VideoDataSet.count() == 0 )
    {
      sql.execute( "ALTER TABLE video_data_set alter column start_date type timestamp with time zone" )
      sql.execute( "ALTER TABLE video_data_set alter column end_date type timestamp with time zone" )
    }

    sql?.close()

    def foo = ['videoDataSet']

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