import org.ossim.omar.core.Repository
import org.ossim.omar.StagerJob

import grails.util.Environment

class BootStrap
{
  def sessionFactory

  def init = { servletContext ->
    if ( Environment.current == Environment.DEVELOPMENT )
    {

      ['/data/celtic', '/data/uav'].each {
        println it
        def repo = Repository.findOrCreateByBaseDir( it )
        repo.save()
        StagerJob.triggerNow( baseDir: repo.baseDir )
      }
      sessionFactory?.currentSession?.flush()
    }
  }

  def destroy = {
  }
}
