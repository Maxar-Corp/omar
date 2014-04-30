package org.ossim.omar.chipper

import org.ossim.omar.raster.RasterEntry

import java.awt.image.Raster

class ImageListService
{
  def grailsApplication
  def sessionFactory

  static columnNames = [
      'id', 'fileType', 'filename', 'entryId', 'acquisitionDate', 'missionId', 'sensorId', 'imageId', 'niirs',
      'groundGeom', 'width', 'height', 'numberOfBands', 'gsdY'
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

  def getData(FetchDataCommand cmd)
  {

    //println params

//    def max = ( params?.rows as Integer ) ?: 10
//    def offset = ( ( params?.page as Integer ?: 1 ) - 1 ) * max
//    def sort = params?.sort ?: 'id'
//    def dir = params?.order ?: 'asc'
//    def x = [max: max, offset: offset, sort: sort, dir: dir]
//
//    println x


    def total = RasterEntry.createCriteria().count {
      if ( cmd.filter )
      {
        sqlRestriction cmd.filter
      }
    }

    def rows = RasterEntry.withCriteria {
      if ( cmd.filter )
      {
        sqlRestriction cmd.filter
      }
//      projections {
//        columnNames.each {
//          property(it)
//        }
//      }
      maxResults( cmd.rows )
      order( cmd.sort, cmd.order )
      firstResult( ( cmd.page - 1 ) * cmd.rows )
    }
    rows = rows.collect { row ->
      columnNames.inject( [:] ) { a, b -> a[b] = row[b]; a }
    }

    return [total: total, rows: rows]
  }
}
