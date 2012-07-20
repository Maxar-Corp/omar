package org.ossim.omar.rss

import groovy.xml.StreamingMarkupBuilder

class RssFeedController
{


  def coordinateConversionService
  def grailsApplication



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
          link( createLink( plugin: 'omar-rss', controller: 'rssFeed', action: 'georss', base: "${grailsApplication.config.omar.serverURL}", absolute: true ) )
          description( "Track the newest images added to OMAR" )
          for ( entry in entries )
          {
            item() {
              title( "${entry.acquisitionDate} ${entry.countryCode} ${entry.targetId} ${entry.imageId}" )
              link( createLink( controller: "mapView", base: "${grailsApplication.config.omar.serverURL}", params: [layers: entry.indexId], absolute: true ) )
              // Using point because polygon is not supported by ESRI ArcGIS Explorer or OpenLayers
              // The code below will support polygons when everyone else does
              // The polygons array is for adding Multi-polygons in the future.
              if ( !enablePolygon )
              {
                def centroid = entry.groundGeom.centroid

                'georss:point'( "${centroid.y} ${centroid.x}" )
              }
              else
              {
                def pts = entry.groundGeom.coordinates.collect { "${it.y} ${it.x}" }.join( ' ' )

                'georss:polygon'( pts )
              }

              def bounds = entry.groundGeom.bounds
              def latFormat = "dd@mm'ss.sss\"C"
              def lonFormat = "ddd@mm'ss.sss\"C"
              def minLonDMS = coordinateConversionService.convertToDms( bounds.minLon, lonFormat, false )
              def maxLonDMS = coordinateConversionService.convertToDms( bounds.maxLon, lonFormat, false )
              def minLaxDMS = coordinateConversionService.convertToDms( bounds.minLat, latFormat, true )
              def maxLaxDMS = coordinateConversionService.convertToDms( bounds.maxLat, latFormat, true )

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
    render contentType: "application/rss+xml", text: rssWriter.toString()
  }
}
