class OpenlayersTagLib
{
  static namespace = 'openlayers'

  def loadJavascript = { attrs ->
    def path = createLinkTo( dir:"${pluginContextPath}/js", file:"OpenLayers.js" )
    def output = "<script type='text/javascript' src='${path}'></script>"

    out << output
  }

  def loadTheme = { attrs ->
    def theme = attrs['theme'] ?: 'default'
    def path = createLinkTo( dir:"${pluginContextPath}/js/theme/${theme}", file:"style.css" )
    def output = "<link rel='stylesheet' href='${path}' type='text/css'/>"
                 
    out << output
  }

  def loadMapToolBar = { attrs ->
    def theme = attrs['theme'] ?: 'default'
    def path = createLinkTo( dir:"${pluginContextPath}/css", file:"mapwidget2.css" )
    def output = "<link rel='stylesheet' href='${path}' type='text/css'/>"

    out << output
  }

}
