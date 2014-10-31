package org.ossim.omar

import grails.converters.JSON
import grails.converters.XML
import grails.plugins.springsecurity.Secured
import org.ossim.omar.chipper.FetchDataCommand
import org.apache.commons.collections.map.CaseInsensitiveMap

class DiskCacheController {

  def diskCacheService

  @Secured(['ROLE_USER', 'ROLE_ADMIN'])
  def index() {
    render view: 'index', model:[
            tableModel  : diskCacheService.createTableModel()
    ]
  }

  @Secured(['IS_AUTHENTICATED_ANONYMOUSLY'])
  def list(FetchDataCommand cmd){
    def data = diskCacheService.list( cmd )
    response.withFormat {
      json {
        render contentType: 'application/json', text: data as JSON
      }
      xml {
        render contentType: 'application/xml', text: data as XML
      }
    }
  }

  @Secured(['ROLE_ADMIN'])
  def create(){
    println params
    def data = diskCacheService.create(params)//new CaseInsensitiveMap(params));
    response.withFormat {
      json {
        render contentType: 'application/json', text: data as JSON
      }
      xml {
        render contentType: 'application/xml', text: data as XML
      }
    }
  }

  @Secured(['ROLE_ADMIN'])
  def update(){
    def data = diskCacheService.update(params)//new CaseInsensitiveMap(params));
    println  "BEST LOCATION: ${diskCacheService.getBestLocation()}"
    response.withFormat {
      json {
        render contentType: 'application/json', text: data as JSON
      }
      xml {
        render contentType: 'application/xml', text: data as XML
      }
    }
  }

  @Secured(['ROLE_ADMIN'])
  def remove(){
    def data = diskCacheService.remove(new CaseInsensitiveMap(params));
    response.withFormat {
      json {
        render contentType: 'application/json', text: data as JSON
      }
      xml {
        render contentType: 'application/xml', text: data as XML
      }
    }
  }
}
