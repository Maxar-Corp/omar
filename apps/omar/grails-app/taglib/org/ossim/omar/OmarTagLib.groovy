package org.ossim.omar

import de.andreasschmitt.richui.taglib.renderer.Renderer
import de.andreasschmitt.richui.taglib.renderer.RenderException

import java.text.SimpleDateFormat


class OmarTagLib
{
  static namespace = "omar"

  def securityClassificationBanner = {attrs ->
    def level = grailsApplication.config.security.level
    def levelMap = grailsApplication.config.security."${level}"
//    def fontSize    = attrs.fontSize?:"1.5em"
    def fontSize = attrs.fontSize ?: "20px"
    def fontWeight = attrs.fontWeight ?: "bold"
    def id = attrs.id
    def style = attrs.style
    if ( !style ) style = "background: ${levelMap.color}; color: black; font-size:${fontSize}; font-weight:${fontWeight}"
    if ( id ) id = "id=${id}"
    out << """<div ${id} align="center" style="${style}">
       ${levelMap.description}
     </div>"""
  }

  Renderer dateChooserRenderer

  static acceptedFormats = ["dd.MM.yyyy", "dd-MM-yyyy", "dd/MM/yyyy", "MM/dd/yyyy", "yyyy-MM-dd"]

  def dateChooser = {attrs ->

    dateChooserRenderer = new org.ossim.omar.DateChooserRenderer()

    if ( attrs.name )
    {

      if ( !attrs?.format || !attrs.format in acceptedFormats )
      {
        attrs.format = "dd.MM.yyyy"
      }
      try
      {
        String date = ""
        if ( attrs?.value )
        {
          date = new SimpleDateFormat(attrs.format).format(attrs.value)
        }
      }
      catch (Exception e)
      {
        log.error("Error parsing date", e)
        attrs.remove("value")
        attrs.value = new SimpleDateFormat(attrs.format).format(new Date())
      }

      if ( !attrs?.locale )
      {
        attrs.locale = request?.locale?.language
      }

      //Render output
      try
      {
        out << dateChooserRenderer.renderTag(attrs)
      }
      catch (RenderException e)
      {
        log.error(e)
      }
    }
  }
}
