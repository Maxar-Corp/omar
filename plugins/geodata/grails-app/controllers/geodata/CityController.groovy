package geodata

import org.grails.plugin.filterpane.FilterPaneUtils

import org.springframework.dao.DataIntegrityViolationException

class CityController
{

  static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

  def filterPaneService


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
            model: [cityInstanceList: filterPaneService.filter( params, City ),
                    cityInstanceTotal: filterPaneService.count( params, City ),
                    filterParams: FilterPaneUtils.extractFilterParams( params ), params: params] )
  }

  def filter( )
  {
    if ( !params.max )
    {
      params.max = 10
    }
    render( view: 'list',
            model: [cityInstanceList: filterPaneService.filter( params, City ),
                    cityInstanceTotal: filterPaneService.count( params, City ),
                    filterParams: FilterPaneUtils.extractFilterParams( params ),
                    params: params] )
  }


  def create( )
  {
    [cityInstance: new City( params )]
  }

  def save( )
  {
    def cityInstance = new City( params )
    if ( !cityInstance.save( flush: true ) )
    {
      render( view: "create", model: [cityInstance: cityInstance] )
      return
    }

    flash.message = message( code: 'default.created.message', args: [message( code: 'city.label', default: 'City' ), cityInstance.id] )
    redirect( action: "show", id: cityInstance.id )
  }

  def show( )
  {
    def cityInstance = City.get( params.id )
    if ( !cityInstance )
    {
      flash.message = message( code: 'default.not.found.message', args: [message( code: 'city.label', default: 'City' ), params.id] )
      redirect( action: "list" )
      return
    }

    [cityInstance: cityInstance]
  }

  def edit( )
  {
    def cityInstance = City.get( params.id )
    if ( !cityInstance )
    {
      flash.message = message( code: 'default.not.found.message', args: [message( code: 'city.label', default: 'City' ), params.id] )
      redirect( action: "list" )
      return
    }

    [cityInstance: cityInstance]
  }

  def update( )
  {
    def cityInstance = City.get( params.id )
    if ( !cityInstance )
    {
      flash.message = message( code: 'default.not.found.message', args: [message( code: 'city.label', default: 'City' ), params.id] )
      redirect( action: "list" )
      return
    }

    if ( params.version )
    {
      def version = params.version.toLong()
      if ( cityInstance.version > version )
      {
        cityInstance.errors.rejectValue( "version", "default.optimistic.locking.failure",
                [message( code: 'city.label', default: 'City' )] as Object[],
                "Another user has updated this City while you were editing" )
        render( view: "edit", model: [cityInstance: cityInstance] )
        return
      }
    }

    cityInstance.properties = params

    if ( !cityInstance.save( flush: true ) )
    {
      render( view: "edit", model: [cityInstance: cityInstance] )
      return
    }

    flash.message = message( code: 'default.updated.message', args: [message( code: 'city.label', default: 'City' ), cityInstance.id] )
    redirect( action: "show", id: cityInstance.id )
  }

  def delete( )
  {
    def cityInstance = City.get( params.id )
    if ( !cityInstance )
    {
      flash.message = message( code: 'default.not.found.message', args: [message( code: 'city.label', default: 'City' ), params.id] )
      redirect( action: "list" )
      return
    }

    try
    {
      cityInstance.delete( flush: true )
      flash.message = message( code: 'default.deleted.message', args: [message( code: 'city.label', default: 'City' ), params.id] )
      redirect( action: "list" )
    }
    catch ( DataIntegrityViolationException e )
    {
      flash.message = message( code: 'default.not.deleted.message', args: [message( code: 'city.label', default: 'City' ), params.id] )
      redirect( action: "show", id: params.id )
    }
  }
}
