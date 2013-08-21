package chipper

import org.apache.commons.collections.map.CaseInsensitiveMap

class ViewerController
{
  def chipperService

  def index()
  {}

  def getChip()
  {
    def chpCmd = new ChipCommand()

    bindData( chpCmd, new CaseInsensitiveMap( params ), chpCmd.properties )

    def results = chipperService.getChip( chpCmd )

    response.contentType = results.contentType
    response.outputStream << results.buffer
  }
}
