package org.ossim.omar
class HomeController
{
  def afterInterceptor = { model, modelAndView ->
    if ( request['isMobile'] )
    {
      if(modelAndView.viewName == "/home/index")
      {
        modelAndView.viewName = modelAndView.viewName + "_mobile"
      }
    }
  }
  
  def grailsApplication
  def index = {
    def editableControllers = []


    ((grailsApplication.domainClasses)*.fullName - ["org.ossim.omar.AuthUser", "org.ossim.omar.Role", "org.ossim.omar.Requestmap"]).sort().each {

      def editableController = grailsApplication.getArtefact("Controller", it + "Controller")

      if ( editableController )
      {
        editableControllers << [
            name: editableController.name,
            path: editableController.logicalPropertyName
        ]
      }
    }

    [editableControllers: editableControllers]
  }
}
