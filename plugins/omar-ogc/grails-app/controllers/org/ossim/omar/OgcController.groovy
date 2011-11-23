package org.ossim.omar

class OgcController
{
  def grailsApplication
  def ogcExceptionService

  def springSecurityService
  def kmlService


  def getTile = {
    log.warn("OgcController getTile is deprecated and image space operations should go through ../icp/getTile\ninstead of /ogc/getTile")
    redirect(controller: "icp", action: "getTile", params: params)
  }
}
