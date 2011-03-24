package org.ossim.omar

class OgcExtendController extends OgcController{

    def wmsFilter = {
      if(params.redirected)
      {
        println "REDIRECTED TO HERE"
        wms()
      }
      else
      {
        println "REDIRECTING"
        params.redirected=true
        redirect(controller:"ogc", action:"wms", params:params)
        null
      }
    }
}
