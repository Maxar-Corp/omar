package org.ossim.omar.common.ui

class OmarTagLib
{
  static namespace = "omar"

  def securityClassificationBanner = {attrs ->
    def level = grailsApplication.config.security.level
    def levelMap = grailsApplication.config.security."${level}"
//    def fontSize    = attrs.fontSize?:"1.5em"
    def fontSize = attrs.fontSize ?: "20px"
    def fontWeight = attrs.fontWeight ?: "bold"
    def id = attrs.id ? "id=${attrs.id}" : ''
    def style = attrs.style
    if ( !style ) style = "background: ${levelMap.color}; color: black; font-size:${fontSize}; font-weight:${fontWeight}"
    out << """<div ${id} align="center" style="${style}">
       ${levelMap.description}
     </div>"""
  }
    def logout = {attrs->
        out<< """<div class=\"logout\"><a href="${createLink( controller: 'logout' )}">Logout</a></div>"""
    }
}
