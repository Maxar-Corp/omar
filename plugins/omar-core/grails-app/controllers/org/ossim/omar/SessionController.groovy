package org.ossim.omar

class SessionController {

  def index = {
  }
  def updateSession = {
    params.remove("action")
    params.remove("controller")
    params.each{k,v->
      session[k] = v
    }
    response.status = 202
  }
}
