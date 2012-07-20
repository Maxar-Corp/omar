package org.ossim.omar.core

import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

import org.ossim.omar.security.SecUser

class ReportController
{

  static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

  def springSecurityService

  def index( )
  {
    redirect( action: "list", params: params )
  }

  def list( )
  {
    params.max = Math.min( params.max ? params.int( 'max' ) : 10, 100 )

    def reportInstanceList
    def reportInstanceTotal

    if ( SpringSecurityUtils.ifAllGranted( "ROLE_ADMIN" ) )
    {
      reportInstanceList = Report.list( params )
      reportInstanceTotal = Report.count()
    }
    else
    {
      reportInstanceList = Report.createCriteria().list( params ) {
        eq( "name", springSecurityService.principal.username )
      }

      reportInstanceTotal = reportInstanceList.totalCount
    }

    [reportInstanceList: reportInstanceList, reportInstanceTotal: reportInstanceTotal]
  }

  def create( )
  {
    def user = SecUser.findByUsername( springSecurityService.principal.username )

    def reportInstance = new Report(
            name: user.username,
            email: user.email,
            phone: user.phoneNumber
    )
    reportInstance.properties = params
    return [reportInstance: reportInstance]
  }

  def save( )
  {
    def reportInstance = new Report( params )
    def user = SecUser.findByUsername( springSecurityService.principal.username )

    if ( user.username == reportInstance.name || SpringSecurityUtils.ifAllGranted( "ROLE_ADMIN" ) )
    {
      if ( reportInstance.save( flush: true ) )
      {
        flash.message = "Thank you for comments. Someone should respond to you as soon as possible."
        //flash.message = "${message(code: 'default.created.message', args: [message(code: 'report.label', default: 'Report'), reportInstance.id])}"
        //redirect(action: "show", id: reportInstance.id)
        redirect( controller: "home" )
      }
      else
      {
        render( view: "create", model: [reportInstance: reportInstance] )
      }
    }
    else
    {
      flash.message = "Not authorized to save that record"
      redirect( controller: "home" )
    }
  }

  def show( )
  {
    def reportInstance = Report.get( params.id )
    def user = SecUser.findByUsername( springSecurityService.principal.username )

    if ( user.username == reportInstance.name || SpringSecurityUtils.ifAllGranted( "ROLE_ADMIN" ) )
    {

      if ( !reportInstance )
      {
        flash.message = "${message( code: 'default.not.found.message', args: [message( code: 'report.label', default: 'Report' ), params.id] )}"
        redirect( action: "list" )
      }
      else
      {
        return [reportInstance: reportInstance]
      }
    }
    else
    {
      flash.message = "Not authorized to see that record"
      redirect( controller: "home" )
    }
  }

  def edit( )
  {
    def reportInstance = Report.get( params.id )
    if ( !reportInstance )
    {
      flash.message = "${message( code: 'default.not.found.message', args: [message( code: 'report.label', default: 'Report' ), params.id] )}"
      redirect( action: "list" )
    }
    else
    {
      def user = SecUser.findByUsername( springSecurityService.principal.username )

      if ( user.username == reportInstance.name || SpringSecurityUtils.ifAllGranted( "ROLE_ADMIN" ) )
      {
        return [reportInstance: reportInstance]
      }
      else
      {
        flash.message = "Not authorized to modify that record"
        redirect( controller: "home" )
      }
    }
  }

  def update( )
  {
    def reportInstance = Report.get( params.id )
    if ( reportInstance )
    {
      def user = SecUser.findByUsername( springSecurityService.principal.username )

      if ( user.username == reportInstance.name || SpringSecurityUtils.ifAllGranted( "ROLE_ADMIN" ) )
      {

        if ( params.version )
        {
          def version = params.version.toLong()
          if ( reportInstance.version > version )
          {

            reportInstance.errors.rejectValue( "version", "default.optimistic.locking.failure", [message( code: 'report.label', default: 'Report' )] as Object[], "Another user has updated this Report while you were editing" )
            render( view: "edit", model: [reportInstance: reportInstance] )
            return
          }
        }

        reportInstance.properties = params

        if ( reportInstance.status == "null" )
        {
          reportInstance.status = null
        }

        if ( !reportInstance.hasErrors() && reportInstance.save( flush: true ) )
        {
          flash.message = "${message( code: 'default.updated.message', args: [message( code: 'report.label', default: 'Report' ), reportInstance.id] )}"
          redirect( action: "show", id: reportInstance.id )
        }
        else
        {
          render( view: "edit", model: [reportInstance: reportInstance] )
        }
      }
      else
      {
        flash.message = "Not authorized to update that record"
        redirect( controller: "home" )
      }
    }
    else
    {
      flash.message = "${message( code: 'default.not.found.message', args: [message( code: 'report.label', default: 'Report' ), params.id] )}"
      redirect( action: "list" )
    }
  }

  def delete( )
  {
    if ( SpringSecurityUtils.ifAllGranted( "ROLE_ADMIN" ) )
    {
      def reportInstance = Report.get( params.id )
      if ( reportInstance )
      {
        try
        {
          reportInstance.delete( flush: true )
          flash.message = "${message( code: 'default.deleted.message', args: [message( code: 'report.label', default: 'Report' ), params.id] )}"
          redirect( action: "list" )
        }
        catch ( org.springframework.dao.DataIntegrityViolationException e )
        {
          flash.message = "${message( code: 'default.not.deleted.message', args: [message( code: 'report.label', default: 'Report' ), params.id] )}"
          redirect( action: "show", id: params.id )
        }
      }
      else
      {
        flash.message = "${message( code: 'default.not.found.message', args: [message( code: 'report.label', default: 'Report' ), params.id] )}"
        redirect( action: "list" )
      }
    }
    else
    {
      flash.message = "Only admins can delete reports"
      redirect( controller: "home" )
    }
  }
}
