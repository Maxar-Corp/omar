package cstars.footprints

import grails.converters.JSON

class FootprintViewerController
{

  def index = { }

  def footprints = {
    def sensor = (params.sensorName) ? Sensor.findByName(params.sensorName) : null

    def footprints = ImageFootprint.withCriteria {
      
      if ( sensor )
      {
        createAlias("parent", "p")
        eq("p.sensor", sensor)
      }
    }.groupBy { it.parent.sensor.name }.sort { it.key }

    footprints?.each { k, v -> footprints[k] = v.collect { it.groundGeom.toString() } }
    
    render contentType: "application/json", text:  footprints as JSON
  }
}
