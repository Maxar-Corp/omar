package org.ossim.omar.ogc

import grails.validation.Validateable
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import groovy.util.slurpersupport.GPathResult
import groovy.xml.StreamingMarkupBuilder

/**
 * Created with IntelliJ IDEA.
 * User: sbortman
 * Date: 11/2/12
 * Time: 10:36 PM
 * To change this template use File | Settings | File Templates.
 */
@Validateable
@ToString( includeNames = true, includeFields = true, excludes = 'errors,dateCreated,lastUpdated,metaClass' )
@EqualsAndHashCode

class WfsCommand
{
  String service
  String version
  String request
  String typeName
  String filter

  String outputFormat
  Integer maxFeatures
  Integer offset
  String resultType
  String sortBy

  /**
   * Google earth is not replacing their variables as described by the
   * SPEC so I can't add a replacement variable in the filter to adhere
   * to the WFS SPEC.  We have to support floating BBOX for now when doing
   * KML Network Links
   */
  String bbox

  def toQuery()
  {
    /*
    def wfsParams = ( this.metaClass.properties.name - ['class', 'metaClass'] ).inject( [:] ) { a, b ->
      if ( this[b] )
      {
        a[b] = this[b]
      }
      return a
    }

    def query = wfsParams?.collect { k, v -> "${ k }=${ URLEncoder.encode( v as String, 'UTF-8' ) }" }?.join( '&' )

    //println query

    return query
    */
    this.properties.grep { !( it.key in ['class', 'errors'] ) && it.value != null }.collect {
      "${it.key}=${URLEncoder.encode( it.value as String, 'UTF-8' )}"
    }.join( '&' )
  }

  def convertSortByToArray()
  {
    def result = [];


    if ( !sortBy )
    {
      return null
    };
    def arrayOfValues = sortBy.split( "," )
    def idx = 0;
    arrayOfValues.each { element ->
      def splitParam = element.split( "\\+" );
      if ( splitParam.length == 1 )
      {
        result << [splitParam]
      }
      else
      {
        if ( splitParam[1].toLowerCase() == "a" )
        {
          result << [splitParam[0], "ASC"]
        }
        else
        {
          result << [splitParam[0], "DESC"]
        }
      }
    }
    result;
  }

  static WfsCommand fromXML(String xmlText)
  {
    fromXML( new XmlSlurper().parseText( xmlText ) )
  }

  static WfsCommand fromXML(GPathResult xml)
  {
    def params = [
        service: xml.@service.text(),
        version: xml.@version.text(),
        request: xml.name(),
        maxFeatures: xml.@maxFeatures.text()?.toInteger() ?: 1000
    ]

    switch ( params.request )
    {
    case 'DescribeFeatureType':
      params.typeName = xml.TypeName.text()
      break
    case 'GetFeature':
      params.with {
        typeName = xml.Query.collect { it.@typeName.text() }?.first()
        filter = xml.Query.collect { new StreamingMarkupBuilder().bindNode( it.Filter ).toString().trim() }?.first()
      }
      break
    }

    new WfsCommand( params )
  }

  String toXML()
  {
    def x = {
      mkp.declareNamespace( xsi: "http://www.w3.org/2001/XMLSchema-instance" )

      switch ( request )
      {
      case 'GetCapabilities':
        "${request}"( service: service, version: version, xmlns: "http://www.opengis.net/wfs",
            'xsi:schemaLocation': "http://www.opengis.net/wfs  http://schemas.opengis.net/wfs/1.1.0/wfs.xsd" )
        break
      case 'DescribeFeatureType':
        "${request}"( service: service, version: version, xmlns: "http://www.opengis.net/wfs",
            'xsi:schemaLocation': "http://www.opengis.net/wfs  http://schemas.opengis.net/wfs/1.1.0/wfs.xsd" ) {
          TypeName( typeName )
        }
        break
      case 'GetFeature':
        mkp.declareNamespace( ogc: "http://www.opengis.net/ogc" )
        "${request}"( service: service, version: version, xmlns: "http://www.opengis.net/wfs",
            'xsi:schemaLocation': "http://www.opengis.net/wfs  http://schemas.opengis.net/wfs/1.1.0/wfs.xsd" ) {
          Query( typeName: typeName ) {
            mkp.yieldUnescaped filter
          }
        }
        break
      }
    }

    new StreamingMarkupBuilder().bind( x ).toString()
  }
}
