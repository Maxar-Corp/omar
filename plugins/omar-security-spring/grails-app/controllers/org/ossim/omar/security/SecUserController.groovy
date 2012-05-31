package org.ossim.omar.security

import au.com.bytecode.opencsv.CSVWriter
import org.grails.plugin.filterpane.FilterPaneUtils

class SecUserController
{

  static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

  def filterPaneService

  def index = {
    redirect( action: "list", params: params )
  }

  def list( )
  {
    if ( !params.max )
    {
      params.max = 10
    }
    render( view: 'list',
            model: [secUserInstanceList: filterPaneService.filter( params, SecUser ),
                    secUserInstanceTotal: filterPaneService.count( params, SecUser ),
                    filterParams: FilterPaneUtils.extractFilterParams( params ), params: params] )
  }

  def filter( )
  {
    if ( !params.max )
    {
      params.max = 10
    }
    render( view: 'list',
            model: [secUserInstanceList: filterPaneService.filter( params, SecUser ),
                    secUserInstanceTotal: filterPaneService.count( params, SecUser ),
                    filterParams: FilterPaneUtils.extractFilterParams( params ),
                    params: params] )
  }

  def create( )
  {
      redirect( controller:  "Register", action: "register", params: params )
  }

  def save( )
  {
    def secUserInstance = new SecUser( params )
    if ( secUserInstance.save( flush: true ) )
    {
      flash.message = "${message( code: 'default.created.message', args: [message( code: 'secUser.label', default: 'SecUser' ), secUserInstance.id] )}"
      redirect( action: "show", id: secUserInstance.id )
    }
    else
    {
      render( view: "create", model: [secUserInstance: secUserInstance] )
    }
  }

  def show( )
  {
    def secUserInstance = SecUser.get( params.id )
    if ( !secUserInstance )
    {
      flash.message = "${message( code: 'default.not.found.message', args: [message( code: 'secUser.label', default: 'SecUser' ), params.id] )}"
      redirect( action: "list" )
    }
    else
    {
      [secUserInstance: secUserInstance]
    }
  }

  def edit( )
  {
    def secUserInstance = SecUser.get( params.id )
    if ( !secUserInstance )
    {
      flash.message = "${message( code: 'default.not.found.message', args: [message( code: 'secUser.label', default: 'SecUser' ), params.id] )}"
      redirect( action: "list" )
    }
    else
    {
      return [secUserInstance: secUserInstance]
    }
  }

  def update( )
  {
    def secUserInstance = SecUser.get( params.id )
    if ( secUserInstance )
    {
      if ( params.version )
      {
        def version = params.version.toLong()
        if ( secUserInstance.version > version )
        {

          secUserInstance.errors.rejectValue( "version", "default.optimistic.locking.failure", [message( code: 'secUser.label', default: 'SecUser' )] as Object[], "Another user has updated this SecUser while you were editing" )
          render( view: "edit", model: [secUserInstance: secUserInstance] )
          return
        }
      }

      secUserInstance.properties = params

      //println params.sort()

      def roleNames = params.keySet().grep { it.startsWith( "ROLE_" )}

      SecUserSecRole.removeAll( secUserInstance )
      roleNames?.each { SecUserSecRole.create( secUserInstance, SecRole.findByAuthority( it ) )}

      if ( !secUserInstance.hasErrors() && secUserInstance.save( flush: true ) )
      {
        flash.message = "${message( code: 'default.updated.message', args: [message( code: 'secUser.label', default: 'SecUser' ), secUserInstance.id] )}"
        redirect( action: "show", id: secUserInstance.id )
      }
      else
      {
        render( view: "edit", model: [secUserInstance: secUserInstance] )
      }
    }
    else
    {
      flash.message = "${message( code: 'default.not.found.message', args: [message( code: 'secUser.label', default: 'SecUser' ), params.id] )}"
      redirect( action: "list" )
    }
  }

  def delete( )
  {
    def secUserInstance = SecUser.get( params.id )
    if ( secUserInstance )
    {
      try
      {
        SecUser.withTransaction {
          SecUserSecRole.removeAll( secUserInstance )
          secUserInstance.delete( flush: true )
        }

        flash.message = "${message( code: 'default.deleted.message', args: [message( code: 'secUser.label', default: 'SecUser' ), params.id] )}"
        redirect( action: "list" )
      }
      catch ( org.springframework.dao.DataIntegrityViolationException e )
      {
        flash.message = "${message( code: 'default.not.deleted.message', args: [message( code: 'secUser.label', default: 'SecUser' ), params.id] )}"
        redirect( action: "show", id: params.id )
      }
    }
    else
    {
      flash.message = "${message( code: 'default.not.found.message', args: [message( code: 'secUser.label', default: 'SecUser' ), params.id] )}"
      redirect( action: "list" )
    }
  }

  def export( )
  {
    def users = filterPaneService.filter( params, SecUser )

    def labels = ['Id', 'Username', 'Real Name', 'Organization', 'Phone Number', 'E-mail', 'Enabled', 'Account Locked', 'Account Expired', 'Password Expired']
    def fields = ['id', 'username', 'userRealName', 'organization', 'phoneNumber', 'email', 'enabled', 'accountLocked', 'accountExpired', 'passwordExpired']
    def formatters = [:]


    def prefix = "omar-users-"
    def workDir = grailsApplication.config.export.workDir ?: "/tmp"

    def csvFile = File.createTempFile( prefix, ".csv", workDir as File )
    def csvWriter = new CSVWriter( csvFile.newWriter() )

    csvWriter.writeNext( labels as String[] )


    for ( user in users )
    {
      def data = []
      for ( field in fields )
      {

        if ( formatters[field] )
        {
          data << formatters[field].call( user[field] )
        }
        else
        {
          data << user[field]
        }
      }

      csvWriter.writeNext( data as String[] )
    }

    csvWriter.close()

    response.setHeader( "Content-disposition", "attachment; filename=${csvFile.name}" );
    response.contentType = "text/csv"
    response.outputStream << csvFile.newInputStream()
    response.outputStream.flush()
  }
}
