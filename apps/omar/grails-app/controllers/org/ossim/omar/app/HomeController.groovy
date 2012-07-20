package org.ossim.omar.app

class HomeController
{
  def springSecurityService

  def afterInterceptor = { model, modelAndView ->
    if ( request['isMobile'] )
    {
      if ( modelAndView.viewName == "/home/index" )
      {
        modelAndView.viewName = modelAndView.viewName + "_mobile"
      }
    }
  }

  def grailsApplication

  def index( )
  {
/*
    def user = AuthUser.findByUsername(authenticateService.userDomain().username)
*/

    def user = springSecurityService.currentUser


    def editableControllers = []


    ( ( grailsApplication.domainClasses )*.fullName - ["org.ossim.omar.AuthUser", "org.ossim.omar.Role", "org.ossim.omar.security.Requestmap", "org.ossim.omar.security.SecUser", "org.ossim.omar.security.SecRole"] ).sort().each {

      def editableController = grailsApplication.getArtefact( "Controller", it + "Controller" )

      if ( editableController )
      {
        editableControllers << [
                name: editableController.name,
                path: editableController.logicalPropertyName
        ]
      }
    }

    [editableControllers: editableControllers, user: user]
  }
}
