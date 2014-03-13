package org.ossim.omar.chipper

import org.apache.commons.collections.map.CaseInsensitiveMap
import org.ossim.omar.chipper.ChipCommand

class ChipperController
{
  def chipperService

  def getChip()
  {
    /*
    def ctx = startAsync()
    ctx.start {
      def chpCmd = new ChipCommand()

      bindData( chpCmd, new CaseInsensitiveMap( params ), chpCmd.properties.keySet().collect { it.toUpperCase() } )

      //println chpCmd

      def results = chipperService.getChip( chpCmd )

      response.contentType = results.contentType
      response.outputStream << results.buffer
      ctx.complete()
    }
    */
  }

  def get2CMV()
  {
    println "get2CMV!!!!!!!!!!!!"

    //def ctx = startAsync()
    //println "CTX ============= ${ctx}"
    //ctx.start {
      def chpCmd = new ChipCommand()

      bindData( chpCmd, new CaseInsensitiveMap( params ), chpCmd.properties )

      if ( !chpCmd.validate() )
      {
        log.error( cmd.createErrorString() )
        //   println cmd.createErrorString()
        ogcExceptionService.writeResponse( response, ogcExceptionService.formatWmsException( cmd ) )
      }
      else
      {
        //println chpCmd

        def results = chipperService.get2CMV( chpCmd )

        response.contentType = results.contentType
        response.outputStream << results.buffer
      }
     // ctx.complete()
   // }
    //println "Leaving get2CMV!!!!!!!!!!!!"
    null
  }

}
