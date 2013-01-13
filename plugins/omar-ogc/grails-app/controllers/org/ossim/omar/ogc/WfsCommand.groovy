package org.ossim.omar.ogc

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
  String sort
  @Override
  String toString()
  {
    return ToStringBuilder.reflectionToString( this )
  }

  def toQuery()
  {
    def wfsParams = ( this.metaClass.properties.name - ['class', 'metaClass'] ).inject( [:] ) { a, b ->
      if ( this[b] )
      {
        a[b] = this[b]
      }
      return a
    }

    def query = wfsParams?.collect { k, v -> "${ k }=${ URLEncoder.encode( v as String, 'UTF-8' ) }" }.join( '&' )

    //println query

    return query
  }

}
