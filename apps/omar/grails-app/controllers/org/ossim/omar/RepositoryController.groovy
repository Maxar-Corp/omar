package org.ossim.omar

class RepositoryController
{

  def index = { redirect(action: list, params: params) }
  def stagerService

  // the delete, save and update actions only accept POST requests
  def static allowedMethods = [delete: 'POST', save: 'POST', update: 'POST']

  def list = {
    if ( !params.max )
    params.max = 10

    def repositoryList = Repository.createCriteria().list(params) {}

    [repositoryList: repositoryList]
  }

  def show = {
    def repository = Repository.get(params.id)

    if ( !repository )
    {
      flash.message = "Repository not found with id ${params.id}"
      redirect(action: list)
    }
    else
    { return [repository: repository] }
  }

  def delete = {
    def repository = Repository.get(params.id)

    if ( repository )
    {
      //println "Deleting: ${this}"

      Repository.withTransaction {
        def rasterDataSets = RasterDataSet.findAllByRepository(repository, [max: 10])

        while ( rasterDataSets?.size() > 0 )
        {
          rasterDataSets?.each {
            //println "\nDeleting ${it}"
            it.delete()
          }

          rasterDataSets = RasterDataSet.findAllByRepository(repository, [max: 10])
        }


        def videoDataSets = VideoDataSet.findAllByRepository(repository, [max: 10])

        while ( videoDataSets?.size() > 0 )
        {
          videoDataSets?.each {
            //println "\nDeleting ${it}"
            it.delete()
          }

          videoDataSets = VideoDataSet.findAllByRepository(repository, [max: 10])
        }

        repository.delete()
      }

      flash.message = "Repository ${params.id} deleted"
      redirect(action: list)
    }
    else
    {
      flash.message = "Repository not found with id ${params.id}"
      redirect(action: list)
    }
  }

  def edit = {
    def repository = Repository.get(params.id)

    if ( !repository )
    {
      flash.message = "Repository not found with id ${params.id}"
      redirect(action: list)
    }
    else
    {
      return [repository: repository]
    }
  }

  def update = {
    def repository = Repository.get(params.id)
    if ( repository )
    {
      repository.properties = params
      if ( !repository.hasErrors() && repository.save() )
      {
        flash.message = "Repository ${params.id} updated"
        redirect(action: show, id: repository.id)
      }
      else
      {
        render(view: 'edit', model: [repository: repository])
      }
    }
    else
    {
      flash.message = "Repository not found with id ${params.id}"
      redirect(action: edit, id: params.id)
    }
  }

  def create = {
    def repository = new Repository()
    repository.properties = params
    return ['repository': repository]
  }

  def save = {
    def repository = new Repository(params)
    if ( !repository.hasErrors() && repository.save() )
    {
      flash.message = "Repository ${repository.id} created"
      redirect(action: show, id: repository.id)
    }
    else
    {
      render(view: 'create', model: [repository: repository])
    }
  }

  def runStager = {


    def repository = Repository.get(params.id)


    if ( !repository )
    {
      flash.message = "Repository not found with id ${params.id}"
      redirect(action: list)
    }
    else
    {
      //repository.scanStartDate = new Date()

      //repository.save()
      //println "Before"
      stagerService.runStager(repository)
      //println "After"

      redirect(action: show, id: params.id)
    }
  }
}
