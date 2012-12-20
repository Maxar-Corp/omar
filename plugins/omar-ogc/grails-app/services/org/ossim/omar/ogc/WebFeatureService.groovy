package org.ossim.omar.ogc

import groovy.xml.StreamingMarkupBuilder

import geoscript.filter.Filter
import geoscript.layer.Layer
import geoscript.layer.io.GeoJSONWriter
import geoscript.workspace.Database
import geoscript.workspace.PostGIS

import org.geotools.data.postgis.PostgisNGDataStoreFactory
import grails.web.JSONBuilder
import org.joda.time.DateTimeZone
import org.joda.time.DateTime
import grails.converters.JSON

class WebFeatureService
{
  static transactional = false

  def grailsLinkGenerator
  def grailsApplication
  def dataSource

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
          version: '1.0.0', updateSequence: '0',
          'xsi:schemaLocation': "http://www.opengis.net/wfs http://schemas.opengis.net/wfs/1.0.0/WFS-capabilities.xsd"
      ) {
        Service {
          Name( "OMAR" )
          Title( "OMAR WFS" )
          Abstract()
          Keywords()
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
                  ogc.Function_Name( nArgs: "1", "abs" )
                  ogc.Function_Name( nArgs: "1", "abs_2" )
                  ogc.Function_Name( nArgs: "1", "abs_3" )
                  ogc.Function_Name( nArgs: "1", "abs_4" )
                  ogc.Function_Name( nArgs: "1", "acos" )
                  ogc.Function_Name( nArgs: "1", "Area" )
                  ogc.Function_Name( nArgs: "1", "area2" )
                  ogc.Function_Name( nArgs: "1", "asin" )
                  ogc.Function_Name( nArgs: "1", "atan" )
                  ogc.Function_Name( nArgs: "1", "atan2" )
                  ogc.Function_Name( nArgs: "3", "between" )
                  ogc.Function_Name( nArgs: "1", "boundary" )
                  ogc.Function_Name( nArgs: "1", "boundaryDimension" )
                  ogc.Function_Name( nArgs: "2", "buffer" )
                  ogc.Function_Name( nArgs: "3", "bufferWithSegments" )
                  ogc.Function_Name( nArgs: "7", "Categorize" )
                  ogc.Function_Name( nArgs: "1", "ceil" )
                  ogc.Function_Name( nArgs: "1", "centroid" )
                  ogc.Function_Name( nArgs: "2", "classify" )
                  ogc.Function_Name( nArgs: "1", "Collection_Average" )
                  ogc.Function_Name( nArgs: "1", "Collection_Bounds" )
                  ogc.Function_Name( nArgs: "0", "Collection_Count" )
                  ogc.Function_Name( nArgs: "1", "Collection_Max" )
                  ogc.Function_Name( nArgs: "1", "Collection_Median" )
                  ogc.Function_Name( nArgs: "1", "Collection_Min" )
                  ogc.Function_Name( nArgs: "1", "Collection_Sum" )
                  ogc.Function_Name( nArgs: "1", "Collection_Unique" )
                  ogc.Function_Name( nArgs: "1", "Concatenate" )
                  ogc.Function_Name( nArgs: "2", "contains" )
                  ogc.Function_Name( nArgs: "2", "convert" )
                  ogc.Function_Name( nArgs: "1", "convexHull" )
                  ogc.Function_Name( nArgs: "1", "cos" )
                  ogc.Function_Name( nArgs: "2", "crosses" )
                  ogc.Function_Name( nArgs: "2", "dateFormat" )
                  ogc.Function_Name( nArgs: "2", "dateParse" )
                  ogc.Function_Name( nArgs: "2", "difference" )
                  ogc.Function_Name( nArgs: "1", "dimension" )
                  ogc.Function_Name( nArgs: "2", "disjoint" )
                  ogc.Function_Name( nArgs: "2", "distance" )
                  ogc.Function_Name( nArgs: "1", "double2bool" )
                  ogc.Function_Name( nArgs: "1", "endAngle" )
                  ogc.Function_Name( nArgs: "1", "endPoint" )
                  ogc.Function_Name( nArgs: "1", "env" )
                  ogc.Function_Name( nArgs: "1", "envelope" )
                  ogc.Function_Name( nArgs: "2", "EqualInterval" )
                  ogc.Function_Name( nArgs: "2", "equalsExact" )
                  ogc.Function_Name( nArgs: "3", "equalsExactTolerance" )
                  ogc.Function_Name( nArgs: "2", "equalTo" )
                  ogc.Function_Name( nArgs: "1", "exp" )
                  ogc.Function_Name( nArgs: "1", "exteriorRing" )
                  ogc.Function_Name( nArgs: "1", "floor" )
                  ogc.Function_Name( nArgs: "1", "geometryType" )
                  ogc.Function_Name( nArgs: "1", "geomFromWKT" )
                  ogc.Function_Name( nArgs: "1", "geomLength" )
                  ogc.Function_Name( nArgs: "2", "getGeometryN" )
                  ogc.Function_Name( nArgs: "1", "getX" )
                  ogc.Function_Name( nArgs: "1", "getY" )
                  ogc.Function_Name( nArgs: "1", "getz" )
                  ogc.Function_Name( nArgs: "2", "greaterEqualThan" )
                  ogc.Function_Name( nArgs: "2", "greaterThan" )
                  ogc.Function_Name( nArgs: "0", "id" )
                  ogc.Function_Name( nArgs: "2", "IEEEremainder" )
                  ogc.Function_Name( nArgs: "3", "if_then_else" )
                  ogc.Function_Name( nArgs: "11", "in10" )
                  ogc.Function_Name( nArgs: "3", "in2" )
                  ogc.Function_Name( nArgs: "4", "in3" )
                  ogc.Function_Name( nArgs: "5", "in4" )
                  ogc.Function_Name( nArgs: "6", "in5" )
                  ogc.Function_Name( nArgs: "7", "in6" )
                  ogc.Function_Name( nArgs: "8", "in7" )
                  ogc.Function_Name( nArgs: "9", "in8" )
                  ogc.Function_Name( nArgs: "10", "in9" )
                  ogc.Function_Name( nArgs: "1", "int2bbool" )
                  ogc.Function_Name( nArgs: "1", "int2ddouble" )
                  ogc.Function_Name( nArgs: "1", "interiorPoint" )
                  ogc.Function_Name( nArgs: "2", "interiorRingN" )
                  ogc.Function_Name( nArgs: "3", "Interpolate" )
                  ogc.Function_Name( nArgs: "2", "intersection" )
                  ogc.Function_Name( nArgs: "2", "intersects" )
                  ogc.Function_Name( nArgs: "1", "isClosed" )
                  ogc.Function_Name( nArgs: "1", "isEmpty" )
                  ogc.Function_Name( nArgs: "2", "isLike" )
                  ogc.Function_Name( nArgs: "1", "isNull" )
                  ogc.Function_Name( nArgs: "2", "isometric" )
                  ogc.Function_Name( nArgs: "1", "isRing" )
                  ogc.Function_Name( nArgs: "1", "isSimple" )
                  ogc.Function_Name( nArgs: "1", "isValid" )
                  ogc.Function_Name( nArgs: "3", "isWithinDistance" )
                  ogc.Function_Name( nArgs: "2", "Jenks" )
                  ogc.Function_Name( nArgs: "1", "length" )
                  ogc.Function_Name( nArgs: "2", "lessEqualThan" )
                  ogc.Function_Name( nArgs: "2", "lessThan" )
                  ogc.Function_Name( nArgs: "1", "log" )
                  ogc.Function_Name( nArgs: "2", "max" )
                  ogc.Function_Name( nArgs: "2", "max_2" )
                  ogc.Function_Name( nArgs: "2", "max_3" )
                  ogc.Function_Name( nArgs: "2", "max_4" )
                  ogc.Function_Name( nArgs: "2", "min" )
                  ogc.Function_Name( nArgs: "2", "min_2" )
                  ogc.Function_Name( nArgs: "2", "min_3" )
                  ogc.Function_Name( nArgs: "2", "min_4" )
                  ogc.Function_Name( nArgs: "1", "mincircle" )
                  ogc.Function_Name( nArgs: "1", "minimumdiameter" )
                  ogc.Function_Name( nArgs: "1", "minrectangle" )
                  ogc.Function_Name( nArgs: "2", "modulo" )
                  ogc.Function_Name( nArgs: "1", "not" )
                  ogc.Function_Name( nArgs: "2", "notEqualTo" )
                  ogc.Function_Name( nArgs: "2", "numberFormat" )
                  ogc.Function_Name( nArgs: "5", "numberFormat2" )
                  ogc.Function_Name( nArgs: "1", "numGeometries" )
                  ogc.Function_Name( nArgs: "1", "numInteriorRing" )
                  ogc.Function_Name( nArgs: "1", "numPoints" )
                  ogc.Function_Name( nArgs: "1", "octagonalenvelope" )
                  ogc.Function_Name( nArgs: "3", "offset" )
                  ogc.Function_Name( nArgs: "2", "overlaps" )
                  ogc.Function_Name( nArgs: "1", "parseBoolean" )
                  ogc.Function_Name( nArgs: "1", "parseDouble" )
                  ogc.Function_Name( nArgs: "1", "parseInt" )
                  ogc.Function_Name( nArgs: "1", "parseLong" )
                  ogc.Function_Name( nArgs: "0", "pi" )
                  ogc.Function_Name( nArgs: "2", "pointN" )
                  ogc.Function_Name( nArgs: "2", "pow" )
                  ogc.Function_Name( nArgs: "1", "property" )
                  ogc.Function_Name( nArgs: "1", "PropertyExists" )
                  ogc.Function_Name( nArgs: "2", "Quantile" )
                  ogc.Function_Name( nArgs: "0", "random" )
                  ogc.Function_Name( nArgs: "5", "Recode" )
                  ogc.Function_Name( nArgs: "2", "relate" )
                  ogc.Function_Name( nArgs: "3", "relatePattern" )
                  ogc.Function_Name( nArgs: "1", "rint" )
                  ogc.Function_Name( nArgs: "1", "round" )
                  ogc.Function_Name( nArgs: "1", "round_2" )
                  ogc.Function_Name( nArgs: "1", "roundDouble" )
                  ogc.Function_Name( nArgs: "2", "setCRS" )
                  ogc.Function_Name( nArgs: "1", "sin" )
                  ogc.Function_Name( nArgs: "1", "sqrt" )
                  ogc.Function_Name( nArgs: "2", "StandardDeviation" )
                  ogc.Function_Name( nArgs: "1", "startAngle" )
                  ogc.Function_Name( nArgs: "1", "startPoint" )
                  ogc.Function_Name( nArgs: "1", "strCapitalize" )
                  ogc.Function_Name( nArgs: "2", "strConcat" )
                  ogc.Function_Name( nArgs: "2", "strEndsWith" )
                  ogc.Function_Name( nArgs: "2", "strEqualsIgnoreCase" )
                  ogc.Function_Name( nArgs: "2", "strIndexOf" )
                  ogc.Function_Name( nArgs: "2", "strLastIndexOf" )
                  ogc.Function_Name( nArgs: "1", "strLength" )
                  ogc.Function_Name( nArgs: "2", "strMatches" )
                  ogc.Function_Name( nArgs: "3", "strPosition" )
                  ogc.Function_Name( nArgs: "4", "strReplace" )
                  ogc.Function_Name( nArgs: "2", "strStartsWith" )
                  ogc.Function_Name( nArgs: "3", "strSubstring" )
                  ogc.Function_Name( nArgs: "2", "strSubstringStart" )
                  ogc.Function_Name( nArgs: "1", "strToLowerCase" )
                  ogc.Function_Name( nArgs: "1", "strToUpperCase" )
                  ogc.Function_Name( nArgs: "1", "strTrim" )
                  ogc.Function_Name( nArgs: "3", "strTrim2" )
                  ogc.Function_Name( nArgs: "2", "symDifference" )
                  ogc.Function_Name( nArgs: "1", "tan" )
                  ogc.Function_Name( nArgs: "1", "toDegrees" )
                  ogc.Function_Name( nArgs: "1", "toRadians" )
                  ogc.Function_Name( nArgs: "2", "touches" )
                  ogc.Function_Name( nArgs: "1", "toWKT" )
                  ogc.Function_Name( nArgs: "2", "union" )
                  ogc.Function_Name( nArgs: "2", "UniqueInterval" )
                  ogc.Function_Name( nArgs: "1", "vertices" )
                  ogc.Function_Name( nArgs: "2", "within" )
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
    def results, contentType

    //println wfsRequest

    def workspace = getWorkspace()
    def layer = workspace[wfsRequest?.typeName]

    def y = {
      mkp.xmlDeclaration()
      mkp.declareNamespace( gml: "http://www.opengis.net/gml" )
      mkp.declareNamespace( omar: "http://omar.ossim.org" )
      mkp.declareNamespace( xsd: "http://www.w3.org/2001/XMLSchema" )

      xsd.schema(
          elementFormDefault: "qualified",
          targetNamespace: "http://omar.ossim.org"
      ) {
        xsd.'import'( namespace: "http://www.opengis.net/gml",
            schemaLocation: "http://schemas.opengis.net/gml/2.1.2/feature.xsd" )

        xsd.complexType( name: "${ layer.name }Type" ) {
          xsd.complexContent {
            xsd.extension( base: "gml:AbstractFeatureType" ) {
              xsd.sequence {
                xsd.element( maxOccurs: "1", minOccurs: "1", name: "id", nillable: "false", type: "xsd:long" )
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
        xsd.element( name: layer.name, substitutionGroup: "gml:_Feature", type: "omar:${ layer.name }Type" )
      }
    }

    workspace.close()

    def z = new StreamingMarkupBuilder( encoding: 'UTF-8' ).bind( y )

    results = z?.toString()
    contentType = 'application/xml'

    return [results, contentType]

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
      case "CSV":
        results = outputCSV( wfsRequest )
        contentType = 'text/csv'
        break
      case "JSON":
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

  private String outputGML(def wfsRequest)
  {
    def results
    def describeFeatureTypeURL = grailsLinkGenerator.link( base: grailsApplication.config.omar.serverURL, absolute: true,
        controller: 'wfs', params: [service: 'WFS', version: '1.0.0', request: 'DescribeFeatureType',
        typeName: "${ wfsRequest.typeName }"] )

    def filterParams = [
        filter: wfsRequest?.filter ?: Filter.PASS,
        max: wfsRequest.maxFeatures ?: -1,
        offset: wfsRequest?.offset ?: -1
    ]
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
      def layer = workspace[wfsRequest?.typeName]
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
        def layer = workspace[wfsRequest?.typeName]

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
            def omarId = featureId?.split( '\\.' )[-1] as long

            //println feature

            gml.featureMember {
              omar."${ wfsRequest?.typeName }"( fid: featureId ) {

                omar.id( omarId )

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

  private def outputCSV(def wfsRequest)
  {

  }

  private def outputJSON(def wfsRequest)
  {
    def results
    def workspace = getWorkspace()
    def layer = workspace[wfsRequest?.typeName]
    def filter
      def filterParams = [
              filter: wfsRequest?.filter ?: Filter.PASS,
              max: wfsRequest.maxFeatures ?: -1,
              offset: wfsRequest?.offset ?: -1
      ]
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
          results = "${[numberOfFeatures:count,timestamp:timestamp] as JSON}"
      }
      else
      {
          def writer = new GeoJSONWriter()
          def cursor = layer.getCursor(filterParams);

          def newLayer = new Layer(cursor.col)

          results = writer.write( newLayer )
          cursor?.close()
      }
      workspace?.close()
      return results
  }


  private def getWorkspace(def flag = true)
  {
    def workspace = null

    if ( flag )
    {
      def jdbcParams = grailsApplication.config.dataSource

      def dbParams = [
          dbtype: "postgis",           //must be postgis
          user: jdbcParams.username,   //the user to connect with
          passwd: jdbcParams.password, //the password of the user.
          schema: "public"
      ]


      def pattern1 = "jdbc:(.*)://(.*):(.*)/(.*)"
      def pattern2 = "jdbc:(.*)://(.*)/(.*)"
      def pattern3 = "jdbc:(.*):(.*)"


      switch ( jdbcParams.url )
      {
      case ~pattern1:
        def matcher = ( jdbcParams.url ) =~ pattern1
        dbParams['host'] = matcher[0][2]
        dbParams['port'] = matcher[0][3]
        dbParams['database'] = matcher[0][4]
        break
      case ~pattern2:
        def matcher = ( jdbcParams.url ) =~ pattern2
        dbParams['host'] = matcher[0][2]
        dbParams['port'] = "5432"
        dbParams['database'] = matcher[0][3]
        break
      case ~pattern3:
        def matcher = ( jdbcParams.url ) =~ pattern3
        dbParams['host'] = "localhost"
        dbParams['port'] = "5432"
        dbParams['database'] = matcher[0][2]
        break
      }

      workspace = new PostGIS(
          dbParams['database'],
          dbParams['host'],
          dbParams['port'],
          dbParams['schema'],
          dbParams['user'],
          dbParams['passwd']
      )
    }
    else
    {
      def getDbParams = [( PostgisNGDataStoreFactory.DATASOURCE.key ): dataSource]
      def dataStore = new PostgisNGDataStoreFactory().createDataStore( getDbParams )

      workspace = new Database( dataStore )
    }

    return workspace
  }

}
