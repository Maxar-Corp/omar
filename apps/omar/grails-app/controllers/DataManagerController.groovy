class DataManagerController
{
  def dataManagerService

  def index = {
  }

  def addRaster = {
    def method = request.method.toUpperCase()
    def filename = params.filename

    switch ( method )
    {
      case "GET":
        break
      case "POST":

        def testFile = new File(filename)
        if(!testFile.exists())
        {
          response.status = HttpStatus.NOT_FOUND
          flash.message = "Not Found: ${filename}"
          render(flash.message)
        }
        else if(!testFile.canRead())
        {
          response.status = HttpStatus.FORBIDDEN
          flash.message = "Not Readable ${filename}"
          render(flash.message)
        }
        else
        {
          def status = dataManagerService.addRaster(filename)

          if ( status )
          {
            response.status = HttpStatus.OK
            flash.message = "Added ${filename}"
            render(flash.message)
//          redirect(action: "index")
          }
          else
          {
            response.status = HttpStatus.UNSUPPORTED_MEDIA_TYPE
            flash.message = "Unable to add Raster with filename ${filename}"
//          redirect(action: "index")
            render(flash.message)
          }
        }

        break
      default:
       response.status = HttpStatus.METHOD_NOT_ALLOWED
       flash.message = "Unsupported method ${method} for action addRaster with filename ${filename}"
//       redirect(action: "index")
       render(flash.message)
      break;
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
        def testFile = new File(filename)
        if(!testFile.exists())
        {
           response.status = HttpStatus.NOT_FOUND
           flash.message = "Not found: ${filename} "
           render(flash.message)
        }
        else if(!testFile.canRead())
        {
          response.status = HttpStatus.FORBIDDEN
          flash.message = "Not Readable: ${filename}"
          render(flash.message)
        }
        else
        {
          def status = dataManagerService.addVideo(filename)

          if ( status )
          {
            response.status = HttpStatus.OK
            flash.message = "Added ${filename}"
            render(flash.message)
           // redirect(action: "index")
          }
          else
          {
            response.status = HttpStatus.UNSUPPORTED_MEDIA_TYPE
            flash.message = "Unable to add Video with filename ${filename}"
            render(flash.message)
//            redirect(action: "index")
          }
        }

        break
      default:
        response.status = HttpStatus.METHOD_NOT_ALLOWED
        flash.message = "Unsupported method ${method} for action addVideo with filename ${filename}"
        render(flash.message)
        //redirect(action: "index")
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
          response.status = HttpStatus.OK
          flash.message = "Deleted ${filename}"
          render(flash.message)
//          redirect(action: "index")
        }
        else
        {
          response.status = HttpStatus.NOT_FOUND
          flash.message = "Unable to delete Raster with filename ${filename}"
          render(flash.message)
//          redirect(action: "index")
        }

        break
      default:
        response.status = HttpStatus.METHOD_NOT_ALLOWED
        flash.message = "Unsupported method ${method} for action removeRaster with filename ${filename}"
        render(flash.message)
//        redirect(action: "index")
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
          response.status = HttpStatus.OK
          flash.message = "Deleted ${filename} "
          render(flash.message)
//          redirect(action: "index")
        }
        else
        {
          response.status = HttpStatus.NOT_FOUND
          flash.message = "Unable to delete Video with filename ${filename}"
//          redirect(action: "index")
          render(flash.message)
        }
        break
      default:
        response.status = HttpStatus.METHOD_NOT_ALLOWED
        flash.message = "Unsupported method ${method} for action removeVideo with filename ${filename}"
        render(flash.message)
//        redirect(action: "index")
    }
  }

}
