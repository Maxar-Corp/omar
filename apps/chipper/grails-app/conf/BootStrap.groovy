class BootStrap
{
  def init = { servletContext ->
    joms.oms.Init.instance().initialize()
  }
  def destroy = {
  }
}
