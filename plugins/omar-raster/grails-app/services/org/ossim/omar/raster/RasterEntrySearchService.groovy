package org.ossim.omar.raster
//import javax.jws.WebParam

//import org.ossim.omar.raster.RasterEntryMetadata


import org.hibernate.CacheMode
import org.hibernate.criterion.*
//import org.ossim.postgis.Geometry

import com.vividsolutions.jts.geom.Polygon

import org.springframework.beans.factory.InitializingBean
import java.awt.Polygon

import geoscript.workspace.PostGIS
import geoscript.filter.Filter
import org.hibernate.ScrollMode

class RasterEntrySearchService implements InitializingBean
{
  def grailsApplication

  //static expose = ['xfire']

  static transactional = false

  def propertyNames

  List<RasterEntryQuery> runQuery(def rasterEntryQuery, Map<String, String> params)
  {
    def max = null;
    if ( params?.max != null ) max = ( params.max as Integer );
    if ( max < 1 ) return null;
    def criteriaBuilder = RasterEntry.createCriteria();
    def x = {
      createAlias( "rasterDataSet", "rds" )
      if ( max )
      {
        setMaxResults( max )
      }
      if ( params?.offset )
      {
        setFirstResult( params.offset as Integer )
      }
      if ( params?.sort && params?.order )
      {
        if ( params?.sort == "id" || params?.sort in propertyNames )
        {
          def sortColumn = params?.sort
          def order = params?.order
          def ordering = ( order == "asc" ) ? Order.asc( sortColumn ) : Order.desc( sortColumn )

          addOrder( ordering )
        }

        //setFetchMode("rasterEntry", FetchMode.JOIN)
        join "rasterEntry"
      }
    }

    def criteria = criteriaBuilder.buildCriteria( x )
    def clause = rasterEntryQuery?.createClause()
    if ( clause )
    {
      criteria.add( clause )
    }

    def rasterEntries = criteria.list()

    if ( rasterEntries )
    {
      RasterFile.withCriteria {
        eq( "type", "main" )
        inList( "rasterDataSet", rasterEntries.rasterDataSet )
      }
    }

    return rasterEntries
  }



  List<Polygon> getGeometries(RasterEntryQuery rasterEntryQuery, Map<String, String> params)
  {
    def max = null;
    if ( params?.max != null ) max = ( params.max as Integer );
    if ( max < 1 ) return null;
    def criteriaBuilder = RasterEntry.createCriteria();
    def x =
      {
        projections { property( "groundGeom" ) }
        firstResult( params.offset as Integer )
        maxResults( max )
        cacheMode( CacheMode.GET )
      }
    def criteria = criteriaBuilder.buildCriteria( x )
    criteria.add( rasterEntryQuery?.createClause() )

    return criteria.list()
    /*
    def criteria = RasterEntry.createCriteria();

    criteria.setProjection(Projections.property("groundGeom"))

    if ( params?.offset )
    {
      criteria.setFirstResult(params.offset as Integer)
    }

    if ( params?.max )
    {
      criteria.setMaxResults(params.max as Integer)
    }

    criteria.setCacheMode(CacheMode.GET)
    rasterEntryQuery.addToCriteria(criteria)

    def geometries = criteria.instance.list()

    return geometries
    */
  }

  void scrollGeometries(RasterEntryQuery rasterEntryQuery, Map<String, String> params, Closure closure)
  {
    def max = null;
    if ( params?.max ) max = ( params.max as Integer );
    if ( max < 1 ) return;
    def criteriaBuilder = RasterEntry.createCriteria();

    def x = {
      projections { property( "groundGeom" ) }

      if ( max )
      {
        maxResults( max )
      }

      if ( params?.offset )
      {
        firstResult( params.offset as Integer )
      }
      cacheMode( CacheMode.GET )
    }

    def criteria = criteriaBuilder.buildCriteria( x )

    criteria.add( rasterEntryQuery?.createClause() )

    def results = criteria.scroll()
    def status = results.first()

    while ( status )
    {
      def geom = results.get( 0 )

      closure.call( geom )

      status = results.next()
    }

    results.close()
  }

  void scrollFeatures(RasterEntryQuery rasterEntryQuery, Map<String, String> params, Closure closure)
  {
    def max = null;
    if ( params?.max ) max = ( params.max as Integer );
    if ( max < 1 ) return;
    def criteriaBuilder = RasterEntry.createCriteria();

    def x = {
      projections {
        property( "groundGeom" )

        if ( params?.fieldName )
        {
          property( params?.fieldName )
        }
      }

      if ( max )
      {
        maxResults( max )
      }

      if ( params?.offset )
      {
        firstResult( params.offset as Integer )
      }
      cacheMode( CacheMode.GET )
    }

    def criteria = criteriaBuilder.buildCriteria( x )

    criteria.add( rasterEntryQuery?.createClause() )
    criteria.setReadOnly( true )

    def results = criteria.scroll(/*ScrollMode.FORWARD_ONLY*/)
    def status = results.first()

    while ( status )
    {
      def r = results.get()

      def d = [
          groundGeom: r[0],
      ]

      if ( params?.fieldName )
      {
        d[params?.fieldName] = r[1]
      }

      closure.call( d )
      status = results.next()
    }

    results.close()
  }


  int getCount(RasterEntryQuery rasterEntryQuery)
  {
    def criteriaBuilder = RasterEntry.createCriteria();
    def x = {
      projections { rowCount() }
    }
    def criteria = criteriaBuilder.buildCriteria( x )
    criteria.add( rasterEntryQuery?.createClause() )
    def totalCount = criteria.list().get( 0 ) as int
    return totalCount
  }


  def getWmsImageLayers(String[] layerNames)
  {
    return findRasterEntries( layerNames )
  }


  def getWmsImageLayers(String filterText)
  {
    def layers = null

    if ( filterText )
    {
      def layerName = 'raster_entry'
      def username = grailsApplication.config.dataSource.username
      def password = grailsApplication.config.dataSource.password
      def database = grailsApplication.config.dataSource.url - 'jdbc:postgresql_postGIS:'

      def workspace = new PostGIS( [user: username, password: password], database )
      def layerNames = workspace[layerName]?.getFeatures( new Filter( filterText ) )?.collect { it.id.split( '\\.' )[-1] }

      layers = getWmsImageLayers( layerNames as String[] )
      workspace?.close()
    }

    return layers
  }

  def findRasterEntries(def rasterIdList)
  {
    rasterIdList = rasterIdList.collect { it?.toString() }

    def ids = rasterIdList?.findAll { it.isLong() }.collect() { it as Long }

    def rasterEntries = RasterEntry.createCriteria().list() {
      or {
        if ( ids )
        {
          inList( "id", ids )
        }
        inList( "indexId", rasterIdList )
        inList( "imageId", rasterIdList )
      }
    }

    return rasterEntries
  }

  void afterPropertiesSet()
  {
    propertyNames = grailsApplication.getDomainClass( "org.ossim.omar.raster.RasterEntry" )?.properties.name
  }
}
