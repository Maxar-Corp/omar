class WmsUtilsService
{
  boolean transactional = false

  def getLayerList(def serviceAddress)
  {
    def capabilitiesURL = "${serviceAddress}service=WMS&version=1.1.1&request=GetCapabilities".toURL()
    def capabilities = new XmlSlurper().parseText(capabilitiesURL.text)
    def layers = []

    capabilities.Capability.Layer.Layer.each {
      layers << [name: it.Name, title: it.Title, url: serviceAddress]
    }

    return layers
  }
}
