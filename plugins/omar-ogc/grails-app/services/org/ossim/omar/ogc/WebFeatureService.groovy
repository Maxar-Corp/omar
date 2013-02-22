package org.ossim.omar.ogc

import geoscript.workspace.Workspace
import groovy.xml.StreamingMarkupBuilder
import au.com.bytecode.opencsv.CSVWriter
import geoscript.filter.Filter
import geoscript.layer.Layer
import geoscript.layer.io.GeoJSONWriter
import org.apache.commons.collections.map.CaseInsensitiveMap
import org.apache.commons.io.FilenameUtils
import org.geotools.factory.CommonFactoryFinder

import java.text.SimpleDateFormat

import org.joda.time.DateTimeZone
import org.joda.time.DateTime
import grails.converters.JSON

class WebFeatureService
{
  static transactional = false

  def grailsLinkGenerator
  def grailsApplication
  def dataSourceUnproxied

  private def wmsPersistParams = ["stretch_mode",
      "stretch_mode_region", "sharpen_width", "sharpen_sigma",
      "sharpen_mode", "width", "height", "format", "srs",
      "service", "version", "request", "quicklook", "bands",
      "transparent", "bgcolor", "styles", "null_flip", "bbox"]

  private def layerNames = [
      'raster_entry',
      'video_data_set'
  ]
  private def typeMappings = [
      'Double': 'xsd:double',
      'Integer': 'xsd:int',
      'Long': 'xsd:long',
      'Polygon': 'gml:PolygonPropertyType',
      'MultiPolygon': 'gml:MultiPolygonPropertyType',
      'String': 'xsd:string',
      'java.lang.Boolean': 'xsd:boolean',
      'java.math.BigDecimal': 'xsd:decimal',
      'java.sql.Timestamp': 'xsd:dateTime',
  ]

  def getCapabilities(def wfsRequest)
  {
    def results, contentType

    def y = {
      mkp.xmlDeclaration()
      mkp.declareNamespace( '': "http://www.opengis.net/wfs" )
      mkp.declareNamespace( ogc: "http://www.opengis.net/ogc" )
      mkp.declareNamespace( omar: "http://omar.ossim.org" )
      mkp.declareNamespace( xsi: "http://www.w3.org/2001/XMLSchema-instance" )

      WFS_Capabilities(
          version: '1.0.0',
          'xsi:schemaLocation': "http://www.opengis.net/wfs http://schemas.opengis.net/wfs/1.0.0/WFS-basic.xsd"
      ) {
        Service {
          Name( "OMAR WFS" )
          Title( "OMAR Web Feature Service" )
          Abstract( "This is the WFS implementation for OMAR" )
          Keywords( "WFS, OMAR" )
          OnlineResource( grailsLinkGenerator.link( base: grailsApplication.config.omar.serverURL, absolute: true, controller: 'wfs' ) )
          Fees( "NONE" )
          AccessConstraints( "NONE" )
        }
        Capability {
          Request {
            GetCapabilities {
              DCPType {
                HTTP {
                  Get( onlineResource: grailsLinkGenerator.link( base: grailsApplication.config.omar.serverURL, absolute: true, controller: 'wfs', params: [request: 'GetCapabilities'] ) )
                }
              }
              DCPType {
                HTTP {
                  Post( onlineResource: grailsLinkGenerator.link( absolute: true, controller: 'wfs' ) )
                }
              }
            }
            DescribeFeatureType {
              SchemaDescriptionLanguage {
                XMLSCHEMA()
              }
              DCPType {
                HTTP {
                  Get( onlineResource: grailsLinkGenerator.link( base: grailsApplication.config.omar.serverURL, absolute: true, controller: 'wfs', params: [request: 'DescribeFeatureType'] ) )
                }
              }
              DCPType {
                HTTP {
                  Post( onlineResource: grailsLinkGenerator.link( absolute: true, controller: 'wfs' ) )
                }
              }
            }
            GetFeature {
              ResultFormat {
                GML2()
                //GML3()
                //'SHAPE-ZIP'()
                GEOJSON()
                CSV()
                KML()
                KMLQUERY()
              }
              DCPType {
                HTTP {
                  Get( onlineResource: grailsLinkGenerator.link( base: grailsApplication.config.omar.serverURL, absolute: true, controller: 'wfs', params: [request: 'GetFeature'] ) )
                }
              }
              DCPType {
                HTTP {
                  Post( onlineResource: grailsLinkGenerator.link( absolute: true, controller: 'wfs' ) )
                }
              }
            }
          }
        }
        FeatureTypeList {
          Operations {
            Query()
          }
          def workspace = getWorkspace()
          for ( def layerName in layerNames )
          {
            def layer = workspace[layerName]
            def bounds = layer.bounds
            FeatureType {
              Name( layerName )
              Title()
              Abstract()
              Keywords()
              SRS( layer?.proj?.id )
              LatLongBoundingBox( minx: "${ bounds?.minX }", miny: "${ bounds?.minY }",
                  maxx: "${ bounds?.maxX }", maxy: "${ bounds?.maxY }" )
            }
          }
          workspace?.close()
        }
        ogc.Filter_Capabilities {
          ogc.Spatial_Capabilities {
            ogc.Spatial_Operators {
              ogc.Disjoint()
              ogc.Equals()
              ogc.DWithin()
              ogc.Beyond()
              ogc.Intersect()
              ogc.Touches()
              ogc.Crosses()
              ogc.Within()
              ogc.Contains()
              ogc.Overlaps()
              ogc.BBOX()
            }
          }
          ogc.Scalar_Capabilities {
            ogc.Logical_Operators()
            ogc.Comparison_Operators {
              ogc.Simple_Comparisons()
              ogc.Between()
              ogc.Like()
              ogc.NullCheck()
            }
            ogc.Arithmetic_Operators {
              ogc.Simple_Arithmetic()
              ogc.Functions {
                ogc.Function_Names {
                  def functionNames = CommonFactoryFinder.getFunctionFactories().collect {
                    it.functionNames
                  }.flatten().sort {
                    it.name.toLowerCase()
                  }.groupBy { it.name }.collect { k, v ->
                    [name: k, nArgs: v[0].argumentCount]
                  }
                  functionNames.each { ogc.Function_Name( nArgs: it.nArgs, it.name ) }
                }
              }
            }
          }
        }
      }
    }

    def z = new StreamingMarkupBuilder( encoding: 'UTF-8' ).bind( y )

    results = z?.toString()
    contentType = 'application/xml'

    return [results, contentType]
  }


  def describeFeatureType(def wfsRequest)
  {
    def (workspaceName, layerName) = wfsRequest?.typeName?.split( ':' )
    def workspace = getWorkspace( workspaceName )
    def layer = workspace[layerName]

    def x = {
      mkp.xmlDeclaration()
      mkp.declareNamespace( gml: "http://www.opengis.net/gml" )
      mkp.declareNamespace( xsd: "http://www.w3.org/2001/XMLSchema" )
      mkp.declareNamespace( "${workspaceName}": layer.schema.uri )
      xsd.schema( elementFormDefault: "qualified", targetNamespace: layer.schema.uri ) {
        xsd.import( namespace: "http://www.opengis.net/gml", schemaLocation: "http://schemas.opengis.net/gml/2.1.2/feature.xsd" )
        xsd.complexType( name: "${layerName}Type" ) {
          xsd.complexContent {
            xsd.extension( base: "gml:AbstractFeatureType" ) {
              xsd.sequence {
                for ( def field in layer.schema.fields )
                {
                  def descr = layer.schema.featureType.getDescriptor( field.name )
                  xsd.element(
                      maxOccurs: "${ descr.maxOccurs }",
                      minOccurs: "${ descr.minOccurs }",
                      name: "${ field.name }",
                      nillable: "${ descr.nillable }",
                      type: "${ typeMappings.get( field.typ, field.typ ) }" )
                }
              }
            }
          }
        }
        xsd.element( name: layerName, substitutionGroup: "gml:_Feature", type: "${workspaceName}:${layerName}Type" )
      }
    }

    workspace.close()

    def buffer = new StreamingMarkupBuilder( encoding: 'UTF-8' ).bind( x ).toString()

    [buffer, 'application/xml']
  }

  def getFeature(def wfsRequest)
  {
    def results, contentType

    //if ( wfsRequest.resultType?.toLowerCase() == "hits" )
    //{
    //  results = outputGML( wfsRequest )
    //  contentType = 'text/xml; subtype=gml/2.1.2'
    //}
    //else
    //{
    switch ( wfsRequest?.outputFormat?.toUpperCase() ?: "" )
    {
    case "SHP":
      contentType = "application/octet-stream"
      break;
    case "GML2":
      results = outputGML( wfsRequest )
      contentType = 'text/xml; subtype=gml/2.1.2'
      break
    case "CSV":
      results = outputCSV( wfsRequest )
      contentType = 'text/csv'
      break
    case "KML":
      results = outputKML( wfsRequest )
      contentType = 'application/vnd.google-earth.kml+xml'
      break
    case "KMLQUERY":
      results = outputKMLQuery( wfsRequest )
      contentType = 'application/vnd.google-earth.kml+xml'
      break
    case "JSON":
    case "GEOJSON":
      results = outputJSON( wfsRequest )
      contentType = 'application/json'
      break
    default:
      results = outputGML( wfsRequest )
      contentType = 'text/xml; subtype=gml/2.1.2'
    }
    //}

    return [results, contentType]
  }

  private getTagLib()
  {
    grailsApplication.mainContext.getBean( "org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib" )

  }

  private String outputGML(def wfsRequest)
  {
    def results
    def describeFeatureTypeURL = grailsLinkGenerator.link(
        base: grailsApplication.config.omar.serverURL,
        absolute: true,
        controller: 'wfs',
        params: [
            service: 'WFS',
            version: '1.0.0',
            request: 'DescribeFeatureType',
            typeName: wfsRequest.typeName
        ]
    )

    def filterParams = [
        filter: wfsRequest?.filter ?: Filter.PASS,
        max: wfsRequest.maxFeatures ?: -1,
        start: wfsRequest?.offset ?: -1
    ]
    if ( wfsRequest.sortBy )
    {
      filterParams.sort = wfsRequest.convertSortByToArray();
    }
    def filter
    try
    {
//        println "BEFORE"
      filter = new Filter( filterParams.filter )
//        println "AFTER"
    }
    catch ( e )
    {
      e.printStackTrace()
    }
    def y

    if ( wfsRequest.resultType?.toLowerCase() == "hits" )
    {
      def workspace = getWorkspace()
      def layer = workspace[wfsRequest?.typeName.split( ':' )[-1]]
      def count = layer.count( filter );
      // println "COUNT = ${count}";
      def timestamp = new DateTime( DateTimeZone.UTC );
      y = {
        mkp.xmlDeclaration()
        mkp.declareNamespace( wfs: "http://www.opengis.net/wfs" )
        mkp.declareNamespace( omar: "http://omar.ossim.org" )
        mkp.declareNamespace( gml: "http://www.opengis.net/gml" )
        mkp.declareNamespace( xsi: "http://www.w3.org/2001/XMLSchema-instance" )

        wfs.FeatureCollection(
            xmlns: 'http://www.opengis.net/wfs',
            'xsi:schemaLocation': "http://omar.ossim.org ${ describeFeatureTypeURL } http://www.opengis.net/wfs http://schemas.opengis.net/wfs/1.0.0/WFS-basic.xsd",
            'numberOfFeatures': "${count}",
            "timestamp": "${timestamp}"
        )
      }
    }
    else
    {
      y = {
        def workspace = getWorkspace()
        def layer = workspace[wfsRequest?.typeName.split( ':' )[-1]]

        //println wfsRequest?.filter

//      xxx.each { println it }


        def cursor = layer.getCursor( filterParams )

        mkp.xmlDeclaration()
        mkp.declareNamespace( wfs: "http://www.opengis.net/wfs" )
        mkp.declareNamespace( omar: "http://omar.ossim.org" )
        mkp.declareNamespace( gml: "http://www.opengis.net/gml" )
        mkp.declareNamespace( xsi: "http://www.w3.org/2001/XMLSchema-instance" )

        wfs.FeatureCollection(
            xmlns: 'http://www.opengis.net/wfs',
            'xsi:schemaLocation': "http://omar.ossim.org ${ describeFeatureTypeURL } http://www.opengis.net/wfs http://schemas.opengis.net/wfs/1.0.0/WFS-basic.xsd"
        ) {
          gml.boundedBy {
            gml.'null'( "unknown" )
          }

          while ( cursor?.hasNext() )
          {
            def feature = cursor.next()
            def featureId = feature.id
            //println feature

            gml.featureMember {
              omar."${ wfsRequest?.typeName.split( ':' )[-1] }"( fid: featureId ) {

                for ( def attribute in feature.attributes )
                {
                  if ( attribute?.value != null )
                  {

                    if ( attribute.key == "ground_geom" )
                    {
                      omar.ground_geom {

                        /*
                        gml.Polygon( srsName: "http://www.opengis.net/gml/srs/epsg.xml#4326" ) {
                          gml.outerBoundaryIs {
                            gml.LinearRing {
                              gml.coordinates( 'xmlns:gml': "http://www.opengis.net/gml", decimal: ".", cs: ",", ts: "", """
                            -122.56492547,38.02596313 -122.1092658,38.02339409 -122.11359067,37.66295699
                            -122.56703818,37.66549309 -122.56492547,38.02596313""" )
                            }
                          }
                        }
                        */

                        def geom = new XmlSlurper( false, false ).parseText( feature.ground_geom.gml2 as String )

                        geom.@srsName = 'http://www.opengis.net/gml/srs/epsg.xml#4326'

                        mkp.yield( geom )

                      }
                    }
                    else
                    {
                      //println "${ attribute.key }: ${ typeMappings[feature.schema.field( attribute.key ).typ] }"

                      switch ( attribute.key )
                      {
                      case "other_tags_xml":
                      case "tie_point_set":
                        omar."${ attribute.key }" {
                          mkp.yieldUnescaped( "<![CDATA[${ attribute.value }]]>" )
                        }
                        break
                      default:
                        switch ( typeMappings[feature.schema.field( attribute.key ).typ] )
                        {
                        case "xsd:dateTime":
                          //println attribute.value?.format( "yyyy-MM-dd'T'hh:mm:ss.SSS" )
                          omar."${ attribute.key }"( attribute.value?.format( "yyyy-MM-dd'T'hh:mm:ss.SSS" ) )
                          break
                        default:
                          omar."${ attribute.key }"( attribute.value )
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }

        cursor?.close()
        workspace?.close()
      }
    }

    def z = new StreamingMarkupBuilder( encoding: 'UTF-8' ).bind( y )

    results = z?.toString()

    return results
  }

  private String createKmlDescription(def wfsRequest,
                                      def feature)
  {
    def flashDirRoot = grailsApplication.config.videoStreaming.flashDirRoot
    def flashUrlRoot = grailsApplication.config.videoStreaming.flashUrlRoot
    def fields
    def labels
    def formatters
    def typeName = wfsRequest?.typeName.split( ':' )[-1].toLowerCase();
    def thumbnail
    def url
    def tagLibBean = getTagLib();
    def omarServerUrl = grailsApplication.config.omar.serverURL
    // def flvUrl
    // def flashPlayerUrl = tagLibBean.createLinkTo(dir: "js", file: "player.swf", base: "${grailsApplication.config.omar.serverURL}", absolute: true)
    def mpegFile = feature["filename"] as File
    def flvFile = "${flashDirRoot}/${mpegFile.name}.flv" as File
    if ( typeName == "raster_entry" )
    {
      fields = grailsApplication.config.export.rasterEntry.fields
      labels = grailsApplication.config.export.rasterEntry.labels
      formatters = grailsApplication.config.export.rasterEntry.formatters
      url = tagLibBean.createLink( absolute: true, base: omarServerUrl,
          controller: "mapView", params: [layers: feature["index_id"]] )

      thumbnail = tagLibBean.createLink( absolute: true, base: omarServerUrl,
          controller: "thumbnail", action: "show", id: feature["id"],
          params: [size: 128, projectionType: 'imagespace'] )
    }
    else if ( typeName == "video_data_set" )
    {
      fields = grailsApplication.config.export.videoDataSet.fields
      labels = grailsApplication.config.export.videoDataSet.labels
      formatters = grailsApplication.config.export.videoDataSet.formatters
      url = tagLibBean.createLink( absolute: true, base: omarServerUrl,
          controller: "videoStreaming",
          action: "show",
          id: feature['index_id'] )
      thumbnail = tagLibBean.createLink( absolute: true,
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
    def searchUrl = tagLibBean.createLink( absolute: true, base: "${grailsApplication.config.omar.serverURL}", controller: "federation", action: "search" )
    description << "<tr>"
    description << "<th align='right'>Search:</th>"
    description << "<td><a href='${searchUrl}'>Find More Data</a></td></tr>"

    def logoUrl = "${grailsApplication.config.omar.serverURL}/images/omarLogo.png"
    description << "<tfoot><tr><td colspan='2'><a href='${grailsApplication.config.omar.serverURL}'><img src='${logoUrl}'/></a></td></tr></tfoot>"
    description << "</table>"

    description.buffer
  }

  private String outputKML(def wfsRequest)
  {
    def tagLibBean = getTagLib()
    def wmsParams = [:]
    def caseInsensitiveParams = new CaseInsensitiveMap( wfsRequest.properties )
    def pushPin = tagLibBean.resource( absolute: true, base: "${grailsApplication.config.omar.serverURL}", plugin: "omar-common-ui", dir: "images/google", file: "red-pushpin.png" )

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
    wmsParams.remove( "elevation" )
    wmsParams.remove( "time" )
    wmsParams?.remove( "bbox" )
    wmsParams?.remove( "width" )
    wmsParams?.remove( "height" )
    wmsParams.remove( "action" )
    wmsParams.remove( "controller" )
    def workspace = getWorkspace()
    def layer = workspace[wfsRequest?.typeName.split( ':' )[-1]]
    def filterParams = [
        filter: wfsRequest?.filter ?: Filter.PASS,
        max: wfsRequest?.maxFeatures ?: -1,
        start: wfsRequest?.offset ?: -1,
    ]
    if ( wfsRequest.sortBy )
    {
      filterParams.sort = wfsRequest.convertSortByToArray();
    }
    def bbox
    def sdf = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss'Z'" )
    // def cursor = layer.getCursor( filterParams )
    def kmlBuilder = new StreamingMarkupBuilder();
    def kmlwriter = new StringWriter()
    kmlwriter << """<?xml version='1.0'?><kml xmlns='http://earth.google.com/kml/2.1'>"""
    kmlwriter << "<Document>"

    def cursor = layer.getCursor( filterParams )
    while ( cursor?.hasNext() )
    {
      def feature = cursor.next();
      def description = createKmlDescription( wfsRequest, feature );
      def bounds = feature["ground_geom"].bounds
      def groundCenterLon = ( bounds?.minX + bounds?.maxX ) * 0.5;
      def groundCenterLat = ( bounds?.minY + bounds?.maxY ) * 0.5;
      def renderedHtml = description

      if ( wfsRequest?.typeName.split( ':' )[-1]?.toLowerCase() == "raster_entry" )
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
        kmlwriter << "<LookAt><longitude>${groundCenterLon}</longitude><latitude>${groundCenterLat}</latitude><altitude>0.0</altitude><heading>0.0</heading><tilt>0.0</tilt><range>${defaultRange}</range><altitudeMode>clampToGround</altitudeMode></LookAt>"
        kmlwriter << "<open>1</open>"
        kmlwriter << "<visibility>1</visibility>"
        wmsParams.layers = feature['index_id']
        def wmsURL = tagLibBean.createLink(
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
        def createFlvUrl = tagLibBean.createLink( absolute: true, base: "${grailsApplication.config.omar.serverURL}", controller: "videoStreaming", action: "show", id: feature['index_id'] )
        //  def descriptionText = ""
        //  def logoUrl = "${grailsApplication.config.omar.serverURL}/images/omarLogo.png"
        //  def thumbnailUrl = tagLibBean.createLink(absolute: true, base: "${grailsApplication.config.omar.serverURL}", controller: "thumbnail", action: "frame", id: feature['id'], params: [size: 128])
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

    kmlwriter.buffer
  }

  private def outputKMLQuery(def wfsRequest)
  {
    def caseInsensitiveParams = new CaseInsensitiveMap();
    wfsRequest.properties.each { caseInsensitiveParams.put( it.key.toLowerCase(), it.value ) }
    def filter = caseInsensitiveParams.filter ?: ""
    def bbox

    if ( !filter.contains( "BBOX(" ) )
    {
      if ( !filter )
      {
        filter = "BBOX(ground_geom,[bboxWest],[bboxSouth],[bboxEast],[bboxNorth])"
      }
      else
      {
        if ( filter.endsWith( ")" ) )
        {
          filter = "${filter}AND(BBOX(ground_geom,[bboxWest],[bboxSouth],[bboxEast],[bboxNorth]))"
        }
        else
        {
          filter = "(${filter})AND(BBOX(ground_geom,[bboxWest],[bboxSouth],[bboxEast],[bboxNorth]))"
        }
      }
    }
    else
    {
      def bboxString = filter.find( "BBOX\\(.*\\)" );
      if ( bboxString )
      {
        def splitBbox = bboxString.split( "," );
        if ( splitBbox.size() == 5 )
        {
          def stripEnding = splitBbox[4].trim()
          stripEnding = stripEnding.substring( 0, stripEnding.indexOf( ')' ) - 1 )
          bbox = [minx: splitBbox[1].toDouble(),
              miny: splitBbox[2].toDouble(),
              maxx: splitBbox[3].toDouble(),
              maxy: stripEnding.toDouble()
          ]
        }
      }
    }
    //println filter
    /*
    if (filter.contains("BBOX("))
    {
        filter = filter.replaceAll("BBOX\\(.*\\)", "BBOX(ground_geom,[bboxWest],[bboxSouth],[bboxEast],[bboxNorth])")
    }
    else
    {
        if (!filter)
        {
            filter = "BBOX(ground_geom,[bboxWest],[bboxSouth],[bboxEast],[bboxNorth])"
        }
        else
        {
            if (filter.endsWith(")"))
            {
                filter = "${filter}AND(BBOX(ground_geom,[bboxWest],[bboxSouth],[bboxEast],[bboxNorth]))"
            }
            else
            {
                filter = "(${filter})AND(BBOX(ground_geom,[bboxWest],[bboxSouth],[bboxEast],[bboxNorth]))"
            }
        }
    }
    */
    caseInsensitiveParams.remove( "filter" );
    caseInsensitiveParams.remove( "class" );
    filter = filter.encodeAsURL()
    def tagLibBean = getTagLib()
    caseInsensitiveParams.outputFormat = "kml"
    // caseInsensitiveParams.each{k,v->
    //     caseInsensitiveParams."${k}" = v.encodeAsURL()
    // }

    def kmlQueryUrl = tagLibBean.createLink( absolute: true, base: "${grailsApplication.config.omar.serverURL}",
        controller: "wfs", action: "index", params: caseInsensitiveParams )
    def kmlwriter = new StringWriter()
    kmlwriter << """<?xml version='1.0'?><kml xmlns='http://earth.google.com/kml/2.1'>"""
    // kmlwriter << "<open>1</open>"
    // if (bbox)
    // {
    //     def groundCenterLon = (bbox.minx+bbox.maxx)*0.5
    //     def groundCenterLat = (bbox.miny+bbox.maxy)*0.5
    //     def defaultRange    = 15000;
    //    kmlwriter << "<LookAt><longitude>${groundCenterLon}</longitude><latitude>${groundCenterLat}</latitude><altitude>0.0</altitude><heading>0.0</heading><tilt>0.0</tilt><range>${defaultRange}</range><altitudeMode>clampToGround</altitudeMode></LookAt>"
    //}
    kmlwriter << "<NetworkLink>"
    kmlwriter << "<name>KML Query</name>"
    kmlwriter << "<Link>" <<
        "<href><![CDATA[${kmlQueryUrl}]]></href>" <<
        "<httpQuery>googleClientVersion=[clientVersion]</httpQuery>" <<
        "<viewFormat>filter=${filter}</viewFormat>" <<
        "<viewRefreshMode>onRequest</viewRefreshMode>"
    kmlwriter << "</Link></NetworkLink></kml>"
    String kmlText = kmlwriter.buffer

    return kmlText
  }

  private def outputCSV(def wfsRequest)
  {
    def fields
    def labels
    def formatters
    def typeName = wfsRequest?.typeName.split( ':' )[-1].toLowerCase();
    if ( typeName == "raster_entry" )
    {
      fields = grailsApplication.config.export.rasterEntry.fields
      labels = grailsApplication.config.export.rasterEntry.labels
      formatters = grailsApplication.config.export.rasterEntry.formatters
    }
    else if ( typeName == "video_data_set" )
    {
      fields = grailsApplication.config.export.videoDataSet.fields
      labels = grailsApplication.config.export.videoDataSet.labels
      formatters = grailsApplication.config.export.videoDataSet.formatters
    }

    def workspace = getWorkspace()
    def layer = workspace[wfsRequest?.typeName]
    def filter = [
        filter: wfsRequest?.filter ?: Filter.PASS,
        //sort: ""// [["<COLUMN NAME>","ASC|DESC"]]
    ]
    def filterParams = [
        filter: wfsRequest?.filter ?: Filter.PASS,
        max: wfsRequest.maxFeatures ?: -1,
        start: wfsRequest?.offset ?: -1,
        //sort: [["<COLUMN NAME>","ASC|DESC"]]
    ]
    if ( wfsRequest.sortBy )
    {
      filterParams.sort = wfsRequest.convertSortByToArray();
    }

    def stringBuffer = new StringWriter()
    def csvWriter = new CSVWriter( stringBuffer )

    csvWriter.writeNext( labels as String[] )

    def cursor = layer.getCursor( filterParams )
    while ( cursor?.hasNext() )
    {
      def feature = cursor.next();
      def data = []
      for ( field in fields )
      {
        def adjustedField = field.replaceAll( "[a-z][A-Z]", { v -> "${v[0]}_${v[1].toLowerCase()}" } )

        if ( formatters && formatters[field] )
        {
          data << formatters[field].call( feature[adjustedField] )
        }
        else
        {
          data << feature[adjustedField]
        }
      }

      csvWriter.writeNext( data as String[] )

    }
    csvWriter.close()
    cursor?.close()
    workspace?.close()

    return stringBuffer.toString();
  }

  private def outputJSON(def wfsRequest)
  {
    def results
    def workspace = getWorkspace()
    def layer = workspace[wfsRequest?.typeName.split( ':' )[-1]]
    def filter = [
        filter: wfsRequest?.filter ?: Filter.PASS,
        //sort: ""// [["<COLUMN NAME>","ASC|DESC"]]
    ]
    def filterParams = [
        filter: wfsRequest?.filter ?: Filter.PASS,
        max: wfsRequest.maxFeatures ?: -1,
        start: wfsRequest?.offset ?: -1,
        //sort: [["<COLUMN NAME>","ASC|DESC"]]
    ]
    if ( wfsRequest.sortBy )
    {

      // filterParams.sort =null//[["TITLE".toUpperCase(),"DESC"]]
      filterParams.sort = wfsRequest.convertSortByToArray();//wfsRequest.sortBy.substring()//JSON.parse( wfsRequest.sort );

      //println filterParams
    }
    try
    {
      filter = new Filter( filterParams.filter )
    }
    catch ( e )
    {
      e.printStackTrace()
    }

    if ( wfsRequest.resultType?.toLowerCase() == "hits" )
    {
      def count = layer.count( filter );
      def timestamp = new DateTime( DateTimeZone.UTC );
      results = "${[numberOfFeatures: count, timestamp: timestamp] as JSON}"
    }
    else
    {
      def writer = new GeoJSONWriter()
      def cursor = layer.getCursor( filterParams );
      def newLayer = new Layer( cursor.col )

      results = writer.write( newLayer )
      cursor?.close()
    }
    workspace?.close()
    return results
  }


  private def getWorkspace(def workspaceName = 'omar')
  {
    def workspace = new Workspace(
        dbtype: 'postgis',
        host: 'localhost',
        port: 5432,
        user: 'postgres',
        database: 'omardb-1.8.16-prod',
        'Expose primary keys': true,
        namespace: 'http://omar.ossim.org'
    )
    workspace
  }
}
