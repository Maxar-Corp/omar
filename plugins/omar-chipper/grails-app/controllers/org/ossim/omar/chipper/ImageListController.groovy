package org.ossim.omar.chipper

import grails.converters.JSON

class ImageListController
{
  def imageListService

  def index()
  {
    [tableModel: imageListService.createTableModel()]
  }

  def getData()
  {
    def data = imageListService.getData( params )
    render contentType: 'application/json', text: data as JSON
  }

  def getFilterParams()
  {
    def filterParams = [
        [group: 'Acquisition Date', name: 'Start Date', editor: 'datetimebox'],
        [group: 'Acquisition Date', name: 'End Date', editor: 'datebox'],
        [group: 'Image Metadata', name: 'Mission', editor: 'combobox'],
        [group: 'Image Metadata', name: 'Sensor', editor: 'combobox'],
        [group: 'Image Metadata', name: 'Image Id', editor: 'text'],
        [group: 'File', name: 'Filename', editor: 'text'],


    ]

    render contentType: 'application/json', text: [total: filterParams.size(), rows: filterParams] as JSON
  }
}
