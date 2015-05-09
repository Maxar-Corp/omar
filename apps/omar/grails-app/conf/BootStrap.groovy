import org.ossim.omar.core.Repository
import org.ossim.omar.StagerJob

class BootStrap {

    def init = { servletContext ->
    	def repo = Repository.findOrCreateByBaseDir('/data/celtic')
    	StagerJob.triggerNow(baseDir: repo.baseDir)
    }

    def destroy = {
    }
}
