package org.ossim.omar.ogc

import groovy.xml.StreamingMarkupBuilder
import org.apache.commons.lang.builder.ToStringBuilder

/**
 * Created with IntelliJ IDEA.
 * User: sbortman
 * Date: 11/2/12
 * Time: 10:36 PM
 * To change this template use File | Settings | File Templates.
 */
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

  @Override
  String toString()
  {
    return ToStringBuilder.reflectionToString( this )
  }

  String toQuery()
  {
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

  static WfsCommand fromXml(def xml)
  {
    def wfsCommand = new WfsCommand()

    wfsCommand.service = xml.@service
    wfsCommand.version = xml.@version
    wfsCommand.request = xml.name()

    if ( wfsCommand.request?.toUpperCase() == "DESCRIBEFEATURETYPE" )
    {
      wfsCommand.typeName = xml.TypeName.text()
    }
    else if ( wfsCommand.request?.toUpperCase() == "GETFEATURE" )
    {
      def foo = new StreamingMarkupBuilder().bindNode( xml ).toString()
      def x = new XmlSlurper().parseText( foo )

      def max = x.@maxFeatures.text()
      def start = x.@offset?.text()

      wfsCommand.with {
        service = x.@service?.text() ?: "WFS"
        version = x.@version?.text() ?: "1.0.0"
        request = x.name() ?: request ?: "GetFeature"
        typeName = x.Query.collect { it.@typeName.text() }?.first()
        filter = x.Query.collect { new StreamingMarkupBuilder().bindNode( it.Filter ).toString().trim() }?.first()

        maxFeatures = ( max ) ? max.toInteger() : 1000
        offset = ( start ) ? start.toInteger() : 0
        outputFormat = x.@outputFormat?.text() ?: "GML2"
      }

//          wfsCommand.service = "WFS"
//          wfsCommand.version = "1.0.0"
//          wfsCommand.request = "GetFeature"
//          wfsCommand.typeName = "raster_entry"
//          wfsCommand.filter = new Filter("file_type='ccf'").xml
//          wfsCommand.maxFeatures = 10
//          wfsCommand.offset = 0
    }

    return wfsCommand
  }

  String toXML()
  {
    def cmd = {
      mkp.xmlDeclaration()
      mkp.declareNamespace( '': "http://www.opengis.net/wfs" )
      mkp.declareNamespace( ogc: "http://www.opengis.net/ogc" )

      "${service}"(
          service: 'WFS',
          'xsi:schemaLocation': "http://www.opengis.net/wfs http://schemas.opengis.net/wfs/1.0.0/WFS-capabilities.xsd"
      )
    }
    new StreamingMarkupBuilder( encoding: 'UTF-8' ).bind( cmd ).toString()
  }
}