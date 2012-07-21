package org.ossim.omar.raster

import org.ossim.omar.raster.RasterFile
import org.ossim.omar.raster.RasterDataSet

class RasterFileController
{

  def index( )
  { redirect( action: 'list', params: params ) }

  // the delete, save and update actions only accept POST requests
  def static allowedMethods = [delete: 'POST', save: 'POST', update: 'POST']

  def list( )
  {
    if ( !params.max )
      params.max = 10

    def rasterFileList = null

    if ( params.rasterDataSetId )
    {
      def rasterDataSet = RasterDataSet.get( params.rasterDataSetId )

      rasterFileList = RasterFile.createCriteria().list( params ) {
        eq( "rasterDataSet", rasterDataSet )
      }
    }
    else
    {
      rasterFileList = RasterFile.createCriteria().list( params ) {}

    }

    [rasterFileList: rasterFileList]
  }

  def show( )
  {
    def rasterFile = RasterFile.get( params.id )

    if ( !rasterFile )
    {
      flash.message = "RasterFile not found with id ${params.id}"
      redirect( action: 'list' )
    }
    else
    { return [rasterFile: rasterFile] }
  }

  def delete = {
    def rasterFile = RasterFile.get( params.id )
    if ( rasterFile )
    {
      rasterFile.delete()
      flash.message = "RasterFile ${params.id} deleted"
      redirect( action: 'list' )
    }
    else
    {
      flash.message = "RasterFile not found with id ${params.id}"
      redirect( action: 'list' )
    }
  }

  def edit( )
  {
    def rasterFile = RasterFile.get( params.id )

    if ( !rasterFile )
    {
      flash.message = "RasterFile not found with id ${params.id}"
      redirect( action: 'list' )
    }
    else
    {
      return [rasterFile: rasterFile]
    }
  }

  def update( )
  {
    def rasterFile = RasterFile.get( params.id )
    if ( rasterFile )
    {
      rasterFile.properties = params
      if ( !rasterFile.hasErrors() && rasterFile.save() )
      {
        flash.message = "RasterFile ${params.id} updated"
        redirect( action: 'show', id: rasterFile.id )
      }
      else
      {
        render( view: 'edit', model: [rasterFile: rasterFile] )
      }
    }
    else
    {
      flash.message = "RasterFile not found with id ${params.id}"
      redirect( action: 'edit', id: params.id )
    }
  }

  def create( )
  {
    def rasterFile = new RasterFile()
    rasterFile.properties = params
    return ['rasterFile': rasterFile]
  }

  def save( )
  {
    def rasterFile = new RasterFile( params )
    if ( !rasterFile.hasErrors() && rasterFile.save() )
    {
      flash.message = "RasterFile ${rasterFile.id} created"
      redirect( action: 'show', id: rasterFile.id )
    }
    else
    {
      render( view: 'create', model: [rasterFile: rasterFile] )
    }
  }
}
