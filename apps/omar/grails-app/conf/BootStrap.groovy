import org.ossim.omar.core.Repository
import org.ossim.omar.StagerJob

class BootStrap {
	def sessionFactory

    def init = { servletContext ->
        ['/data/celtic', '/data1', '/data/uav' ].each {
           println it
    	   def repo = Repository.findOrCreateByBaseDir(it)
    	   StagerJob.triggerNow(baseDir: repo.baseDir)
        }
        sessionFactory?.currentSession?.flush()
    }

    def destroy = {
    }
}
