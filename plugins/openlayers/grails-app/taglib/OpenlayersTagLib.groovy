class OpenlayersTagLib
{
  static namespace = 'openlayers'
  def grailsApplication

  def loadJavascript = {attrs ->
    def path = resource(plugin: "openlayers", dir: "js", file: "OpenLayers.js")
    def output = "<script type='text/javascript' src='${path}'></script>"

    out << output
  }

  def loadTheme = {attrs ->
    def theme = attrs['theme'] ?: 'default'
    def path = resource(plugin: "openlayers", dir: "js/theme/${theme}", file: "style.css")
    def output = "<link rel='stylesheet' href='${path}' type='text/css'/>"

    out << output
  }

  def loadMapToolBar = {attrs ->
    /*
    if ( grailsApplication.config.wms.supportIE6 )
    {

      def theme = attrs['theme'] ?: 'default'
      def path = resource(plugin: "openlayers", dir: "css", file: "mapwidget3.css")
      def output = "<link rel='stylesheet' href='${path}' type='text/css'/>"
      out << output
    }
    else
    {
    */
      def theme = attrs['theme'] ?: 'default'
      def path = resource(plugin: "openlayers", dir: "css", file: "mapwidget2.css")
      def output = "<link rel='stylesheet' href='${path}' type='text/css'/>"
      out << output
    /*
    }
    */
  }

}
