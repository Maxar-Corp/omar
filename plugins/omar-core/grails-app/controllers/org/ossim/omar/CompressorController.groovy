package org.ossim.omar

import java.util.zip.GZIPOutputStream

class CompressorController
{
  def compress = {

    def files = params.files.split(',')
    def buffer = new StringBuffer()
    def acceptEncoding = request.getHeader('accept-encoding')
    def outputStream = response.outputStream

    if ( acceptEncoding?.contains('gzip') )
    {
      outputStream = new GZIPOutputStream(outputStream)
      response.setHeader("content-encoding", "gzip");
    }

    files.each {
      def text = servletContext.getResource(it.toString() - resource(file: '/'))?.text

      buffer.append(text)

      if ( !text.endsWith('\n') )
      {
        buffer.append("\n")
      }
    }

    response.contentType = params.contentType
    outputStream << buffer.toString()
    outputStream.flush()
    outputStream.close()
  }
}
