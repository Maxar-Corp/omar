package org.ossim.omar.ogc.wfs

import geoscript.filter.Filter
import groovy.xml.StreamingMarkupBuilder
import org.apache.commons.collections.map.CaseInsensitiveMap
import org.apache.commons.io.FilenameUtils

import java.text.SimpleDateFormat

/**
 * Created with IntelliJ IDEA.
 * User: sbortman
 * Date: 2/25/13
 * Time: 9:17 AM
 * To change this template use File | Settings | File Templates.
 */
class KmlResultFormat implements ResultFormat
{
  def name = "KML"
  def contentType = 'application/vnd.google-earth.kml+xml'

  def grailsApplication
  def grailsLinkGenerator

  private def wmsPersistParams = ["stretch_mode",
      "stretch_mode_region", "sharpen_width", "sharpen_sigma",
      "sharpen_mode", "width", "height", "format", "srs",
      "service", "version", "request", "quicklook", "bands",
      "transparent", "bgcolor", "styles", "null_flip", "bbox"]


  def getFeature(def wfsRequest, def workspace)
  {
    def wmsParams = [:]
    def caseInsensitiveParams = new CaseInsensitiveMap( wfsRequest.properties )
    def pushPin = grailsLinkGenerator.resource( absolute: true, base: "${grailsApplication.config.omar.serverURL}", plugin: "omar-common-ui", dir: "images/google", file: "red-pushpin.png" )

    SimpleDateFormat isdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" )
    SimpleDateFormat osdf = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss'Z'" )
    caseInsensitiveParams.format = "image/png"
    caseInsensitiveParams.version = "1.1.0"
    caseInsensitiveParams.transparent = "TRUE"
    caseInsensitiveParams.request = "GetMap"
    caseInsensitiveParams.service = "WMS"
    caseInsensitiveParams.srs = "EPSG:4326"
    caseInsensitiveParams.stretch_mode = "linear_auto_min_max"
    caseInsensitiveParams.stretch_mode_region = "viewport"
    caseInsensitiveParams.bands = "default"

    caseInsensitiveParams.each { wmsParams.put( it.key.toLowerCase(), it.value ) }
    wmsParams = wmsParams.subMap( wmsPersistParams )
    def filter = caseInsensitiveParams.filter
    def bbox = caseInsensitiveParams.bbox
    // We will only pass BBOX if needed
    //
    if ( bbox && filter && filter.toLowerCase().contains( "bbox" ) )
    {
      caseInsensitiveParams.remove( "bbox" );
      bbox = null
    }
    wmsParams.remove( "elevation" )
    wmsParams.remove( "time" )
    wmsParams?.remove( "bbox" )
    wmsParams?.remove( "width" )
    wmsParams?.remove( "height" )
    wmsParams.remove( "action" )
    wmsParams.remove( "controller" )
    def layer = workspace[wfsRequest?.typeName]
    if ( bbox )
    {
      if ( filter )
      {
        filter += " AND BBOX(ground_geom,${bbox})"
      }
      else
      {
        filter = "BBOX(ground_geom,${bbox})"
      }
    }
    def filterParams = [
        filter: filter ?: Filter.PASS,
        max: wfsRequest?.maxFeatures ?: -1,
        start: wfsRequest?.offset ?: -1,
    ]
    if ( wfsRequest.sortBy )
    {
      filterParams.sort = wfsRequest.convertSortByToArray();
    }
    def sdf = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss'Z'" )
    // def cursor = layer.getCursor( filterParams )
    def kmlBuilder = new StreamingMarkupBuilder();
    def kmlwriter = new StringWriter()
    kmlwriter << """<?xml version='1.0'?><kml xmlns='http://earth.google.com/kml/2.1'>"""
    kmlwriter << "<Document>"

    def cursor = layer.getCursor( filterParams )
    def idx = 1000;
    while ( cursor?.hasNext() )
    {
      --idx
      def feature = cursor.next();
      def description = createKmlDescription( wfsRequest, feature );
      def bounds = feature["ground_geom"].bounds
      def groundCenterLon = ( bounds?.minX + bounds?.maxX ) * 0.5;
      def groundCenterLat = ( bounds?.minY + bounds?.maxY ) * 0.5;
      def renderedHtml = description

      if ( wfsRequest?.typeName?.toLowerCase() == "raster_entry" )
      {
        kmlwriter << "<name>OMAR Rasters</name>"

        def acquisition = ( feature["acquisition_date"] ) ? sdf.format( feature["acquisition_date"] ) : null
        // wmsParams?.layers = feature["index_id"]
        def mpp = feature["gsdy"]//rasterEntry.getMetersPerPixel()
        // calculate a crude metric for putting an image that almost fits within the google viewport
        //
        def defaultRange = mpp * Math.sqrt( ( feature["width"] ** 2 ) + ( feature["height"] ** 2 ) );
        if ( defaultRange < 1 )
        {
          defaultRange = 15000
        }

        kmlwriter << "<GroundOverlay><name>${feature['title'] ?: ( feature['filename'] as File ).name}</name><Snippet/><description><![CDATA[${renderedHtml}]]>}</description>"
        kmlwriter << "<drawOrder>${idx}</drawOrder>"
        kmlwriter << "<LookAt><longitude>${groundCenterLon}</longitude><latitude>${groundCenterLat}</latitude><altitude>0.0</altitude><heading>0.0</heading><tilt>0.0</tilt><range>${defaultRange}</range><altitudeMode>clampToGround</altitudeMode></LookAt>"
        kmlwriter << "<open>1</open>"
        kmlwriter << "<visibility>1</visibility>"
        wmsParams.layers = feature['index_id']
        def wmsURL = grailsLinkGenerator.link(
            absolute: true, base: "${grailsApplication.config.omar.serverURL}",
            controller: "ogc", action: "wms", params: wmsParams
        )
        kmlwriter << "<Icon><href><![CDATA[${wmsURL}]]></href>" <<
            "<viewRefreshMode>onStop</viewRefreshMode><viewRefreshTime>1</viewRefreshTime>" <<
            "<viewBoundScale>0.85</viewBoundScale>" <<
            "<viewFormat><![CDATA[BBOX=[bboxWest],[bboxSouth],[bboxEast],[bboxNorth]&width=[horizPixels]&height=[vertPixels]]]></viewFormat></Icon>"

        kmlwriter << "<LatLonBox>"
        //  if (bbox)
        //  {
        //
        //  }
        //  else
        //  {
        kmlwriter << "<north>${bounds?.maxY}</north><south>${bounds?.minY}</south>"
        kmlwriter << "<east>${bounds?.maxX}</east><west>${bounds?.minX}</west>"
        //  }
        kmlwriter << "</LatLonBox>"
        if ( acquisition )
        {
          kmlwriter << "<TimeStamp><when>${acquisition}</when></TimeStamp>"
        }
        kmlwriter << "</GroundOverlay>"
      }
      else
      {
        def flashbasename = "${FilenameUtils.getBaseName( feature['filename'] )}.flv"
        def createFlvUrl = grailsLinkGenerator.link( absolute: true, base: "${grailsApplication.config.omar.serverURL}", controller: "videoStreaming", action: "show", id: feature['index_id'] )
        //  def descriptionText = ""
        //  def logoUrl = "${grailsApplication.config.omar.serverURL}/images/omarLogo.png"
        //  def thumbnailUrl = tagLibBean.link(absolute: true, base: "${grailsApplication.config.omar.serverURL}", controller: "thumbnail", action: "frame", id: feature['id'], params: [size: 128])
        kmlwriter << "<name>OMAR Videos</name>"
        def styleBuilder = new StreamingMarkupBuilder().bind {
          Style( "id": "sh_red" ) {
            LineStyle() {
              color( "ffOOOOff" )
            }
            PolyStyle {
              color( "7f00005f" )
            }
            IconStyle {
              color( "ff00007f" )
              scale( "1.0" )
              Icon() {
                href( "${pushPin}" )
              }
              hotspot( "x": "20", "y": "2", "xunits": "pixels", "yunits": "pixels" )
            }
          }
          Style( "id": "sn_red" ) {
            LineStyle() {
              color( "ff00007f" )
            }
            PolyStyle {
              color( "3f00001f" )
            }
            IconStyle {
              color( "ff00007f" )
              scale( "1.0" )
              Icon() {
                href( "${pushPin}" )
              }
              hotspot( "x": "20", "y": "2", "xunits": "pixels", "yunits": "pixels" )
            }
          }
          StyleMap( "id": "red" ) {
            Pair() {
              key( "normal" )
              styleUrl( "#sn_red" )
            }
            Pair() {
              key( "highlight" )
              styleUrl( "#sh_red" )
            }
          }
        }
        def point = null
        def polygons = []
        def kmlPoly = ""
        feature['ground_geom'].each() { geom ->
          // for now until we have a utility to get access to all polgons we will assume multi
          // geom and each is a poly
          //
          ( 0..geom.getNumGeometries() - 1 ).each() { geomIdx ->
            def poly = geom.getGeometryN( geomIdx ) as geoscript.geom.Polygon
            if ( poly )
            {
              kmlPoly = ""
              def ring = poly.getExteriorRing();
              def coordinates = ring.getCoordinates();
              if ( coordinates.size() > 0 )
              {
                ( 0..coordinates.size() - 1 ).each() { coordIdx ->
                  kmlPoly = "${kmlPoly} ${coordinates[coordIdx].x},${coordinates[coordIdx].y}"
                  if ( !point )
                  {
                    point = "${coordinates[coordIdx].x},${coordinates[coordIdx].y}"
                  }
                }
              }
              polygons.add( kmlPoly )
            }
          }
        }
        def multiGeometryBuilder = new StreamingMarkupBuilder().bind {
          MultiGeometry() {
            polygons.each { polygon ->
              Polygon() {
                tessellate( "1" )
                // altitudeMode("relativeToGround")
                altitudeMode( "clampToGround" )
                outerBoundaryIs() {
                  LinearRing() {
                    coordinates( "${polygon}" )
                  }
                }
              }
            }
            Point() {
              altitudeMode( "clampToGround" )

              //altitudeMode("relativeToGround")
              coordinates( "${point}" )
            }
          } // END MultiGeometry()
        }
        kmlwriter << styleBuilder.toString()

        kmlwriter << "<Placemark><styleUrl>#red</styleUrl>"
        kmlwriter << "<name>${flashbasename}</name>"
        kmlwriter << "<description><![CDATA[${description}]]></description>"
        kmlwriter << "<Snippet><![CDATA[<a href='${createFlvUrl}'>CLICK TO PLAY</a>]]></Snippet>"
        kmlwriter << multiGeometryBuilder.toString()

        if ( feature['start_date'] )
        {
          kmlwriter << "<Timestamp>"
          kmlwriter << "<when>${osdf.format( new Date( isdf.parse( feature['start_date'] as String ) as String ) )}</when>"
          kmlwriter << "</Timestamp>"
        }

        kmlwriter << "</Placemark>"
      }
    }
    kmlwriter << "</Document></kml>"
    cursor?.close()
    workspace?.close()

    [kmlwriter.buffer.toString(), contentType]
  }

  private String createKmlDescription(def wfsRequest,
                                      def feature)
  {
    def flashDirRoot = grailsApplication.config.videoStreaming.flashDirRoot
    def flashUrlRoot = grailsApplication.config.videoStreaming.flashUrlRoot
    def fields
    def labels
    def formatters
    def typeName = wfsRequest?.typeName.toLowerCase();
    def thumbnail
    def url

    def omarServerUrl = grailsApplication.config.omar.serverURL
    // def flvUrl
    // def flashPlayerUrl = tagLibBean.linkTo(dir: "js", file: "player.swf", base: "${grailsApplication.config.omar.serverURL}", absolute: true)
    def mpegFile = feature["filename"] as File
    def flvFile = "${flashDirRoot}/${mpegFile.name}.flv" as File
    if ( typeName == "raster_entry" )
    {
      fields = grailsApplication.config.export.rasterEntry.fields
      labels = grailsApplication.config.export.rasterEntry.labels
      formatters = grailsApplication.config.export.rasterEntry.formatters

      url = grailsLinkGenerator.link( absolute: true, base: omarServerUrl,
          controller: "mapView", params: [layers: feature["index_id"]] )

      thumbnail = grailsLinkGenerator.link( absolute: true, base: omarServerUrl,
          controller: "thumbnail", action: "show", id: feature["id"],
          params: [size: 128, projectionType: 'imagespace'] )
    }
    else if ( typeName == "video_data_set" )
    {
      fields = grailsApplication.config.export.videoDataSet.fields
      labels = grailsApplication.config.export.videoDataSet.labels
      formatters = grailsApplication.config.export.videoDataSet.formatters
      url = grailsLinkGenerator.link( absolute: true, base: omarServerUrl,
          controller: "videoStreaming",
          action: "show",
          id: feature['index_id'] )
      thumbnail = grailsLinkGenerator.link( absolute: true,
          base: omarServerUrl,
          controller: "thumbnail",
          action: "frame",
          id: feature['id'],
          params: [size: 128] )
      // flvUrl = new URL("${flashUrlRoot}/${flvFile.name}")
    }
    def description = new StringWriter()
    description << "<table border='1'>"
    description << "<tr>"
    description << "<th align='right'>Thumbnail:</th>"
    description << "<td><a href='${url}'><img src='${thumbnail}'/></a></td></tr>"


    ( 0..fields.size() - 1 ).each { idx ->
      def field = fields[idx];
      def label = labels[idx];
      def value

      def adjustedField = field.replaceAll( "[a-z][A-Z]", { v -> "${v[0]}_${v[1].toLowerCase()}" } )

      if ( formatters && formatters[field] )
      {
        value = formatters[field].call( feature[adjustedField] )
      }
      else
      {
        value = feature[adjustedField]
      }

      if ( field == "filename" )
      {
        description << "<tr>"
        description << "<th align='right'>${label}:</th>"
        description << "<td><a href='${url}'>${( value as File ).name}</a></td></tr>"
      }
      else
      {
        description << "<tr>"
        description << "<th align='right'>${label}:</th>"
        description << "<td>${value}</td></tr>"
      }
    }

    def searchUrl = grailsLinkGenerator.link( absolute: true, base: "${grailsApplication.config.omar.serverURL}", controller: "federation", action: "search" )
    description << "<tr>"
    description << "<th align='right'>Search:</th>"
    description << "<td><a href='${searchUrl}'>Find More Data</a></td></tr>"

    def logoUrl = "${grailsApplication.config.omar.serverURL}/images/omarLogo.png"
    description << "<tfoot><tr><td colspan='2'><a href='${grailsApplication.config.omar.serverURL}'><img src='${logoUrl}'/></a></td></tr></tfoot>"
    description << "</table>"

    description.buffer
  }

}
