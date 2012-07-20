package org.ossim.omar.raster

import org.ossim.omar.raster.RasterEntry
import org.ossim.omar.raster.RasterEntryFile

class RasterEntryFileController
{

  def index( )
  { redirect( action: list, params: params ) }

  // the delete, save and update actions only accept POST requests
  def static allowedMethods = [delete: 'POST', save: 'POST', update: 'POST']

  def list( )
  {
    if ( !params.max )
      params.max = 10

    def rasterEntryFileList = null

    if ( params.rasterEntryId )
    {
      def rasterEntry = RasterEntry.get( params.rasterEntryId )
      rasterEntryFileList = RasterEntryFile.createCriteria().list( params ) {
        eq( "rasterEntry", rasterEntry )
      }
    }
    else
    {
      rasterEntryFileList = RasterEntryFile.createCriteria().list( params ) {}
    }

    [rasterEntryFileList: rasterEntryFileList]
  }

  def show( )
  {
    def rasterEntryFile = RasterEntryFile.get( params.id )

    if ( !rasterEntryFile )
    {
      flash.message = "RasterEntryFile not found with id ${params.id}"
      redirect( action: list )
    }
    else
    { return [rasterEntryFile: rasterEntryFile] }
  }

  def delete( )
  {
    def rasterEntryFile = RasterEntryFile.get( params.id )
    if ( rasterEntryFile )
    {
      rasterEntryFile.delete()
      flash.message = "RasterEntryFile ${params.id} deleted"
      redirect( action: list )
    }
    else
    {
      flash.message = "RasterEntryFile not found with id ${params.id}"
      redirect( action: list )
    }
  }

  def edit( )
  {
    def rasterEntryFile = RasterEntryFile.get( params.id )

    if ( !rasterEntryFile )
    {
      flash.message = "RasterEntryFile not found with id ${params.id}"
      redirect( action: list )
    }
    else
    {
      return [rasterEntryFile: rasterEntryFile]
    }
  }

  def update( )
  {
    def rasterEntryFile = RasterEntryFile.get( params.id )
    if ( rasterEntryFile )
    {
      rasterEntryFile.properties = params
      if ( !rasterEntryFile.hasErrors() && rasterEntryFile.save() )
      {
        flash.message = "RasterEntryFile ${params.id} updated"
        redirect( action: show, id: rasterEntryFile.id )
      }
      else
      {
        render( view: 'edit', model: [rasterEntryFile: rasterEntryFile] )
      }
    }
    else
    {
      flash.message = "RasterEntryFile not found with id ${params.id}"
      redirect( action: edit, id: params.id )
    }
  }

  def create( )
  {
    def rasterEntryFile = new RasterEntryFile()
    rasterEntryFile.properties = params
    return ['rasterEntryFile': rasterEntryFile]
  }

  def save( )
  {
    def rasterEntryFile = new RasterEntryFile( params )
    if ( !rasterEntryFile.hasErrors() && rasterEntryFile.save() )
    {
      flash.message = "RasterEntryFile ${rasterEntryFile.id} created"
      redirect( action: show, id: rasterEntryFile.id )
    }
    else
    {
      render( view: 'create', model: [rasterEntryFile: rasterEntryFile] )
    }
  }
}
