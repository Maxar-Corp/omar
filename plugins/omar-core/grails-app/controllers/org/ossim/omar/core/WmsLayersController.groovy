package org.ossim.omar.core

import org.ossim.omar.core.WmsLayers

class WmsLayersController
{

  static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

  def index( )
  {
    redirect( action: "list", params: params )
  }

  def list( )
  {
    params.max = Math.min( params.max ? params.int( 'max' ) : 10, 100 )
    [wmsLayersInstanceList: WmsLayers.list( params ), wmsLayersInstanceTotal: WmsLayers.count()]
  }

  def create( )
  {
    def wmsLayersInstance = new WmsLayers()
    wmsLayersInstance.properties = params
    return [wmsLayersInstance: wmsLayersInstance]
  }

  def save( )
  {
    def wmsLayersInstance = new WmsLayers( params )
    if ( wmsLayersInstance.save( flush: true ) )
    {
      flash.message = "${message( code: 'default.created.message', args: [message( code: 'wmsLayers.label', default: 'WmsLayers' ), wmsLayersInstance.id] )}"
      redirect( action: "show", id: wmsLayersInstance.id )
    }
    else
    {
      render( view: "create", model: [wmsLayersInstance: wmsLayersInstance] )
    }
  }

  def show( )
  {
    def wmsLayersInstance = WmsLayers.get( params.id )
    if ( !wmsLayersInstance )
    {
      flash.message = "${message( code: 'default.not.found.message', args: [message( code: 'wmsLayers.label', default: 'WmsLayers' ), params.id] )}"
      redirect( action: "list" )
    }
    else
    {
      [wmsLayersInstance: wmsLayersInstance]
    }
  }

  def edit( )
  {
    def wmsLayersInstance = WmsLayers.get( params.id )
    if ( !wmsLayersInstance )
    {
      flash.message = "${message( code: 'default.not.found.message', args: [message( code: 'wmsLayers.label', default: 'WmsLayers' ), params.id] )}"
      redirect( action: "list" )
    }
    else
    {
      return [wmsLayersInstance: wmsLayersInstance]
    }
  }

  def update( )
  {
    def wmsLayersInstance = WmsLayers.get( params.id )
    if ( wmsLayersInstance )
    {
      if ( params.version )
      {
        def version = params.version.toLong()
        if ( wmsLayersInstance.version > version )
        {

          wmsLayersInstance.errors.rejectValue( "version", "default.optimistic.locking.failure", [message( code: 'wmsLayers.label', default: 'WmsLayers' )] as Object[], "Another user has updated this WmsLayers while you were editing" )
          render( view: "edit", model: [wmsLayersInstance: wmsLayersInstance] )
          return
        }
      }
      wmsLayersInstance.properties = params
      if ( !wmsLayersInstance.hasErrors() && wmsLayersInstance.save( flush: true ) )
      {
        flash.message = "${message( code: 'default.updated.message', args: [message( code: 'wmsLayers.label', default: 'WmsLayers' ), wmsLayersInstance.id] )}"
        redirect( action: "show", id: wmsLayersInstance.id )
      }
      else
      {
        render( view: "edit", model: [wmsLayersInstance: wmsLayersInstance] )
      }
    }
    else
    {
      flash.message = "${message( code: 'default.not.found.message', args: [message( code: 'wmsLayers.label', default: 'WmsLayers' ), params.id] )}"
      redirect( action: "list" )
    }
  }

  def delete( )
  {
    def wmsLayersInstance = WmsLayers.get( params.id )
    if ( wmsLayersInstance )
    {
      try
      {
        wmsLayersInstance.delete( flush: true )
        flash.message = "${message( code: 'default.deleted.message', args: [message( code: 'wmsLayers.label', default: 'WmsLayers' ), params.id] )}"
        redirect( action: "list" )
      }
      catch ( org.springframework.dao.DataIntegrityViolationException e )
      {
        flash.message = "${message( code: 'default.not.deleted.message', args: [message( code: 'wmsLayers.label', default: 'WmsLayers' ), params.id] )}"
        redirect( action: "show", id: params.id )
      }
    }
    else
    {
      flash.message = "${message( code: 'default.not.found.message', args: [message( code: 'wmsLayers.label', default: 'WmsLayers' ), params.id] )}"
      redirect( action: "list" )
    }
  }
}
