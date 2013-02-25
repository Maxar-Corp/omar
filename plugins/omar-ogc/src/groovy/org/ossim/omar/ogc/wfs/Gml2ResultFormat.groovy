package org.ossim.omar.ogc.wfs

import geoscript.filter.Filter
import groovy.xml.StreamingMarkupBuilder
import org.joda.time.DateTime
import org.joda.time.DateTimeZone

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


  def getFeature(def wfsRequest, def workspace)
  {
    def results
    def describeFeatureTypeURL = grailsLinkGenerator.link( base: grailsApplication.config.omar.serverURL, absolute: true,
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
              omar."${ wfsRequest?.typeName }"( fid: featureId ) {

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
