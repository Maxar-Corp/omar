package org.ossim.omar.security

import org.springframework.dao.DataIntegrityViolationException

import org.grails.plugin.filterpane.FilterPaneUtils

class RequestmapController
{
  def filterPaneService

  static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

  def index( )
  {
    redirect( action: "list", params: params )
  }

  def list( )
  {
    if ( !params.max )
    {
      params.max = 10
    }
    render( view: 'list',
            model: [requestmapInstanceList: filterPaneService.filter( params, Requestmap ),
                    requestmapInstanceTotal: filterPaneService.count( params, Requestmap ),
                    filterParams: FilterPaneUtils.extractFilterParams( params ), params: params] )
  }

  def filter( )
  {
    if ( !params.max )
    {
      params.max = 10
    }
    render( view: 'list',
            model: [requestmapInstanceList: filterPaneService.filter( params, Requestmap ),
                    requestmapInstanceTotal: filterPaneService.count( params, Requestmap ),
                    filterParams: FilterPaneUtils.extractFilterParams( params ),
                    params: params] )
  }


  def create( )
  {
    [requestmapInstance: new Requestmap( params )]
  }

  def save( )
  {
    def requestmapInstance = new Requestmap( params )
    if ( !requestmapInstance.save( flush: true ) )
    {
      render( view: "create", model: [requestmapInstance: requestmapInstance] )
      return
    }

    flash.message = message( code: 'default.created.message', args: [message( code: 'requestmap.label', default: 'Requestmap' ), requestmapInstance.id] )
    redirect( action: "show", id: requestmapInstance.id )
  }

  def show( )
  {
    def requestmapInstance = Requestmap.get( params.id )
    if ( !requestmapInstance )
    {
      flash.message = message( code: 'default.not.found.message', args: [message( code: 'requestmap.label', default: 'Requestmap' ), params.id] )
      redirect( action: "list" )
      return
    }

    [requestmapInstance: requestmapInstance]
  }

  def edit( )
  {
    def requestmapInstance = Requestmap.get( params.id )
    if ( !requestmapInstance )
    {
      flash.message = message( code: 'default.not.found.message', args: [message( code: 'requestmap.label', default: 'Requestmap' ), params.id] )
      redirect( action: "list" )
      return
    }

    [requestmapInstance: requestmapInstance]
  }

  def update( )
  {
    def requestmapInstance = Requestmap.get( params.id )
    if ( !requestmapInstance )
    {
      flash.message = message( code: 'default.not.found.message', args: [message( code: 'requestmap.label', default: 'Requestmap' ), params.id] )
      redirect( action: "list" )
      return
    }

    if ( params.version )
    {
      def version = params.version.toLong()
      if ( requestmapInstance.version > version )
      {
        requestmapInstance.errors.rejectValue( "version", "default.optimistic.locking.failure",
                [message( code: 'requestmap.label', default: 'Requestmap' )] as Object[],
                "Another user has updated this Requestmap while you were editing" )
        render( view: "edit", model: [requestmapInstance: requestmapInstance] )
        return
      }
    }

    requestmapInstance.properties = params

    if ( !requestmapInstance.save( flush: true ) )
    {
      render( view: "edit", model: [requestmapInstance: requestmapInstance] )
      return
    }

    flash.message = message( code: 'default.updated.message', args: [message( code: 'requestmap.label', default: 'Requestmap' ), requestmapInstance.id] )
    redirect( action: "show", id: requestmapInstance.id )
  }

  def delete( )
  {
    def requestmapInstance = Requestmap.get( params.id )
    if ( !requestmapInstance )
    {
      flash.message = message( code: 'default.not.found.message', args: [message( code: 'requestmap.label', default: 'Requestmap' ), params.id] )
      redirect( action: "list" )
      return
    }

    try
    {
      requestmapInstance.delete( flush: true )
      flash.message = message( code: 'default.deleted.message', args: [message( code: 'requestmap.label', default: 'Requestmap' ), params.id] )
      redirect( action: "list" )
    }
    catch ( DataIntegrityViolationException e )
    {
      flash.message = message( code: 'default.not.deleted.message', args: [message( code: 'requestmap.label', default: 'Requestmap' ), params.id] )
      redirect( action: "show", id: params.id )
    }
  }
}
