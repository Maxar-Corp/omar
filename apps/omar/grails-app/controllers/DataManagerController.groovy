class DataManagerController
{
  def dataManagerService

  def index = {
  }

  def addRaster = {
    def method = request.method.toUpperCase()
    def filename = params.filename
    def httpStatusMessage = new HttpStatusMessage()
    httpStatusMessage.status = HttpStatus.OK
    httpStatusMessage.message = "Added raster ${filename}"

    switch ( method )
    {
      case "GET":
        break
      case "PUT":
      case "POST":
        dataManagerService.addRaster(httpStatusMessage, filename)
        response.status = httpStatusMessage.status
        render (httpStatusMessage.message)
        break
      default:
       httpStatusMessage.status = HttpStatus.METHOD_NOT_ALLOWED
       httpStatusMessage.message = "Unsupported method ${method} for action addRaster with filename ${filename}"
       httpStatusMessage.header.Allow = "GET, PUT, POST"
       httpStatusMessage.initializeResponse(response)
       render(httpStatusMessage.message)
      break;
    }
  }

  def addVideo = {
    def method = request.method.toUpperCase()
    def filename = params.filename
    def httpStatusMessage = new HttpStatusMessage()
    httpStatusMessage.status = HttpStatus.OK
    httpStatusMessage.message = "Added video ${filename}"

    switch ( method )
    {
      case "GET":
        break
      case "POST":
      case "PUT":
         dataManagerService.addVideo(httpStatusMessage, filename)

        response.status = httpStatusMessage.status
        render(httpStatusMessage.message)

        break
      default:
        httpStatusMessage.status = HttpStatus.METHOD_NOT_ALLOWED
        httpStatusMessage.message = "Unsupported method ${method} for action addVideo with filename ${filename}"
        httpStatusMessage.header.Allow = "GET, POST, PUT"
        httpStatusMessage.initializeResponse(response)
        render(httpStatusMessage.message)
        break;
    }

  }

  def removeRaster = {
    def method = request.method.toUpperCase()
    def filename = params.filename
    def httpStatusMessage = new HttpStatusMessage()
    httpStatusMessage.status = HttpStatus.OK
    httpStatusMessage.message = "Removed raster ${filename}"
    
    switch ( method )
    {
      case "GET":
        break
      case "POST":
      case "DELETE":
        def status = dataManagerService.removeRaster(httpStatusMessage, filename)

        response.status = httpStatusMessage.status
        render(httpStatusMessage.message)

        break
      default:
        httpStatusMessage.status = HttpStatus.METHOD_NOT_ALLOWED
        httpStatusMessage.message = "Unsupported method ${method} for action removeRaster with filename ${filename}"
        httpStatusMessage.header.Allow = "GET, POST, DELETE"
        httpStatusMessage.initializeResponse(response)
        render(httpStatusMessage.message)

        break
    }
  }

  def removeVideo = {
    def method = request.method.toUpperCase()
    def filename = params.filename
    def httpStatusMessage = new HttpStatusMessage()
    httpStatusMessage.status = HttpStatus.OK
    httpStatusMessage.message = "Removed ${filename}"

    switch ( method )
    {
      case "GET":
        break
      case "POST":
      case "DELETE":
        def status = dataManagerService.removeVideo(httpStatusMessage, filename)

        response.status = httpStatusMessage.status
        render(httpStatusMessage.message)
        break
      default:
        httpStatusMessage.status = HttpStatus.METHOD_NOT_ALLOWED
        httpStatusMessage.message = "Unsupported method ${method} for action removeVideo with filename ${filename}"
        httpStatusMessage.header.Allow = "GET, POST, DELETE"
        httpStatusMessage.initializeResponse(response)
        render(httpStatusMessage.message)
    }
  }
}
