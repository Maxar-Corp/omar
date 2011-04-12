package org.ossim.omar
import org.hibernate.criterion.*

class ReportController {
  def scaffold = Report

  def springSecurityService
  def index = {
    redirect(action: "create", params: params)
  }
    def create = {
        if(springSecurityService.loggedIn)
        {
            def userDetails = springSecurityService.principal
            def person = SecUser.get(userDetails.id)
            if(person)
            {
                render( view:"create", model:[userInfo:person] )
            }
        }
        else
        {
            flash.message = "You must be logged in to produce a Report"
            redirect(controller:"login", action: "auth")
        }

    }
    def delete = {
        if(springSecurityService.loggedIn)
        {
            if(params.id)
            {
                def userDetails = springSecurityService.principal
                def person = SecUser.get(userDetails.id)
                def report = Report.get(params.id);
                if(report)
                {
                    if(person)
                    {
                        def roles = person.getAuthoritiesAsStringList()
                        if(("ROLE_ADMIN" in roles))
                        {
                            report.delete()
                            flash.message = "Report with id ${params.id} deleted"
                            redirect(action:"create", params:[:])
                        }
                        else
                        {
                            render("${person.username} does not have authority to delete report id ${param.id}")
                        }
                    }
                    else
                    {
                        render("Unable to get user details to delete the report ${report}")
                    }
                }
                else
                {
                    render("Report with id = ${params.id} not found in the database and will not be deleted")
                }
            }
        }
        else
        {
            flash.message = "You must be logged in to delete a Report"
            redirect(controller:"login", action: "auth")
        }
    }
    def edit = {
        if(springSecurityService.loggedIn)
        {
            if(params.id)
            {
                def userDetails = springSecurityService.principal
                def person = SecUser.get(userDetails.id)
                def report = Report.get(params.id);
                if(report)
                {
                    if(person)
                    {
                        def roles = person.getAuthoritiesAsStringList()
                        if((report.name == person.username)||
                           ("ROLE_ADMIN" in roles))
                        {
                            render( view:"edit", model:[userInfo:person,reportInstance:report] )
                        }
                        else
                        {
                            flash.message = "You have no authority to edit this report"
                            redirect(controller:"report", action: "list")
                        }
                    }
                }
            }
        }
        else
        {
            flash.message = "You must be logged in to produce a Report"
            redirect(controller:"login", action: "auth")
        }
    }
    def list = {
         if(springSecurityService.loggedIn)
        {
            def userDetails = springSecurityService.principal
            def person = SecUser.get(userDetails.id)
            if(person)
            {
                def roles = person.getAuthoritiesAsStringList()
                def x =
                {
                    if(!("ROLE_ADMIN" in roles))
                    {
                        eq("name",person.username)
                    }
                  projections { rowCount()}
                }
                def criteriaBuilder = Report.createCriteria()
                def criteria = criteriaBuilder.buildCriteria(x)

                def totalCount = criteria.list().get(0) as int
                 def order = params?.order?:"desc";
                 def  reportInstanceList = Report.createCriteria().list{
                     if(!("ROLE_ADMIN" in roles))
                     {
                         eq("name",person.username)
                     }
                     if ( params?.offset )
                     {
                       setFirstResult(params.offset as Integer)
                     }
                     if(params?.max)
                     {
                         setMaxResults(params.max as Integer)
                     }
                     if(params?.sort)
                     {
                         def ordering = (order == "asc") ? Order.asc(params.sort) : Order.desc(params.sort)
                         addOrder(ordering)
                     }

                 }
                render( view:"list", model:[userInfo:person,
                                            reportInstanceList:reportInstanceList,
                                            reportInstanceTotal:totalCount] )
            }
        }
        else
        {
            flash.message = "You must be logged in to produce a Report"
            redirect(controller:"login", action: "auth")
        }

    }
    def show = {
        if(springSecurityService.loggedIn)
        {
            if(params.id)
            {
                def userDetails = springSecurityService.principal
                def person = SecUser.get(userDetails.id)
                def report = Report.get(params.id);
                if(report)
                {
                    if(person)
                    {
                        def roles = person.getAuthoritiesAsStringList()
                        if((report.name == person.username)||
                           ("ROLE_ADMIN" in roles))
                        {
                            render( view:"show", model:[userInfo:person,reportInstance:report] )
                        }
                        else
                        {
                            flash.message = "You have no authority to show the details of this report"
                            redirect(controller:"report", action: "list")
                        }
                    }
                }
            }
        }
        else
        {
            flash.message = "You must be logged in to show a Report"
            redirect(controller:"login", action: "auth")
        }

    }
}
