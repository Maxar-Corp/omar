package org.ossim.omar.raster

import groovy.xml.StreamingMarkupBuilder
import java.text.SimpleDateFormat
import org.ossim.omar.ogc.KmlService
import org.ossim.omar.raster.RasterEntry

class RasterKmlService extends KmlService
{
  def grailsApplication

  String createName( RasterEntry rasterEntry )
  {
    rasterEntry.title ?: rasterEntry.filename
  }

  String createImageKmlDescription( RasterEntry rasterEntry )
  {
    def description = ""

    def imageUrl = tagLibBean.createLink( absolute: true, base: "${grailsApplication.config.omar.serverURL}",
            controller: "mapView", params: [layers: rasterEntry.indexId] )

    def thumbnailUrl = tagLibBean.createLink( absolute: true, base: "${grailsApplication.config.omar.serverURL}",
            controller: "thumbnail", action: "show", id: rasterEntry.id,
            params: [size: 128, projectionType: 'imagespace'] )

    def logoUrl = "${grailsApplication.config.omar.serverURL}/images/omarLogo.png"

    def mpp = rasterEntry.getMetersPerPixel()
    def fieldMap = [
            Thumbnail: "<img src='${thumbnailUrl}'/>",
            File: "<a href='${imageUrl}'>${( rasterEntry.mainFile.name as File ).name}</a>",
            'Entry Id': rasterEntry.entryId ?: "",
            'Image Id': rasterEntry.imageId ?: "",
            'Title': rasterEntry.title ?: "",
            'NIIRS': rasterEntry.niirs ?: "",
            'Width': rasterEntry.width ?: "",
            'Height': rasterEntry.height ?: "",
            'Bands': rasterEntry.numberOfBands ?: "",
            'Acquistion Date': rasterEntry?.acquisitionDate ?: "",
            'Meters Per Pixel': mpp ?: ""
    ]


    description = "<table border='1'>"
    fieldMap.each {k, v ->
      description += "<tr>"
      description += "<th align='right'>${k}:</th>"
      description += "<td>${v}</td></tr>"
    }
    description += "<tfoot><tr><td colspan='2'><a href='${grailsApplication.config.omar.serverURL}'><img src='${logoUrl}'/></a></td></tr></tfoot>"
    description += "</table>"

    description
  }

  String createImagesKml( List rasterEntries, Map wmsParams, Map params )
  {
    def kmlbuilder = new StreamingMarkupBuilder()

    kmlbuilder.encoding = "UTF-8"

    wmsParams?.request = "GetMap"
    if ( !params?.containsKey( "version" ) )
    {
      wmsParams.version = "1.1.1"
    }
//    if ( !params?.containsKey("width") )
    //    {
    //      wmsParams.width = "1024"
    //    }
    //    if ( !params?.containsKey("height") )
    //    {
    //      wmsParams.height = "512"
    //    }
    if ( !params?.containsKey( "format" ) )
    {
      wmsParams.format = "image/png"
    }
    if ( !params?.containsKey( "transparent" ) )
    {
      wmsParams.transparent = "TRUE"
    }
    wmsParams?.srs = "EPSG:4326"
    def bbox = wmsParams?.bbox?.split( ',' )?.collect { it.toDouble() };

    wmsParams?.remove( "bbox" );
    wmsParams?.remove( "width" );
    wmsParams?.remove( "height" );
    wmsParams.remove( "action" )
    wmsParams.remove( "controller" )
    def rasterIdx = 0
    def descriptionMap = [:]
    rasterEntries?.each {rasterEntry ->
      descriptionMap.put( rasterIdx, createImageKmlDescription( rasterEntry ) )
      rasterIdx++;
    }
    def kmlnode = {
      mkp.xmlDeclaration()
      kml( "xmlns": "http://earth.google.com/kml/2.1" ) {
        Document() {
//          Folder() {
          name( "Omar WMS" )
          rasterIdx = 0
          rasterEntries?.each {rasterEntry ->
            def minLonDMS
            def maxLonDMS
            def minLatDMS
            def maxLatDMS
            def acquisition = ( rasterEntry?.acquisitionDate ) ? sdf.format( rasterEntry?.acquisitionDate ) : null
            def bounds = rasterEntry?.groundGeom?.bounds
            def groundCenterLon = ( bounds?.minLon + bounds?.maxLon ) * 0.5;
            def groundCenterLat = ( bounds?.minLat + bounds?.maxLat ) * 0.5;
            wmsParams?.layers = rasterEntry?.indexId

            def renderedHtml = "${descriptionMap.get( rasterIdx )}"
            rasterIdx++
            def mpp = rasterEntry.getMetersPerPixel()
            // calculate a crude metric for putting an image that almost fits within the google viewport
            //
            def defaultRange = mpp * Math.sqrt( ( rasterEntry.width ** 2 ) + ( rasterEntry.height ** 2 ) );
            if ( defaultRange < 1 )
            {
              defaultRange = 15000
            }
            GroundOverlay() {

              name( createName( rasterEntry ) )
              Snippet()
              description { mkp.yieldUnescaped( "<![CDATA[${renderedHtml}]]>" ) }
              LookAt() {
                longitude( groundCenterLon )
                latitude( groundCenterLat )
                altitude( 0.0 )
                heading( 0.0 )
                tilt( 0.0 )
                range( defaultRange )
                altitudeMode( "clampToGround" )
              }
              open( "1" )
              visibility( "1" )
              Icon() {
                def wmsURL = tagLibBean.createLink(
                        absolute: true, base: "${grailsApplication.config.omar.serverURL}",
                        controller: "ogc", action: "wms", params: wmsParams
                )

                href { mkp.yieldUnescaped( "<![CDATA[${wmsURL}]]>" ) }
                viewRefreshMode( "onStop" )
                viewRefreshTime( "1" )
                viewBoundScale( "0.85" )
                viewFormat {
                  mkp.yieldUnescaped(
                          "<![CDATA[BBOX=[bboxWest],[bboxSouth],[bboxEast],[bboxNorth]&width=[horizPixels]&height=[vertPixels]]]>"
                  )
                }
              }
              LatLonBox() {
                if ( bbox )
                {
                  north( Math.max( bbox[3], bbox[1] ) )
                  south( Math.min( bbox[1], bbox[3] ) )
                  east( Math.max( bbox[2], bbox[0] ) )
                  west( Math.min( bbox[0], bbox[2] ) )
                }
                else
                {
                  north( bounds?.maxLat )
                  south( bounds?.minLat )
                  east( bounds?.maxLon )
                  west( bounds?.minLon )
                }
              }
              if ( acquisition )
              {
                TimeStamp() {
                  when( acquisition )
                }
              }
            }
          }
//          }
        }
      }
    }
    def kmlwriter = new StringWriter()

    kmlwriter << kmlbuilder.bind( kmlnode )

    String kmlText = kmlwriter.buffer
    return kmlText
  }

  String createTopImagesKml( Map params )
  {
    def kmlQueryUrl = tagLibBean.createLink( absolute: true, base: "${grailsApplication.config.omar.serverURL}",
            controller: "rasterKmlQuery", action: "getImagesKml", params: params )

    def kmlbuilder = new StreamingMarkupBuilder()

    kmlbuilder.encoding = "UTF-8"

    def kmlnode = {
      mkp.xmlDeclaration()
      kml( "xmlns", "http://earth.google.com/kml/2.1" ) {
        NetworkLink() {
          name( "OMAR Last ${params.max} Images For View" )
          Link() {
            href {
              mkp.yieldUnescaped( "<![CDATA[${kmlQueryUrl}]]>" )
            }
            httpQuery( "googleClientVersion=[clientVersion];" )
            viewRefreshMode( "onRequest" )
          }
        }
      }
    }

    def kmlwriter = new StringWriter()

    kmlwriter << kmlbuilder.bind( kmlnode )

    String kmlText = kmlwriter.buffer

    return kmlText
  }

  String createImageFootprint( Map params )
  {
    def dateFormat = new SimpleDateFormat( "yyyyMMdd" );
    def date = new Date()
    def url = buildUrl( grailsApplication.config.wms.data.raster.url,
            [VERSION: "1.1.1",
                    REQUEST: "GetMap",
                    LAYERS: "${grailsApplication.config.wms.data.raster.options.footprintLayers}",
                    STYLES: "${grailsApplication.config.wms.data.raster.options.styles}",
                    SRS: "EPSG:4326",
                    WIDTH: "1024",
                    HEIGHT: "512",
                    TRANSPARENT: "TRUE",
                    TIME: "P${params.days}D/${dateFormat.format( date )}",
//            IMAGEFILTER: "acquisition_date>=(date(now())-integer'${params.imagedays}')",
                    FORMAT: "image/png"] )

    def kmlbuilder = new StreamingMarkupBuilder()
    def kmlnode = {
      mkp.xmlDeclaration()
      kml( "xmlns", "http://earth.google.com/kml/2.1" ) {
        GroundOverlay() {
          name( "OMAR Last ${params.days} Days Imagery Coverage" )
          open( "1" )
          visibility( "1" )
          Icon() {
            href( url )
            viewRefreshMode( "onStop" )
            viewRefreshTime( "${grailsApplication.config.kml.viewRefreshTime}" )
            viewBoundScale( "1.0" )
          }
          LatLonBox() {
            north( 90 )
            south( -90 )
            east( 180 )
            west( -180 )
          }
        }
      }
    }
    return kmlbuilder.bind( kmlnode ).toString()
  }

}
