package org.ossim.omar

import java.util.concurrent.ConcurrentHashMap

class CompressorService
{
  static transactional = true

  def cachedFiles = new ConcurrentHashMap()

  def bundleFiles(def servletContext, def files)
  {
    def buffer = new StringBuffer()

    files.each { key ->
      def text = cachedFiles[key]

      if ( !text )
      {
        //println "loading $key"
        text = servletContext.getResource(key)?.text
        //println text

        cachedFiles[key] = text
      }

      buffer.append(text)

      if ( !text.endsWith('\n') )
      {
        buffer.append("\n")
      }
    }

    return buffer.toString()
  }
}
