package org.ossim.omar.chipper

import org.apache.commons.collections.map.CaseInsensitiveMap
import org.ossim.omar.chipper.ChipCommand

class ChipperController
{
  def chipperService
  def ogcExceptionService

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

    def data = chipperService.getChip( new CaseInsensitiveMap( params ) )

    response.contentType = data.contentType
    response.outputStream << data.content

  }

//  def get2CMV()
//  {
//    //def ctx = startAsync()
//    //println "CTX ============= ${ctx}"
//    //ctx.start {
//    def chpCmd = new TwoColorMultiCommand()
//
//    def caseParams = new CaseInsensitiveMap( params )
//
//    bindData( chpCmd, new CaseInsensitiveMap( params ), chpCmd.properties )
//
//    if ( !chpCmd.validate() )
//    {
//      log.error( chpCmd.createErrorString() )
//      //   println cmd.createErrorString()
//      ogcExceptionService.writeResponse( response, ogcExceptionService.formatWmsException( chpCmd ) )
//    }
//    else
//    {
//      //println chpCmd
//
//      def results = chipperService.get2CMV( chpCmd )
//
//      response.contentType = results.contentType
//      response.outputStream << results.buffer
//    }
//    // ctx.complete()
//    // }
//    //println "Leaving get2CMV!!!!!!!!!!!!"
//    null
//  }


  def getThumbnail()
  {
//    def ctx = startAsync()
//    println "CTX ============= ${ctx}"
//    ctx.start {
    def data = chipperService.getThumbnail( new CaseInsensitiveMap( params ) )

    response.contentType = data.contentType
    response.outputStream << data.content
//      ctx.complete()
//    }
  }

  def get2CMV()
  {
    def data = chipperService.get2CMV( new CaseInsensitiveMap( params ) )

    response.contentType = data.contentType
    response.outputStream << data.content
  }

  def getPSM()
  {
    def data = chipperService.getPSM( new CaseInsensitiveMap( params ) )

    response.contentType = data.contentType
    response.outputStream << data.content
  }

  def getHillShade()
  {
//    println '*' * 40
//    println params
//    println '*' * 40



    def data = chipperService.getHillShade( new CaseInsensitiveMap( params ) )

    response.contentType = data.contentType
    response.outputStream << data.content
  }

}
