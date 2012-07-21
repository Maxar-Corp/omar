package org.ossim.omar.raster

import org.ossim.omar.core.Repository
import org.ossim.omar.raster.RasterDataSet

class RasterDataSetController
{

  def index( )
  { redirect( action: 'list', params: params ) }

  // the delete, save and update actions only accept POST requests
  def static allowedMethods = [delete: 'POST', save: 'POST', update: 'POST']

  def list( )
  {
    if ( !params.max )
      params.max = 10

    def rasterDataSetList = null

    if ( params.repositoryId )
    {
      def repository = Repository.get( params.repositoryId )

      rasterDataSetList = RasterDataSet.createCriteria().list( params ) {
        eq( "repository", repository )
      }
    }
    else
    {
      rasterDataSetList = RasterDataSet.createCriteria().list( params ) {}
    }

    [rasterDataSetList: rasterDataSetList]
  }

  def show( )
  {
    def rasterDataSet = RasterDataSet.get( params.id )

    if ( !rasterDataSet )
    {
      flash.message = "RasterDataSet not found with id ${params.id}"
      redirect( action: 'list' )
    }
    else
    { return [rasterDataSet: rasterDataSet] }
  }

  def delete( )
  {
    def rasterDataSet = RasterDataSet.get( params.id )
    if ( rasterDataSet )
    {
      rasterDataSet.delete()
      flash.message = "RasterDataSet ${params.id} deleted"
      redirect( action: 'list')
    }
    else
    {
      flash.message = "RasterDataSet not found with id ${params.id}"
      redirect( action: 'list' )
    }
  }

  def edit( )
  {
    def rasterDataSet = RasterDataSet.get( params.id )

    if ( !rasterDataSet )
    {
      flash.message = "RasterDataSet not found with id ${params.id}"
      redirect( action: 'list' )
    }
    else
    {
      return [rasterDataSet: rasterDataSet]
    }
  }

  def update( )
  {
    def rasterDataSet = RasterDataSet.get( params.id )
    if ( rasterDataSet )
    {
      rasterDataSet.properties = params
      if ( !rasterDataSet.hasErrors() && rasterDataSet.save() )
      {
        flash.message = "RasterDataSet ${params.id} updated"
        redirect( action: 'show', id: rasterDataSet.id )
      }
      else
      {
        render( view: 'edit', model: [rasterDataSet: rasterDataSet] )
      }
    }
    else
    {
      flash.message = "RasterDataSet not found with id ${params.id}"
      redirect( action: 'edit', id: params.id )
    }
  }

  def create( )
  {
    def rasterDataSet = new RasterDataSet()
    rasterDataSet.properties = params
    return ['rasterDataSet': rasterDataSet]
  }

  def save( )
  {
    def rasterDataSet = new RasterDataSet( params )
    if ( !rasterDataSet.hasErrors() && rasterDataSet.save() )
    {
      flash.message = "RasterDataSet ${rasterDataSet.id} created"
      redirect( action: 'show', id: rasterDataSet.id )
    }
    else
    {
      render( view: 'create', model: [rasterDataSet: rasterDataSet] )
    }
  }
}
