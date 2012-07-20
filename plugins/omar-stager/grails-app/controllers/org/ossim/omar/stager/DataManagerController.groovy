package org.ossim.omar.stager

import org.ossim.omar.core.HttpStatusMessage
import org.ossim.omar.core.HttpStatus
import org.springframework.context.ApplicationContextAware
import org.springframework.context.ApplicationContext

/**
 * DataManagerController supplies a simple API to add and remove raster
 * and video datasets to the OMAR tables.  Currently, only a filename argument is passed
 *
 * All controller methods will use the internal params variable to parse out
 * the required arguments.  All http parameters are stored as a Java HashMap type
 * in a variable called params.
 *
 * @see DataManagerService
 */
class DataManagerController implements ApplicationContextAware
{
  ApplicationContext applicationContext

  def index( )
  {
  }

  def action( )
  {
    def method = request.method.toUpperCase()
    def httpStatusMessage = new HttpStatusMessage()

    def pattern = /(add|update|remove|delete)(.*)/
    def matcher = params.opType =~ pattern
    def op = null
    def type = null

    if ( matcher )
    {
      op = matcher[0][1]
      type = matcher[0][2]
    }

    httpStatusMessage.status = HttpStatus.OK
    httpStatusMessage.message = ""

    switch ( method )
    {
    case "GET":

      return [opType: params.opType, op: op, type: type]
      break
    case "POST":
    case "DELETE":
      def serviceName = "${type[0].toLowerCase() + type.substring( 1 ) }DataSetService"
      def service = applicationContext.getBean( serviceName )
      def status = service."${params.opType}"( httpStatusMessage, params )

      response.status = httpStatusMessage.status
      render( httpStatusMessage.message )
      break
    default:
      httpStatusMessage.status = HttpStatus.METHOD_NOT_ALLOWED
      httpStatusMessage.message = "Unsupported method ${method} for action ${params.opType} with filename ${filename}"
      httpStatusMessage.header.Allow = "GET, POST, DELETE"
      httpStatusMessage.initializeResponse( response )
      render( httpStatusMessage.message )
    }
  }

  void setApplicationContext( org.springframework.context.ApplicationContext applicationContext )
  {
    this.applicationContext = applicationContext
  }
}
