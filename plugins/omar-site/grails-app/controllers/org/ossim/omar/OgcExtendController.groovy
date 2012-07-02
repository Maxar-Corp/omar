package org.ossim.omar

import org.ossim.omar.ogc.OgcController

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
