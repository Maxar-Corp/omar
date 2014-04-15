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
}
