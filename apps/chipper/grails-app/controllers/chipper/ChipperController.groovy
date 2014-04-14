package chipper

import geoscript.geom.Bounds
import org.apache.commons.collections.map.CaseInsensitiveMap

import java.awt.Dimension

class ChipperController
{
  def chipperService

  def getChip()
  {
    def ctx = startAsync()
    ctx.start {
      def chpCmd = new Chip( new CaseInsensitiveMap( params ) )

      //bindData( chpCmd, new CaseInsensitiveMap( params ), chpCmd.properties.keySet().collect { it.toUpperCase() } )

      println chpCmd

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
      println params
      def chpCmd = new PanSharpen( new CaseInsensitiveMap( params ) )

      //bindData( chpCmd, new CaseInsensitiveMap( params ), chpCmd.properties )

      println chpCmd

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
      println params
      def chpCmd = new TwoColorMultiView( new CaseInsensitiveMap( params ) )

      //bindData( chpCmd, new CaseInsensitiveMap( params ), chpCmd.properties )

      println chpCmd

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

  def getThumbnail()
  {
    def ctx = startAsync()
    ctx.start {

//      println params

      def chpCmd = new Thumbnail( new CaseInsensitiveMap( params ) )

      //bindData( chpCmd, new CaseInsensitiveMap( params ), chpCmd.properties )

      //println chpCmd

      def results = chipperService.getThumbnail( chpCmd )

      response.contentType = results.contentType
      response.outputStream << results.buffer
      ctx.complete()
    }
  }
}
