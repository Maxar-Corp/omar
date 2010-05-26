package org.ossim.omar

/**
 * DataManagerController supplies a simple API to add and remove raster
 * and video datasets to the OMAR tables.  Currently, only a filenam argument is passed
 *
 * All controller methods will use the internal params variable to parse out
 * the required arguments.  All http parameters are stored as a Java HashMap type
 * in a variable called params.
 *
 * @see DataManagerService
 */
class DataManagerController
{
  def dataManagerService

  def index = {
  }

  /**
   * renders a response back to the caller to notify if the raster was added successfully
   * or not.  The response header is populated with the proper response code.
   * for adding a raster you must use the http method POST or PUT.  If you are going through the
   * browser then a GET method will bring up a form to type in a file to add to the table.<br>
   * Example use case:
   * <br>
   * <code>
   *   http://&lt;server&gt;/omar/dataManager/addRaster?filename="&lt;myFileToAdd&gt;"
   * </code>
   *
   * <br>
   * @param params.filename This is a http parameter of the form filename="&lt;raster to add&gt;"
   */
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
  def updateRaster={
    def method = request.method.toUpperCase()
    def httpStatusMessage = new HttpStatusMessage()
    httpStatusMessage.status = HttpStatus.OK
    def rasterName = null
    if(params.id)
    {
      rasterName = params.id;
    }
    else if(params.filename)
    {
      rasterName = params.filename
    }
    httpStatusMessage.message = "Update raster ${rasterName}"
    switch ( method )
    {
      case "GET":
      case "PUT":
      case "POST":
        dataManagerService.updateRaster(httpStatusMessage, params)
        response.status = httpStatusMessage.status
        render (httpStatusMessage.message)
        break
      default:
       httpStatusMessage.status = HttpStatus.METHOD_NOT_ALLOWED
       httpStatusMessage.message = "Unsupported method ${method} for action addRaster with value ${rasterName}"
       httpStatusMessage.header.Allow = "GET,PUT,POST"
       httpStatusMessage.initializeResponse(response)
       render(httpStatusMessage.message)
      break;
    }
    
  }
  /**
    * renders a response back to the caller to notify if the video was added successfully
    * or not.  For adding a video you must use the http method POST or PUT. If you are going through the
   *  browser then a GET method will bring up a form to type in a file to add to the table.<br>
    * Example use case:
    * <br>
    * <code>
    *   http://&lt;server&gt;/omar/dataManager/addVideo?filename="&lt;myFileToAdd&gt;"
    * </code>
    *
    * <br>
    * @param params.filename This is a http parameter of the form filename="&lt;video to add&gt;"
    * @return Proper http response code is rendered back to the caller 
    */
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

  /**
    * renders a response back to the caller to notify if the raster was removed successfully
    * or not.  For removing a raster you must use the http method POST or DELETE.  If you are going through the
    * browser then a GET method will bring up a form to type in a file to delete from the table.<br>
    * Example use case:
    * <br>
    * <code>
    *   http://&lt;server&gt;/omar/dataManager/removeRaster?filename="&lt;myFileToRemove&gt;"
    * </code>
    *
    * <br>
    * @param params.filename This is a http parameter of the form filename="&lt;raster to add&gt;"
    * @return Proper http response code is rendered back to the caller
    */
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

  /**
    * renders a response back to the caller to notify if the video was removed successfully
    * or not.  For removing a video you must use the http method POST or DELETE.  If you are going through the
    * browser then a GET method will bring up a form to type in a file to delete from the table.<br>
    * Example use case:
    * <br>
    * <code>
    *   http://&lt;server&gt;/omar/dataManager/removeVideo?filename="&lt;myFileToRemove&gt;"
    * </code>
    *
    * <br>
    * @param params.filename This is a http parameter of the form filename="&lt;video to remove&gt;"
    * @return Proper http response code is rendered back to the caller
    */
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
