package org.ossim.omar
class HomeController
{
/*
def authenticateService
*/
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
  def index = {
/*
    def user = AuthUser.findByUsername(authenticateService.userDomain().username)
*/

    def user = springSecurityService.currentUser


    def editableControllers = []


    ((grailsApplication.domainClasses)*.fullName - ["org.ossim.omar.AuthUser", "org.ossim.omar.Role", "org.ossim.omar.Requestmap", "org.ossim.omar.SecUser", "org.ossim.omar.SecRole"]).sort().each {

      def editableController = grailsApplication.getArtefact("Controller", it + "Controller")

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
