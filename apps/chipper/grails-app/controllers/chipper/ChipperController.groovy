package chipper

import org.apache.commons.collections.map.CaseInsensitiveMap

class ChipperController
{
  def chipperService

  def getChip()
  {
    def ctx = startAsync()
    ctx.start {
      def chpCmd = new ChipCommand()

      bindData( chpCmd, new CaseInsensitiveMap( params ), chpCmd.properties?*.toUpperCase() )

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

  def get2CMV()
  {
    def ctx = startAsync()
    ctx.start {
      def chpCmd = new ChipCommand()

      bindData( chpCmd, new CaseInsensitiveMap( params ), chpCmd.properties )

      //println chpCmd

      def results = chipperService.get2CMV( chpCmd )

      response.contentType = results.contentType
      response.outputStream << results.buffer
      ctx.complete()
    }
  }

  def getHillShade()
  {
    def ctx = startAsync()
    ctx.start {
      def chpCmd = new ChipCommand()

      bindData( chpCmd, new CaseInsensitiveMap( params ), chpCmd.properties )

      //println chpCmd

      def results = chipperService.getHillShade( chpCmd )

      response.contentType = results.contentType
      response.outputStream << results.buffer
      ctx.complete()
    }
  }
}