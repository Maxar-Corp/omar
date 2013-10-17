package chipper

import org.apache.commons.collections.map.CaseInsensitiveMap

class ViewerController
{
  def chipperService
  def grailsApplication

  def index()
  {
    def orthoImage = grailsApplication.config.chipper.orthoImage
    def colorImage = grailsApplication.config.chipper.colorImage
    def panImage = grailsApplication.config.chipper.panImage
    def psmImage = [colorImage, panImage].join( ',' )

    [colorImage: colorImage, panImage: panImage, orthoImage: orthoImage, psmImage: psmImage]
  }

  def getChip()
  {
    def ctx = startAsync()
    ctx.start {
      def chpCmd = new ChipCommand()

      bindData( chpCmd, new CaseInsensitiveMap( params ), chpCmd.properties )

      //println chpCmd

      def results = chipperService.getChip( chpCmd )

      response.contentType = results.contentType
      response.outputStream << results.buffer
      ctx.complete()
    }
  }

  def getPSM()
  {
    def ctx = startAsync()
    ctx.start {
      def chpCmd = new ChipCommand()

      bindData( chpCmd, new CaseInsensitiveMap( params ), chpCmd.properties )

      //println chpCmd

      def results = chipperService.getPSM( chpCmd )

      response.contentType = results.contentType
      response.outputStream << results.buffer
      ctx.complete()
    }
  }

}
