package org.ossim.omar.app
import grails.converters.JSON
import org.apache.commons.collections.map.CaseInsensitiveMap
import org.ossim.omar.core.Utility
import org.ossim.omar.Job

class ProductController {
  def springSecurityService
  def grailsApplication
  def jobService
  def productService

  def index()
  {
    render view: 'index', model: [params:params,
                                  tableModel  : jobService.createTableModel()
    ]
  }


  def submitJob()
  {
    println "PARAMS =========== ${params}"
    def tempParams = new HashMap( params )
    if(springSecurityService?.isLoggedIn())
      tempParams.username = springSecurityService?.principal?.username
    switch (request.method.toUpperCase())
    {
      case "POST":
        //println request.JSON
        break
      case "GET":
        //println "GET"
        break
    }

    def jobResult = productService.newProduct(tempParams)
    def result
    if(!jobResult)
    {
      result = [] as JSON
    }
    else
    {
      result = [jobId : jobResult.jobId.toString()] as JSON
    }

    def callback = ""
    if ( tempParams.callback )
    {
      callback = tempParams.callback
    }
    else if ( tempParams.jsonCallback )
    {
      callback = tempParams.jsonCallback
    }
    if ( callback )
    {
      result = "${callback}(${result})"// added for cross domain support
    }
    render contentType: 'application/json', text: result.toString()
  }
}