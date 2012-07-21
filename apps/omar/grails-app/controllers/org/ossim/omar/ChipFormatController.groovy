package org.ossim.omar

import org.springframework.dao.DataIntegrityViolationException


class ChipFormatController
{

  static allowedMethods = [save: "POST", update: "POST", delete: "POST"]


  def index( )
  {
    redirect( action: "list", params: params )
  }

  def list( )
  {
    params.max = Math.min( params.max ? params.int( 'max' ) : 10, 100 )

    def chipFormatInstanceList
    def chipFormatInstanceTotal

    chipFormatInstanceList = ChipFormat.list( params )
    chipFormatInstanceTotal = ChipFormat.count()

    [chipFormatInstanceList: chipFormatInstanceList, chipFormatInstanceTotal: chipFormatInstanceTotal]
  }

  def create( )
  {
    def chipFormat = new ChipFormat()
    chipFormat.properties = params
    [chipFormat: chipFormat]
  }

  def save( )
  {
    def chipFormat = new ChipFormat( params )
    if ( !chipFormat.save() )
    {
      render( view: "create", model: [chipFormat: chipFormat] )
      return
    }

    flash.message = message( code: 'default.created.message', args: [message( code: 'chipFormat.label', default: 'ChipFormat' ), chipFormat.id] )
    redirect( action: "show", id: chipFormat.id )
  }

  def show( )
  {
    def chipFormat = ChipFormat.get( params.id )
    if ( !chipFormat )
    {
      flash.message = message( code: 'default.not.found.message', args: [message( code: 'chipFormat.label', default: 'ChipFormat' ), params.id] )
      redirect( action: "list" )
      return
    }

    [chipFormat: chipFormat]
  }

  def edit( )
  {
    def chipFormat = ChipFormat.get( params.id )
    if ( !chipFormat )
    {
      flash.message = message( code: 'default.not.found.message', args: [message( code: 'chipFormat.label', default: 'ChipFormat' ), params.id] )
      redirect( action: "list" )
      return
    }

    [chipFormat: chipFormat]
  }

  def update( )
  {
    def chipFormat = ChipFormat.get( params.id )
    if ( chipFormat )
    {
      chipFormat.properties = params
      if ( !chipFormat.hasErrors() && chipFormat.save() )
      {
        flash.message = "ChipFormat ${params.id} updated"
        redirect( action: 'show', id: chipFormat.id )
      }
      else
      {
        render( view: 'edit', model: [chipFormat: chipFormat] )
      }
    }
    else
    {
      flash.message = "Chip format not found with id ${params.id}"
      redirect( action: 'edit', id: params.id )
    }
  }

  def delete( )
  {
    def chipFormat = ChipFormat.get( params.id )
    if ( !chipFormat )
    {
      flash.message = message( code: 'default.not.found.message', args: [message( code: 'chipFormat.label', default: 'ChipFormat' ), params.id] )
      redirect( action: "list" )
      return
    }

    try
    {
      chipFormat.delete()
      flash.message = message( code: 'default.deleted.message', args: [message( code: 'chipFormat.label', default: 'ChipFormat' ), params.id] )
      redirect( action: "list" )
    }
    catch ( DataIntegrityViolationException e )
    {
      flash.message = message( code: 'default.not.deleted.message', args: [message( code: 'chipFormat.label', default: 'ChipFormat' ), params.id] )
      redirect( action: "show", id: params.id )
    }
  }
}
