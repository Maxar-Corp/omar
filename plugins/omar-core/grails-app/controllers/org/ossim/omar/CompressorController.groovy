package org.ossim.omar

import java.util.zip.GZIPOutputStream

class CompressorController
{
  def compressorService

  def compress = {

    def files = params.files.split(',')?.collect {
      it.toString() - resource(file: '/')
    }

    def acceptEncoding = request.getHeader('Accept-Encoding')
    def outputStream = null

//    if ( acceptEncoding?.contains('gzip') )
//    {
//      outputStream = new GZIPOutputStream(response.outputStream)
//      response.setHeader("Content-Encoding", "gzip");
//      response.setHeader("Vary", "Accept-Encoding");
//    }
//    else
//    {
      outputStream = response.outputStream
//    }

    def buffer = compressorService.bundleFiles(servletContext, files)

    response.contentType = params.contentType
    outputStream << buffer
    outputStream.flush()
    outputStream.close()
  }
}
