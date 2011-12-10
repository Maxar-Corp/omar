package org.codehaus.groovy.grails.plugins.filter.controller

class FilterController
{

  def filterService

  def filter = {

    try
    {
      def result = filterService.filter(params)

      if ( request.getHeader("X-Requested-With") == "XMLHttpRequest" )
      {
        if ( params.plugin )
        {
          render(plugin: params.plugin, template: params.filterTemplate, model: result)
        }
        else
        {
          render(template: params.filterTemplate, model: result)
        }
      }
    }
    catch (IllegalArgumentException e)
    {
      response.status = 666
      render "<div class='filterError' id='filterError'>${message(code: 'Filter.error.' + e.message)}</div>"
      return
    }
  }
}
