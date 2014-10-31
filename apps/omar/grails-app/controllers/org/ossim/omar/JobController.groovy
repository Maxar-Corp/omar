package org.ossim.omar
import grails.converters.JSON
import org.ossim.omar.chipper.FetchDataCommand
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import grails.plugins.springsecurity.Secured

@Secured(['IS_AUTHENTICATED_ANONYMOUSLY'])
class JobController {
  def springSecurityService
  def jobService

  @Secured(['ROLE_USER', 'ROLE_ADMIN'])
  def index() {
    render view: 'index', model:[
                tableModel  : jobService.createTableModel()
           ]
  }
  @Secured(['IS_AUTHENTICATED_ANONYMOUSLY'])
  def getData(FetchDataCommand cmd)
  {
    def usernameRestriction
    if ( SpringSecurityUtils.ifNotGranted( "ROLE_ADMIN" ) )
    {
     // println      springSecurityService.principal.username
      if(springSecurityService.isLoggedIn())
      {
        usernameRestriction = "(username='${springSecurityService.principal.username}')"
      }
      else
      {
        usernameRestriction = "(username='anonymous')"
      }
      if(usernameRestriction)
      {
        if(!cmd.filter)
        {
          cmd.filter = "${usernameRestriction}"
        }
        else
        {
          cmd.filter = "${cmd.filter} AND ${usernameRestriction}"
        }
      }
    }

   // println "-------------------------${cmd.filter}"
    def data = jobService.getData( cmd )
    render contentType: 'application/json', text: data as JSON
  }
  @Secured(['ROLE_ADMIN'])
  def update()
  {
    def data = jobService.update( params )
    render contentType: 'application/json', text: data?:[:] as JSON
  }

  @Secured(['ROLE_ADMIN'])
  def remove()
  {
    def data = jobService.remove( params )
    render contentType: 'application/json', text: data?:[:] as JSON
  }

  def download()
  {
    jobService.download(params, response)

  }

  @Secured(['IS_AUTHENTICATED_ANONYMOUSLY'])
  def ids(){
//    println "*"*30
//    println params
    def result = jobService.getByAllJobIds(params?.jobIds)

    render contentType: 'application/json', text: (result as JSON).toString()
  }
  @Secured(['IS_AUTHENTICATED_ANONYMOUSLY'])
  def get() {
    def result = jobService.getByJobId(params?.jobId)

    render contentType: 'application/json', text: (result?.toMap() as JSON).toString()
  }
}