import grails.converters.JSON

class CountryController
{

  static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

  def index = {
    redirect(action: "list", params: params)
  }

  def list = {
    params.max = Math.min(params.max ? params.int('max') : 10, 100)
    [countryInstanceList: Country.list(params), countryInstanceTotal: Country.count()]
  }

  def create = {
    def countryInstance = new Country()
    countryInstance.properties = params
    return [countryInstance: countryInstance]
  }

  def save = {
    def countryInstance = new Country(params)
    if ( countryInstance.save(flush: true) )
    {
      flash.message = "${message(code: 'default.created.message', args: [message(code: 'country.label', default: 'Country'), countryInstance.id])}"
      redirect(action: "show", id: countryInstance.id)
    }
    else
    {
      render(view: "create", model: [countryInstance: countryInstance])
    }
  }

  def show = {
    def countryInstance = Country.get(params.id)
    if ( !countryInstance )
    {
      flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'country.label', default: 'Country'), params.id])}"
      redirect(action: "list")
    }
    else
    {
      [countryInstance: countryInstance]
    }
  }

  def edit = {
    def countryInstance = Country.get(params.id)
    if ( !countryInstance )
    {
      flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'country.label', default: 'Country'), params.id])}"
      redirect(action: "list")
    }
    else
    {
      return [countryInstance: countryInstance]
    }
  }

  def update = {
    def countryInstance = Country.get(params.id)
    if ( countryInstance )
    {
      if ( params.version )
      {
        def version = params.version.toLong()
        if ( countryInstance.version > version )
        {

          countryInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'country.label', default: 'Country')] as Object[], "Another user has updated this Country while you were editing")
          render(view: "edit", model: [countryInstance: countryInstance])
          return
        }
      }
      countryInstance.properties = params
      if ( !countryInstance.hasErrors() && countryInstance.save(flush: true) )
      {
        flash.message = "${message(code: 'default.updated.message', args: [message(code: 'country.label', default: 'Country'), countryInstance.id])}"
        redirect(action: "show", id: countryInstance.id)
      }
      else
      {
        render(view: "edit", model: [countryInstance: countryInstance])
      }
    }
    else
    {
      flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'country.label', default: 'Country'), params.id])}"
      redirect(action: "list")
    }
  }

  def delete = {
    def countryInstance = Country.get(params.id)
    if ( countryInstance )
    {
      try
      {
        countryInstance.delete(flush: true)
        flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'country.label', default: 'Country'), params.id])}"
        redirect(action: "list")
      }
      catch (org.springframework.dao.DataIntegrityViolationException e)
      {
        flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'country.label', default: 'Country'), params.id])}"
        redirect(action: "show", id: params.id)
      }
    }
    else
    {
      flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'country.label', default: 'Country'), params.id])}"
      redirect(action: "list")
    }
  }

  def dataAsJSON = {
    params.max = Math.min(params.max ? params.int('max') : 10, 100)

    response.setHeader("Cache-Control", "no-store")

    def countryInstanceList = Country.list(params)
    def countryInstanceTotal = Country.count()

    def data = [
        countryInstanceTotal: countryInstanceTotal,
        results: countryInstanceList
    ]

    render data as JSON
  }
}
