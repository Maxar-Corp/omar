package org.ossim.omar.app
import grails.converters.JSON
import org.apache.commons.collections.map.CaseInsensitiveMap

class ProductController {

  def grailsApplication

  def productService

  def index()
  {
    render view: 'index', model: [params:params]
  }

  def submitJob()
  {
    def caseInsensitiveParams = new CaseInsensitiveMap( params )

    switch (request.method.toUpperCase())
    {
      case "POST":
        //println request.JSON
        break
      case "GET":
        //println "GET"
        break
    }

    def jobResult = productService.newProduct(caseInsensitiveParams)
    
    def result = [jobId : jobResult.jobId.toString()] as JSON   //jabberFederatedServerService.serverList as JSON

    def callback = ""
    if ( caseInsensitiveParams.callback )
    {
      callback = caseInsensitiveParams.callback
    }
    else if ( caseInsensitiveParams.jsonCallback )
    {
      callback = caseInsensitiveParams.jsonCallback
    }
    if ( callback )
    {
      result = "${callback}(${result})"// added for cross domain support
    }
    render contentType: 'application/json', text: result.toString()
  }
}