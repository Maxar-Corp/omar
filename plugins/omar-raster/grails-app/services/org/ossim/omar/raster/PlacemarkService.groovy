package org.ossim.omar.raster

import geoscript.filter.Filter
import geoscript.geom.Bounds
import geoscript.geom.MultiPoint
import geoscript.workspace.Workspace
import groovy.xml.MarkupBuilder
import groovy.xml.StreamingMarkupBuilder

/**
 * Created by sbortman on 6/14/16.
 */
class PlacemarkService
{
  def dataSourceUnproxied
  def grailsApplication
  def grailsLinkGenerator

  def getPlacemarks(def params)
  {
//    println params

    def placenames = []

    if ( grailsApplication.config.placemarks )
    {
      def task = { Workspace workspace ->
//      println workspace.names.sort()

        def proj = params?.srs ?: 'epsg:4326'
        def bbox = params?.bbox?.split( ',' )?.collect { it as double } ?: [-180, -90, 180, 90]
        def bounds = new Bounds( *bbox, proj )
        def layer = workspace[grailsApplication?.config?.placemarks?.tableName];
        def geomColumn = layer.schema.geom.name

        layer.collectFromFeature(
            filter: Filter.intersects( geomColumn, bounds.geometry ),
            max: grailsApplication?.config?.placemarks?.maxResults as int
        ) { feature ->
          def data = feature.attributes

          data[geomColumn] = [x: feature.geom.x, y: feature.geom.y, proj: layer.proj.id]
          placenames << data
        }
      }

      executeWithWorkspace( task )
    }

    //println placenames
    placenames
  }

  def getPlacenameColumnNames()
  {
    def columnNames = []

    if ( grailsApplication.config.placemarks )
    {

      def task = { Workspace workspace ->
        def layer = workspace[grailsApplication?.config?.placemarks?.tableName];

        columnNames = layer.schema.fields*.name
      }

      executeWithWorkspace( task )
    }

    columnNames
  }

  def createBeFilter(def params)
  {
//    println "params=${params}"

    def results = ""

    def task = { Workspace workspace ->
      def tableName = grailsApplication?.config?.placemarks?.tableName
      def columnName = grailsApplication?.config?.placemarks?.columnName
      def layer = workspace[tableName];
      def geomColumn = layer.schema.geom.name
      def filter = params?.query?.replace( 'be_number', columnName )

//      println filter

      def points = layer.collectFromFeature( filter ) { it[geomColumn] }
      def multiPoint = new MultiPoint( points )

      results = multiPoint?.wkt
    }

    executeWithWorkspace( task )
    results
  }

  def getPlacemarkList(def params)
  {
    def buffer = new StringBuffer( [
        'lon',
        'lat',
        'title',
        'description',
        'icon'
    ].join( '\t' ) )

    buffer.append( '\n' )

    def placemarks = []

    def task = { Workspace workspace ->
//      println workspace.names.sort()

      def proj = params?.srs ?: 'epsg:4326'
      def bbox = params?.bbox?.split( ',' )?.collect { it as double } ?: [-180, -90, 180, 90]
      def bounds = new Bounds( *bbox, proj )
      def layer = workspace[grailsApplication?.config?.placemarks?.tableName];
      def geomColumn = layer.schema.geom.name

      layer.collectFromFeature(
          filter: Filter.intersects( geomColumn, bounds.geometry ),
          max: grailsApplication?.config?.placemarks?.maxResults as int
      ) { feature ->
        def data = feature.attributes

        data[geomColumn] = [x: feature.geom.x, y: feature.geom.y, proj: layer.proj.id]
        placemarks << data
      }
    }

    executeWithWorkspace( task )

    placemarks.each {
      buffer.append( [
          it.geom.x,
          it.geom.y,
          "Placemark",
          formatDescription( it ),
          grailsLinkGenerator.resource( dir: 'images', file: 'marker.png' )
      ].join( '\t' ) )
      buffer.append( '\n' )
    }
    buffer
  }

  def formatDescription(def rec)
  {
    new StreamingMarkupBuilder().bind {
      div( 'class': 'dialog', style: "overflow-y:scroll;" ) {
        table {
          rec.each { field ->
            tr( 'class': 'prop' ) {
              td( 'class': 'name' ) {
                font( size: '2', field.key )
              }
              td( 'class': 'value' ) {
                font( size: '2', field.value )
              }
            }
          }
        }
      }
    }
  }

  def executeWithWorkspace(Closure task)
  {
    def dbParams = [
        dbtype: 'postgis',

        // All these can be blank (except for port for some reason)
        // The dataSource is provided by Hibernate.
        database: '',
        host: '',
        port: 5432,
        user: '',
        password: '',

        'Data Source': dataSourceUnproxied,
        'Expose primary keys': true,
        namespace: 'http://omar.ossim.org'
    ]

    Workspace.withWorkspace( dbParams ) { Workspace workspace ->
      task( workspace )
      workspace?.close()
    }
  }
}
