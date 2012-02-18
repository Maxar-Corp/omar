package org.ossim.omar.stager

import org.ossim.omar.core.HttpStatusMessage
import org.ossim.omar.core.HttpStatus

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
class DataManagerController
{
  def dataManagerService

  def index = {
  }

  def action = {
    def method = request.method.toUpperCase()
    def httpStatusMessage = new HttpStatusMessage()

    httpStatusMessage.status = HttpStatus.OK
    httpStatusMessage.message = ""

    switch ( method )
    {
    case "GET":
      def pattern = /(add|update|delete)(Raster|Video)/
      def matcher = params.opType =~ pattern
      def op = null
      def type = null

      if ( matcher )
      {
        op = matcher[0][1]
        type = matcher[0][2]
      }

      return [opType: params.opType, op: op, type: type]
      break
    case "POST":
    case "DELETE":
      def status = dataManagerService."${params.opType}"(httpStatusMessage, params)

      response.status = httpStatusMessage.status
      render(httpStatusMessage.message)
      break
    default:
      httpStatusMessage.status = HttpStatus.METHOD_NOT_ALLOWED
      httpStatusMessage.message = "Unsupported method ${method} for action ${params.opType} with filename ${filename}"
      httpStatusMessage.header.Allow = "GET, POST, DELETE"
      httpStatusMessage.initializeResponse(response)
      render(httpStatusMessage.message)
    }
  }
}
