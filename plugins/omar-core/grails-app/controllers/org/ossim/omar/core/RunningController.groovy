package org.ossim.omar.core

class RunningController
{

  def index( )
  {
    render( text: "OMAR_RUNNING", contentType: "text/plain", encoding: "UTF-8" )
  }
}
