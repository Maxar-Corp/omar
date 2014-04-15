package org.ossim.omar.chipper

import org.ossim.omar.raster.RasterEntry

import java.awt.image.Raster

class ImageListService
{
  def grailsApplication
  def sessionFactory

  static columnNames = [
      'id', 'fileType', 'filename', 'entryId', 'acquisitionDate', 'missionId', 'sensorId', 'imageId', 'niirs',
      'groundGeom', 'width', 'height', 'numberOfBands'
  ]

  def createTableModel()
  {
    def clazz = RasterEntry.class
    def domain = grailsApplication.getDomainClass( clazz.name )

    def columns = columnNames?.collect {
      def property = ( it == 'id' ) ? domain?.identifier : domain?.getPersistentProperty( it )

      [field: property?.name, type: property?.type, title: property?.naturalName, sortable: true]
    }

    def tableModel = [
        columns: [columns]
    ]

    return tableModel
  }

  def getData(def params)
  {

    //println params

    def total = RasterEntry.count()

    def max = ( params?.rows as Integer ) ?: 10
    def offset = ( ( params?.page as Integer ?: 1 ) - 1 ) * max
    def sort = params?.sort ?: 'id'
    def order = params?.order ?: 'asc'
    def x = [max: max, offset: offset, sort: sort, order: order]

    println x

    def rows = RasterEntry.list( x )

    rows = rows.collect { row ->
      columnNames.inject( [:] ) { a, b -> a[b] = row[b]; a }
    }

    return [total: total, rows: rows]
  }
}
