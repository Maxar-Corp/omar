package org.ossim.omar
class OmarTagLib
{
  static namespace = "omar"

  def securityClassificationBanner = {attrs->
    def level = grailsApplication.config.security.level
    def levelMap = grailsApplication.config.security."${level}"
    def fontSize    = attrs.fontSize?:"1.5em"
    def fontWeight  = attrs.fontWeight?:"bold"
    def id          = attrs.id
    def style       = attrs.style
    if(!style) style =  "background: ${levelMap.color}; color: black; font-size:${fontSize}; font-weight:${fontWeight}"
    if(id) id = "id=${id}"
    out << """<div ${id} align="center" style="${style}">
       ${levelMap.description}
     </div>"""
   }
}
