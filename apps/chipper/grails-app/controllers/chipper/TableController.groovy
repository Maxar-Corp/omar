package chipper

import grails.converters.JSON

class TableController
{
  def grailsLinkGenerator
  def wfsClientService

  def queryService

  def serviceAddress = 'http://omar.ngaiost.org/omar/wfs'
  def typeName = 'omar:raster_entry'
  def columnNames = ['id', 'file_type', 'acquisition_date', 'mission_id', 'sensor_id', 'filename', 'ground_geom']

  def index()
  {
    def tableModel = [
        url    : grailsLinkGenerator.link( action: 'getData' ),
        columns: ( wfsClientService.getColumns( serviceAddress, typeName, columnNames ).collect {
          it + [resizeable: true]
        } )
    ]

    render view: 'index', model: [tableModel: tableModel, tableMetadata: queryService.tableMetadata
    ]
  }

  def getData(TableCommand tableCmd)
  {
    //println tableCmd

    def data = wfsClientService.getFeature( serviceAddress, typeName, columnNames, tableCmd )

    render contentType: 'application/json', text: data as JSON
  }
}
