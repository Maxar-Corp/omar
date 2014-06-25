import joms.oms.Init

class OmarOmsBootStrap {

    def init = { servletContext ->
	Init.instance().initialize()
    }
    def destroy = {
    }
}
