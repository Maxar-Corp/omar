class HomeController
{

  def index = {
    def editableControllers = []


    ((grailsApplication.domainClasses )*.name - ["AuthUser", "Role", "Requestmap"]).sort().each {

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
