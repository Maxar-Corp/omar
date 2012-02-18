package org.ossim.omar.core
public class HttpStatusMessage
{
  // General error message
  String message    = "";
  // Error code as defined by the http response specification
  int    status = HttpStatus.OK;
  // Some error codes expect header information to be defined
  def    header =[:]
  
  public HttpStatusMessage()
  {
    
  }
  public HttpStatusMessage(String m, int stat)
  {
    message    = m
    status = stat
  }
  public void initializeResponse(def response)
  {
    response.status = status
    header.each{ k, v ->
      response.setHeader(k, v)
    }
  }
}