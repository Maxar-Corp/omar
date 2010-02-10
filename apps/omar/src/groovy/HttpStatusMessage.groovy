public class HttpStatusMessage
{
  String message    = "";
  int    status = HttpStatus.OK;

  public HttpStatusMessage()
  {
    
  }
  public HttpStatusMessage(String m, int stat)
  {
    message    = m
    status = stat
  }

}