package org.ossim.omar.core

import org.springframework.context.ApplicationContextAware
import org.springframework.context.ApplicationContext

class RepositoryController implements ApplicationContextAware
{
  def grailsApplication
  def applicationContext

  def index( )
  { redirect( action: 'list', params: params ) }

  def scripts( )
  { }

  def stagerService

  // the delete, save and update actions only accept POST requests
  def static allowedMethods = [delete: 'POST', save: 'POST', update: 'POST']

  def list( )
  {
    if ( !params.max )
      params.max = 10

    def repositoryList = Repository.createCriteria().list( params ) {}

    [repositoryList: repositoryList]
  }

  def show( )
  {
    def repository = Repository.get( params.id )

    if ( !repository )
    {
      flash.message = "Repository not found with id ${params.id}"
      redirect( action: 'list' )
    }
    else
    { return [repository: repository] }
  }

  def delete( )
  {
    def repository = Repository.get( params.id )

    if ( repository )
    {
      def dataSetClasses = grailsApplication.getArtefacts( "Domain" ).grep { it.name ==~ /.*DataSet/ }

      Repository.withTransaction {

        dataSetClasses?.each { dataSetClass ->
          def service = applicationContext.getBean( "${dataSetClass.propertyName}Service" )

          service?.deleteFromRepository( repository )
        }

        repository.delete()
      }

      flash.message = "Repository ${params.id} deleted"
      redirect( action: 'list' )
    }
    else
    {
      flash.message = "Repository not found with id ${params.id}"
      redirect( action: 'list' )
    }
  }

  def edit( )
  {
    def repository = Repository.get( params.id )

    if ( !repository )
    {
      flash.message = "Repository not found with id ${params.id}"
      redirect( action: 'list' )
    }
    else
    {
      return [repository: repository]
    }
  }

  def update( )
  {
    def repository = Repository.get( params.id )
    if ( repository )
    {
      repository.properties = params
      if ( !repository.hasErrors() && repository.save() )
      {
        flash.message = "Repository ${params.id} updated"
        redirect( action: show, id: repository.id )
      }
      else
      {
        render( view: 'edit', model: [repository: repository] )
      }
    }
    else
    {
      flash.message = "Repository not found with id ${params.id}"
      redirect( action: 'edit', id: params.id )
    }
  }

  def create( )
  {
    def repository = new Repository()
    repository.properties = params
    return ['repository': repository]
  }

  def save( )
  {
    def repository = new Repository( params )
    if ( !repository.hasErrors() && repository.save() )
    {
      flash.message = "Repository ${repository.id} created"
      redirect( action: 'show', id: repository.id )
    }
    else
    {
      render( view: 'create', model: [repository: repository] )
    }
  }

  def runStager( )
  {


    def repository = Repository.get( params.id )


    if ( !repository )
    {
      flash.message = "Repository not found with id ${params.id}"
      redirect( action: 'list' )
    }
    else
    {
      //repository.scanStartDate = new Date()

      //repository.save()
      //println "Before"
      stagerService.runStager( repository )
      //println "After"

      redirect( action: 'show', id: params.id )
    }
  }

  

  void setApplicationContext( ApplicationContext applicationContext )
  {
    this.applicationContext = applicationContext
  }
}
