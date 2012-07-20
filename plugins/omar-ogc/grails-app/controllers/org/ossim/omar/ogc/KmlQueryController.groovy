package org.ossim.omar.ogc

class KmlQueryController
{
  def grailsApplication

  def kmlPersistParams = ["googleversion", "visibility"]

  def getkml( )
  {
    // let's just reuse the getImagesKml code
    //redirect(controller: "kmlQuery", action: "getImagesKml", params:params)

    println "KmlQueryController.getkml: SHOULD NEVER SEE THIS!!!!!!!!!!!!!!!!!!!!!!!!!"


    try
    {
      forward( controller: "kmlQuery", action: "getImagesKml", params: params )
    }
    catch ( Exception e )
    {}
  }
}
