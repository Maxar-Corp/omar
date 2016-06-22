package org.ossim.omar.raster

import grails.converters.JSON

class PlacemarkController
{
  def placemarkService

  def getPlacemarks()
  {
    def placemarks = placemarkService.getPlacemarks( params )

    render contentType: 'application/json', text: placemarks as JSON
  }

  def getPlacemarkList()
  {
    def placemarkList = placemarkService.getPlacemarkList( params )

    render contentType: 'application/json', text: placemarkList
  }

  def createBeFilter()
  {
    def results = placemarkService.createBeFilter( params )

    render contentType: 'text/plain', text: results
  }
}
