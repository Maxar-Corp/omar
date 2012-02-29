package org.ossim.omar

import groovy.xml.StreamingMarkupBuilder

class KmlSiteService{

    static transactional = true

    def myCreateImagesKml(List<org.ossim.omar.raster.RasterEntry> rasterEntries,
                                                Map wmsParams,
                                                Map params)
    {
      def kmlbuilder = new StreamingMarkupBuilder()

      kmlbuilder.encoding = "UTF-8"
      def kmlnode = {
        mkp.xmlDeclaration()
        kml("xmlns": "http://earth.google.com/kml/2.1") {
          Document() {
              name("Omar WMS")
          }
        }
      }
      def kmlwriter = new StringWriter()

      kmlwriter << kmlbuilder.bind(kmlnode)

      kmlwriter.buffer

    }
}
