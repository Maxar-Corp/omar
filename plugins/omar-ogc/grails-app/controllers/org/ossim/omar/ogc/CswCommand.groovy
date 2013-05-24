package org.ossim.omar.ogc

import grails.validation.Validateable
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import groovy.util.slurpersupport.GPathResult
import groovy.xml.StreamingMarkupBuilder

/**
 * Created with IntelliJ IDEA.
 * User: sbortman
 * Date: 5/23/13
 * Time: 2:31 PM
 * To change this template use File | Settings | File Templates.
 */
@Validateable
@ToString( includeNames = true, includeFields = true, excludes = 'errors,dateCreated,lastUpdated,metaClass' )
@EqualsAndHashCode

class CswCommand
{
  String service
  String version
  String request

  static CswCommand fromXML(String xmlText)
  {
    fromXML( new XmlSlurper().parseText( xmlText ) )
  }

  static CswCommand fromXML(GPathResult xml)
  {
    def params = [
        service: xml.@service?.text(),
        version: xml.@version?.text(),
        request: xml?.name(),
//        maxFeatures: xml.@maxFeatures.text()?.toInteger() ?: 1000
    ]

    switch ( params.request )
    {
    case "GetCapabilities":
      params.version = xml?.AcceptVersions?.Version?.text()
      break

    case "DescribeRecord":
      break
/*
    case 'DescribeFeatureType':
      params.typeName = xml.TypeName.text()
      break
    case 'GetFeature':
      params.with {
        typeName = xml.Query.collect { it.@typeName.text() }?.first()
        filter = xml.Query.collect { new StreamingMarkupBuilder().bindNode( it.Filter ).toString().trim() }?.first()
      }
      break
*/
    }

    new CswCommand( params )
  }
}
