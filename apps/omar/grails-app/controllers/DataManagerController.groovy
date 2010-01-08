class DataManagerController
{
  def dataManagerService

  def index = { }

  def addRaster = {
    def method = request.method.toUpperCase()
    def filename = params.filename

    switch ( method )
    {
      case "GET":
        break
      case "POST":
        def status = dataManagerService.addRaster(filename)

        if ( status )
        {
          flash.message = "Raster with filename ${filename} added"
          redirect(action: "index")
        }
        else
        {
          flash.message = "Unable to add Raster with filename ${filename}"
          redirect(action: "index")
        }

        break
      default:
        flash.message = "Unsupported method ${method} for action addRaster with filename ${filename}"
        redirect(action: "index")
    }

  }

  def addVideo = {
    def method = request.method.toUpperCase()
    def filename = params.filename

    switch ( method )
    {
      case "GET":
        break
      case "POST":
        def status = dataManagerService.addVideo(filename)

        if ( status )
        {
          flash.message = "Video with filename ${filename} added"
          redirect(action: "index")
        }
        else
        {
          flash.message = "Unable to add Video with filename ${filename}"
          redirect(action: "index")
        }

        break
      default:
        flash.message = "Unsupported method ${method} for action addVideo with filename ${filename}"
        redirect(action: "index")
    }

  }

  def removeRaster = {
    def method = request.method.toUpperCase()
    def filename = params.filename
    
    switch ( method )
    {
      case "GET":
        break
      case "POST":
      case "DELETE":
        def status = dataManagerService.removeRaster(filename)

        if ( status )
        {
          flash.message = "Raster with filename ${filename} deleted"
          redirect(action: "index")
        }
        else
        {
          flash.message = "Unable to delete Raster with filename ${filename}"
          redirect(action: "index")
        }

        break
      default:
        flash.message = "Unsupported method ${method} for action removeRaster with filename ${filename}"
        redirect(action: "index")
    }
  }

  def removeVideo = {
    def method = request.method.toUpperCase()
    def filename = params.filename

    switch ( method )
    {
      case "GET":
        break
      case "POST":
      case "DELETE":
        def status = dataManagerService.removeVideo(filename)

        if ( status )
        {
          flash.message = "Video with filename ${filename} deleted"
          redirect(action: "index")
        }
        else
        {
          flash.message = "Unable to delete Video with filename ${filename}"
          redirect(action: "index")
        }

        break
      default:
        flash.message = "Unsupported method ${method} for action removeVideo with filename ${filename}"
        redirect(action: "index")
    }
  }

}
