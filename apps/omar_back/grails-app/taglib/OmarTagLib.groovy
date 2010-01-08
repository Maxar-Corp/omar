class OmarTagLib
{
  static namespace = "omar"

  def securityClassificationBanner = {
    def color
    def description



    switch ( grailsApplication.config.security.level )
    {
      case "UNCLASS":
        color = "green"
        description = "Unclassified"
        break
      case "SECRET":
        color = "red"
        description = "Secret // NOFORN"

        break
      case "TOPSECRET":
        color = "yellow"
        description = "Top Secret"
        break
    }

    if ( color && description )
    {
      out << """<div align="center" style="background: ${color}; color: black; font-size:1.5em; font-weight:bold">
        ${description}
      </div>"""
    }
  }

}
