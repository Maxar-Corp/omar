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
    String sortBy

    /**
     * Google earth is not replacing their variables as described by the
     * SPEC so I can't add a replacement variable in the filter to adhere
     * to the WFS SPEC.  We have to support floating BBOX for now when doing
     * KML Network Links
     */
    String bbox
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
    def convertSortByToArray()
    {
        def result = [];


        if(!sortBy) return null;
        def arrayOfValues = sortBy.split(",")
        def idx = 0;
        arrayOfValues.each{element->
            def splitParam = element.split("\\+");
            if (splitParam.length == 1)
            {
                result << [splitParam]
            }
            else
            {
                if (splitParam[1].toLowerCase() == "a")
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

}
