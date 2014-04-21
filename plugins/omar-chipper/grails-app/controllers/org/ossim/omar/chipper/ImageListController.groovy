package org.ossim.omar.chipper

import grails.converters.JSON
import org.ossim.omar.raster.RasterEntry

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
    def missions = RasterEntry.withCriteria {
      projections {
        distinct( 'missionId' )
      }
    }?.sort()?.collect {
      [label: it, value: it]
    }

    def sensors = RasterEntry.withCriteria {
      projections {
        distinct( 'sensorId' )
      }
    }?.sort()?.collect {
      [label: it, value: it]
    }

    def fileTypes = RasterEntry.withCriteria {
      projections {
        distinct( 'fileType' )
      }
    }?.sort()?.collect {
      [label: it, value: it]
    }


    def filterParams = [
        [group: 'Acquisition Date', name: 'Start Date', editor: 'datetimebox'],
        [group: 'Acquisition Date', name: 'End Date', editor: 'datetimebox'],

        [group: 'Image Metadata', name: 'Mission', editor: [
            type: 'combobox', options: [valueField: 'label', textField: 'value', data: missions]]
        ],
        [group: 'Image Metadata', name: 'Sensor', editor: [
            type: 'combobox', options: [valueField: 'label', textField: 'value', data: sensors]]
        ],
        [group: 'Image Metadata', name: 'Image Id', editor: 'text'],
        [group: 'File', name: 'Filename', editor: 'text'],
        [group: 'File', name: 'Format', editor: [
            type: 'combobox', options: [valueField: 'label', textField: 'value', data: fileTypes]]
        ],


    ]

    render contentType: 'application/json', text: [total: filterParams.size(), rows: filterParams] as JSON
  }

}
