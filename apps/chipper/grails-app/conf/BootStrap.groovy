class BootStrap
{
  def init = { servletContext ->
//    joms.oms.Init.instance().initialize(3, ['', '-T', 'ossimChipper'] as String[])
    joms.oms.Init.instance().initialize()

  }
  def destroy = {
  }
}
