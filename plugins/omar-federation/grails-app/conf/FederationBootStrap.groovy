class FederationBootStrap {

	def jabberFederatedServerService

    def init = { servletContext ->
    	jabberFederatedServerService.initialize()
    }

    def destroy = {
    }
}
