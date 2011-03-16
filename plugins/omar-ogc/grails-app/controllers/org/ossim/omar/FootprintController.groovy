package org.ossim.omar

class FootprintController
{

  def footprintService

  def index = {
  }

  def footprints = {
    def ostream = response.outputStream
    def start = System.currentTimeMillis()

    response.contentType = params.get("format", "image/png")
    footprintService.render(params, ostream)
    ostream.flush()
    ostream.close()

    def end = System.currentTimeMillis()

    //println "${params}, elapsed: ${end - start}ms"
    return null
  }

  def features = {

  }
}
