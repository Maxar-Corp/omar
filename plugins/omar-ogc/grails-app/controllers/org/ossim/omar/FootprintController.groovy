package org.ossim.omar

import org.apache.commons.collections.map.CaseInsensitiveMap
import geoscript.filter.Color

class FootprintController
{
  def grailsApplication
  def footprintService

  def index = {

    def fileTypes = RasterEntry.createCriteria().list {
      projections {
        distinct("fileType")
      }
      order("fileType", "asc")
    }

    def colorNames = Color.colorNameMap.keySet().toList()
    def layerData = []
    def i = 0

    for ( def fileType in fileTypes )
    {
      layerData << [
              name: fileType,
              filter: "file_type='${fileType}'",
              color: Color.colorNameMap[colorNames[i]]
      ]
      i++
    }

    layerData = [layerData[0]]

    def baseLayer = grailsApplication.config.wms.base.layers[0].url

    [layerData: layerData, baseLayer: baseLayer]
  }

  def footprints = {
    def ostream = response.outputStream
    def caseInsensitiveParams = new CaseInsensitiveMap(params)
    def start = System.currentTimeMillis()

    response.contentType = caseInsensitiveParams.get("format", "image/png")
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
