package org.ossim.omar

class ReportController {
  def scaffold = Report

  def springSecurityService
  def index = {
    redirect(action: "create", params: params)
  }
    def create = {

        def userDetails = springSecurityService.principal
        def person = SecUser.get(userDetails.id)
        if(person)
        {
            render( view:"create", model:[userInfo:person] )
        }

    }
}
