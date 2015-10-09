package org.ossim.omar.rss

import com.vividsolutions.jts.geom.Envelope
import com.vividsolutions.jts.geom.Geometry
import com.vividsolutions.jts.geom.Polygon
import groovy.xml.StreamingMarkupBuilder

class RssFeedController
{


  def coordinateConversionService
  def grailsApplication
  def grailsLinkGenerator


  def georss( )
  {
    def enablePolygon = params.boolean( "enablePolygon" ) ?: false
    def cc = params.cc
    def be = params.be

    def rssLayerName = grailsApplication.config.rss.keySet().toArray()[0]

    def domainClass = grailsApplication.getArtefactByLogicalPropertyName( "Domain", rssLayerName ).newInstance()
    def entries = domainClass.withCriteria {
      isNotNull( "acquisitionDate" )
      order( "acquisitionDate", "desc" )
      maxResults( 10 )
      if ( be )
      {
        eq( "beNumber", be )
      }
      if ( cc )
      {
        eq( "countryCode", cc )
      }
    }

    def rssBuilder = new StreamingMarkupBuilder()

    rssBuilder.encoding = "UTF-8"

    def rssNode = {
      mkp.xmlDeclaration()
      mkp.declareNamespace( content: "http://purl.org/rss/modules/content/" )
      mkp.declareNamespace( georss: "http://www.georss.org/georss" )

      rss( version: "2.0" ) {
        channel {
          title( "OMAR GeoRSS Feed" )
          link( grailsLinkGenerator.link( plugin: 'omar-rss', controller: 'rssFeed', action: 'georss', absolute: true ) )
          description( "Track the newest images added to OMAR" )
          for ( entry in entries )
          {
            Geometry geom = entry.groundGeom

            item() {
              title( "${entry.acquisitionDate} ${entry.countryCode} ${entry.targetId?:""} ${entry.imageId?:""}" )
              link( grailsLinkGenerator.link(  controller: "mapView", params: [layers: entry.indexId], absolute: true ) )
              // Using point because polygon is not supported by ESRI ArcGIS Explorer or OpenLayers
              // The code below will support polygons when everyone else does
              // The polygons array is for adding Multi-polygons in the future.
              if ( !enablePolygon )
              {
                def centroid = geom.centroid //entry.groundGeom.centroid

                'georss:point'( "${centroid.y} ${centroid.x}" )
              }
              else
              {
                def pts = geom.coordinates.collect { "${it.y} ${it.x}" }.join( ' ' )

                'georss:polygon'( pts )
              }

              Envelope b = entry.groundGeom.envelopeInternal

              def latFormat = "dd@mm'ss.sss\"C"
              def lonFormat = "ddd@mm'ss.sss\"C"
              def minLonDMS = coordinateConversionService.convertToDms( b.minX, lonFormat, false )
              def maxLonDMS = coordinateConversionService.convertToDms( b.maxX, lonFormat, false )
              def minLaxDMS = coordinateConversionService.convertToDms( b.minY, latFormat, true )
              def maxLaxDMS = coordinateConversionService.convertToDms( b.maxY, latFormat, true )

              def content = g.render( plugin: 'omar-rss', template: 'rss', model: [
                      entry: entry,
                      properties: grailsApplication.config.rss."${rssLayerName}".properties,
                      minLonDMS: minLonDMS,
                      maxLonDMS: maxLonDMS,
                      minLaxDMS: minLaxDMS,
                      maxLaxDMS: maxLaxDMS
              ] )
              'content:encoded' { mkp.yieldUnescaped( "<![CDATA[${content}]]>" ) }
            } // item
          } // image
        } // channel
      } // rss
    } // rssNode

    def rssWriter = new StringWriter()
    rssWriter << rssBuilder.bind( rssNode )

    render contentType: "application/rss+xml", text: rssBuilder.bind(rssNode).toString()
  }
}
