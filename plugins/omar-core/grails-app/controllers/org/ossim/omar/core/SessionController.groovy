package org.ossim.omar.core

class SessionController
{

  def index( )
  {
  }

  def updateSession( )
  {
    params.remove( "action" )
    params.remove( "controller" )
    params.each {k, v ->
      session[k] = v
//      println "session[${k}] = ${session[k]}"
    }
    response.status = 202
  }
}
