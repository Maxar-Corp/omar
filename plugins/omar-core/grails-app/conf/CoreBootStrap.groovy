import grails.util.GrailsUtil
import groovy.sql.Sql
import org.ossim.omar.core.Report
import org.ossim.omar.core.Repository

import javax.sql.DataSource

class CoreBootStrap
{
  def dataSourceUnproxied

  def init = { servletContext ->

    def sql = new Sql( dataSourceUnproxied as DataSource )

    if ( Report.count() == 0 )
    {
//      sql.execute( "ALTER TABLE report alter column date_created type timestamp with time zone" )
//      sql.execute( "ALTER TABLE report alter column last_updated type timestamp with time zone" )
    }

    if ( Repository.count() == 0 )
    {
//      sql.execute( "ALTER TABLE repository alter column scan_start_date type timestamp with time zone" )
//      sql.execute( "ALTER TABLE repository alter column scan_end_date type timestamp with time zone" )

//      def baseDirs

//      if ( GrailsUtil.isDevelopmentEnv() )
//      {
//        baseDirs = ["/", "/data/uav", "/Volumes/Iomega_HDD/data"]
//      }
//      else
//      {
//        baseDirs = ["/"]
//      }

//      baseDirs.each { baseDir ->
//        Repository.findOrSaveWhere( baseDir: baseDir )
//      }
    }

    sql?.close()
  }
}
