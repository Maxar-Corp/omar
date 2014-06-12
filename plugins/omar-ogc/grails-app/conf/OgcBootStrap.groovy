import groovy.sql.Sql
import org.ossim.omar.ogc.WmsLog

import javax.sql.DataSource

class OgcBootStrap
{
  def dataSourceUnproxied

  def init = { servletContext ->
    def sql = new Sql( dataSourceUnproxied as DataSource )

    if ( WmsLog.count() == 0 )
    {
      sql.execute( "ALTER TABLE wms_log alter column start_date type timestamp with time zone" )
      sql.execute( "ALTER TABLE wms_log alter column end_date type timestamp with time zone" )

      sql.execute( "ALTER TABLE get_tile_log alter column start_date type timestamp with time zone" )
      sql.execute( "ALTER TABLE get_tile_log alter column end_date type timestamp with time zone" )

    }

    sql?.close()
  }
}