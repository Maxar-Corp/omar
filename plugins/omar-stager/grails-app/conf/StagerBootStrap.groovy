import groovy.sql.Sql
import org.ossim.omar.stager.StagerQueueItem

import javax.sql.DataSource

class StagerBootStrap
{
  def dataSourceUnproxied

  def init = { servletContext ->
    def sql = new Sql( dataSourceUnproxied as DataSource )

    if ( StagerQueueItem.count() == 0 )
    {
      sql.execute( "ALTER TABLE stager_queue_item alter column date_created type timestamp with time zone" )
      sql.execute( "ALTER TABLE stager_queue_item alter column last_updated type timestamp with time zone" )

    }

    sql?.close()

  }
}