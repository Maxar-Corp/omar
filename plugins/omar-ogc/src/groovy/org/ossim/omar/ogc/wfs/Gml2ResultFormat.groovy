package org.ossim.omar.ogc.wfs

import geoscript.feature.Schema
import geoscript.filter.Filter
import geoscript.layer.io.GmlWriter
import geoscript.workspace.Memory
import geoscript.workspace.Workspace
import groovy.xml.StreamingMarkupBuilder
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.ossim.omar.ogc.WfsCommand

/**
 * Created with IntelliJ IDEA.
 * User: sbortman
 * Date: 2/25/13
 * Time: 9:17 AM
 * To change this template use File | Settings | File Templates.
 */
class Gml2ResultFormat implements ResultFormat
{
  def name = "GML2"
  def contentType = 'text/xml; subtype=gml/2.1.2'

  def grailsApplication
  def grailsLinkGenerator

  // Temporary HACK
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

  def getFeatureNEW(def cmd, def inputWorkspace)
  {
    def (workspaceName, layerName) = cmd?.typeName?.split( ':' )
    //def inputWorkspace = getWorkspace( workspaceName )
    def inputLayer = inputWorkspace[layerName]

    def outputWorkspace = new Memory()
    def outputSchema = new Schema( inputLayer.name, inputLayer.schema.fields, inputLayer.schema.uri )
    def outputLayer = outputWorkspace.create( outputSchema )

    def filterParams = [
        filter: cmd?.filter ?: Filter.PASS,
        max: cmd.maxFeatures ?: -1,
        start: cmd?.offset ?: -1
    ]

    if ( cmd.sortBy )
    {
      filterParams.sort = cmd.convertSortByToArray();
    }

    try
    {
      filterParams.filter = new Filter( filterParams.filter )
    }
    catch ( e )
    {
      e.printStackTrace()
    }

    def cursor = inputLayer.getCursor( filterParams )

    while ( cursor.hasNext() )
    {
      def inputFeature = cursor.next()

      outputLayer.add( inputFeature )
    }

    cursor.close()
    inputWorkspace.close()

    def writer = new GmlWriter()
    def xml = writer.write( outputLayer, 2, true, false, true, workspaceName as String )
    def wfs = new XmlSlurper().parseText( xml )

    def describeFeatureTypeURL = grailsLinkGenerator.link(
        params: [
            service: 'WFS',
            version: '1.0.0',
            request: 'DescribeFeatureType',
            typeName: cmd.typeName,
            controller: 'wfs',
            action: 'index'
        ],
        absolute: true
    )

    wfs.@xmlns = "http://www.opengis.net/wfs"
    wfs.@'xsi:schemaLocation' = "${outputLayer.schema.uri} ${ describeFeatureTypeURL } http://www.opengis.net/wfs http://schemas.opengis.net/wfs/1.0.0/WFS-basic.xsd"


    wfs.featureMember."${outputLayer.name}".each {
      it.@fid = "${it.name()}.${it.id.text()}"
    }

    def geomName = outputLayer.schema.geom.name

    wfs.featureMember."${outputLayer.name}"."${geomName}".each {
      it.children()[0].@srsName = 'http://www.opengis.net/gml/srs/epsg.xml#4326'
    }


    def x = {
      mkp.xmlDeclaration()
      mkp.declareNamespace( gml: "http://www.opengis.net/gml" )
      mkp.declareNamespace( wfs: "http://www.opengis.net/wfs" )
      mkp.declareNamespace( xsi: "http://www.w3.org/2001/XMLSchema-instance" )
      mkp.declareNamespace( "${workspaceName}": outputLayer.schema.uri )
      mkp.yield wfs
    }

    outputWorkspace.close()

    def buffer = new StreamingMarkupBuilder( encoding: 'UTF-8' ).bind( x ).toString()

    [buffer, contentType]
  }



  def getFeature(def wfsRequest, def workspace)
  {
    def results
    def describeFeatureTypeURL = grailsLinkGenerator.link( absolute: true,
        controller: 'wfs', params: [service: 'WFS', version: '1.0.0', request: 'DescribeFeatureType',
        typeName: "${ wfsRequest.typeName }"] )

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
      filter = new Filter( filterParams.filter )
    }
    catch ( e )
    {
      e.printStackTrace()
    }
    def y

    if ( wfsRequest.resultType?.toLowerCase() == "hits" )
    {
      def layer = workspace[wfsRequest?.typeName?.split( ':' )[-1]]
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
        def layer = workspace[wfsRequest?.typeName?.split( ':' )[-1]]

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
              "${ wfsRequest?.typeName }"( fid: featureId ) {

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

    return [results, contentType]
  }
}
