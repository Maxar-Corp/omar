package org.ossim.omar.ogc.wfs

import org.apache.commons.collections.map.CaseInsensitiveMap

/**
 * Created with IntelliJ IDEA.
 * User: sbortman
 * Date: 2/25/13
 * Time: 9:19 AM
 * To change this template use File | Settings | File Templates.
 */
class KmlQueryResultFormat implements ResultFormat
{
  def name = "KMLQUERY"
  def contentType = 'application/vnd.google-earth.kml+xml'

  def grailsApplication
  def grailsLinkGenerator

  def getFeature(def wfsRequest, def workspace)
  {
    def caseInsensitiveParams = new CaseInsensitiveMap();
    wfsRequest.properties.each { caseInsensitiveParams.put( it.key.toLowerCase(), it.value ) }
    def filter = caseInsensitiveParams.filter ?: ""
    //def bbox = caseInsensitiveParams.bbox

    /*
    if (!filter.contains("BBOX("))
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
    else
    {
        def bboxString = filter.find("BBOX\\(.*\\)");
        if (bboxString)
        {
            def splitBbox = bboxString.split(",");
            if (splitBbox.size() == 5)
            {
                def stripEnding = splitBbox[4].trim()
                stripEnding = stripEnding.substring(0, stripEnding.indexOf(')')-1)
                bbox = [minx: splitBbox[1].toDouble(),
                        miny: splitBbox[2].toDouble(),
                        maxx: splitBbox[3].toDouble(),
                        maxy: stripEnding.toDouble()
                     ]
            }
        }
    }
    */
    //println "___________________________${filter}"
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
    // caseInsensitiveParams.remove("filter");
    caseInsensitiveParams.remove( "class" );
    filter = filter.encodeAsURL()

    caseInsensitiveParams.outputFormat = "kml"
    // caseInsensitiveParams.each{k,v->
    //     caseInsensitiveParams."${k}" = v.encodeAsURL()
    // }
    caseInsensitiveParams.remove( "bbox" );
    def kmlQueryUrl = grailsLinkGenerator.link( absolute: true, base: "${grailsApplication.config.omar.serverURL}",
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
        "<viewRefreshMode>onRequest</viewRefreshMode>"
    kmlwriter << "</Link></NetworkLink></kml>"
    String kmlText = kmlwriter.buffer

    return [kmlText, contentType]
  }
}
