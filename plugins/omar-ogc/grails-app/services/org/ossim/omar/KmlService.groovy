package org.ossim.omar

import groovy.xml.StreamingMarkupBuilder
import java.text.SimpleDateFormat
import org.springframework.context.ApplicationContextAware
import org.springframework.context.ApplicationContext
import org.springframework.beans.factory.InitializingBean


import org.apache.commons.io.FilenameUtils

class KmlService implements ApplicationContextAware, InitializingBean
{

  boolean transactional = false
  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
  ApplicationContext applicationContext
  def grailsApplication
  def tagLibBean
  def flashDirRoot
  def flashUrlRoot
  def coordinateConversionService
  String createName(RasterEntry rasterEntry)
  {
      rasterEntry.title?:rasterEntry.filename
  }
  String createImageKmlDescription(RasterEntry rasterEntry)
  {
    def description = ""
    def imageUrl = tagLibBean.createLink(absolute: true, controller: "mapView",
            params: [layers: rasterEntry.indexId])

    def mpp = rasterEntry.getMetersPerPixel()
    def fieldMap = [File: "<a href=${imageUrl}>${(rasterEntry.mainFile.name as File).name}</a>",
            'Entry id': rasterEntry.entryId?:"",
            'Image Id': rasterEntry.imageId?:"",
            'Title': rasterEntry.title?:"",
            'Niirs': rasterEntry.niirs?:"",
            'Width': rasterEntry.width?:"",
            'Height': rasterEntry.height?:"",
            'Bands': rasterEntry.numberOfBands?:"",
            'Acquistion Date': rasterEntry?.acquisitionDate?:"",
            'Meters Per Pixel': mpp?:""]


    description = "<hr/><table>"
    fieldMap.each {k, v ->
      description += "<tr>"
      description += "<td>${k}</td>"
      description += "<td>${v}</td></tr>"
    }
    description += "</table>"

    description
  }

  String createImagesKml(List rasterEntries, Map wmsParams, Map params)
  {
    def kmlbuilder = new StreamingMarkupBuilder()

    kmlbuilder.encoding = "UTF-8"

    wmsParams?.request = "GetMap"
    if ( !params?.containsKey("version") )
    {
      wmsParams.version = "1.1.1"
    }
//    if ( !params?.containsKey("width") )
//    {
//      wmsParams.width = "1024"
//    }
//    if ( !params?.containsKey("height") )
//    {
//      wmsParams.height = "512"
//    }
    if ( !params?.containsKey("format") )
    {
      wmsParams.format = "image/png"
    }
    if ( !params?.containsKey("transparent") )
    {
      wmsParams.transparent = "TRUE"
    }
    wmsParams?.srs = "EPSG:4326"
    def bbox = wmsParams?.bbox?.split(',')?.collect { it.toDouble() };

    wmsParams?.remove("bbox");
    wmsParams?.remove("width");
    wmsParams?.remove("height");
    wmsParams.remove("action")
    wmsParams.remove("controller")
    def rasterIdx = 0
    def descriptionMap = [:]
    rasterEntries?.each {rasterEntry ->
      descriptionMap.put(rasterIdx, createImageKmlDescription(rasterEntry))
      rasterIdx++;
    }
    def kmlnode = {
      mkp.xmlDeclaration()
      kml("xmlns": "http://earth.google.com/kml/2.1") {
        Document() {
//          Folder() {
          name("Omar WMS")
          rasterIdx = 0
          rasterEntries?.each {rasterEntry ->
            def minLonDMS
            def maxLonDMS
            def minLatDMS
            def maxLatDMS
            def acquisition = (rasterEntry?.acquisitionDate) ? sdf.format(rasterEntry?.acquisitionDate) : null
            def bounds = rasterEntry?.groundGeom?.bounds
            def groundCenterLon = (bounds?.minLon + bounds?.maxLon) * 0.5;
            def groundCenterLat = (bounds?.minLat + bounds?.maxLat) * 0.5;
            wmsParams?.layers = rasterEntry?.indexId

            def renderedHtml = "${descriptionMap.get(rasterIdx)}"
            rasterIdx++
            def mpp = rasterEntry.getMetersPerPixel()
            // calculate a crude metric for putting an image that almost fits within the google viewport
            //
            def defaultRange = mpp * Math.sqrt((rasterEntry.width ** 2) + (rasterEntry.height ** 2));
            if ( defaultRange < 1 )
            {
              defaultRange = 15000
            }
            GroundOverlay() {

              name(createName(rasterEntry))
              Snippet()
              description { mkp.yieldUnescaped("<![CDATA[${renderedHtml}]]>") }
              LookAt() {
                longitude(groundCenterLon)
                latitude(groundCenterLat)
                altitude(0.0)
                heading(0.0)
                tilt(0.0)
                range(defaultRange)
                altitudeMode("clampToGround")
              }
              open("1")
              visibility("1")
              Icon() {
                def wmsURL = tagLibBean.createLink(absolute: true, controller: "ogc", action: "wms", params: wmsParams)

                href { mkp.yieldUnescaped("<![CDATA[${wmsURL}]]>") }
                viewRefreshMode("onStop")
                viewRefreshTime("1")
                viewBoundScale("0.85")
                viewFormat("""BBOX=[bboxWest],[bboxSouth],[bboxEast],[bboxNorth]&width=[horizPixels]&height=[vertPixels]""")
              }
              LatLonBox() {
                if ( bbox )
                {
                  north(Math.max(bbox[3], bbox[1]))
                  south(Math.min(bbox[1], bbox[3]))
                  east(Math.max(bbox[2], bbox[0]))
                  west(Math.min(bbox[0], bbox[2]))
                }
                else
                {
                  north(bounds?.maxLat)
                  south(bounds?.minLat)
                  east(bounds?.maxLon)
                  west(bounds?.minLon)
                }
              }
              if ( acquisition )
              {
                TimeStamp() {
                  when(acquisition)
                }
              }
            }
          }
//          }
        }
      }
    }
    def kmlwriter = new StringWriter()

    kmlwriter << kmlbuilder.bind(kmlnode)

    String kmlText = kmlwriter.buffer
    return kmlText
  }

  String createVideosKml(List<org.ossim.omar.VideoDataSet> videoEntries, Map params)
  {
    Boolean embed = params.embed
    SimpleDateFormat isdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    SimpleDateFormat osdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")

    def kmlbuilder = new StreamingMarkupBuilder()
    kmlbuilder.encoding = "UTF-8"

    def kmlnode = {
      mkp.xmlDeclaration()
      kml("xmlns": "http://earth.google.com/kml/2.1") {
        Document() {
          Style("id": "sh_red") {
            LineStyle() {
              color("ffOOOOff")
            }
            PolyStyle {
              color("7f00005f")
            }
            IconStyle {
              color("ff00007f")
              scale("1.0")
              Icon() {
                href("http://maps.google.com/mapfiles/kml/pushpin/red-pushpin.png")
              }
              hotspot("x": "20", "y": "2", "xunits": "pixels", "yunits": "pixels")
            }
          }
          Style("id": "sn_red") {
            LineStyle() {
              color("ff00007f")
            }
            PolyStyle {
              color("3f00001f")
            }
            IconStyle {
              color("ff00007f")
              scale("1.0")
              Icon() {
                href("http://maps.google.com/mapfiles/kml/pushpin/red-pushpin.png")
              }
              hotspot("x": "20", "y": "2", "xunits": "pixels", "yunits": "pixels")
            }
          }
          StyleMap("id": "red") {
            Pair() {
              key("normal")
              styleUrl("#sn_red")
            }
            Pair() {
              key("highlight")
              styleUrl("#sh_red")
            }
          }

          videoEntries.reverse().each { videoDataSet ->
            def groundGeom = videoDataSet.groundGeom as String
            def list = []
            def point = null
            def polygons = []
            def kmlPoly = ""
            // for now we will use a simple test to see if the current geometry is a polygon
            // or a multi geom and then fix.
            // We  now use Java Topology Suite
            videoDataSet.groundGeom.each() {geom ->
              // for now until we have a utility to get access to all polgons we will assume multi
              // geom and each is a poly
              //
              (0..geom.getNumGeometries() - 1).each() {geomIdx ->
                def poly = geom.getGeometryN(geomIdx) as com.vividsolutions.jts.geom.Polygon
                if ( poly )
                {
                  kmlPoly = ""
                  def ring = poly.getExteriorRing();
                  def coordinates = ring.getCoordinates();
                  if ( coordinates.size() > 0 )
                  {
                    (0..coordinates.size() - 1).each() {coordIdx ->
                      kmlPoly = "${kmlPoly} ${coordinates[coordIdx].x},${coordinates[coordIdx].y}"
                      if ( !point )
                      {
                        point = "${coordinates[coordIdx].x},${coordinates[coordIdx].y}"
                      }
                    }
                  }
                  polygons.add(kmlPoly)
                }
              }
            }

            File mpegFile = videoDataSet.mainFile.name as File
            File flvFile = "${flashDirRoot}/${mpegFile.name}.flv" as File
            URL flvUrl = new URL("${flashUrlRoot}/${flvFile.name}")
            def flashPlayerUrl = tagLibBean.createLinkTo(dir: "js", file: "player.swf", absolute: true)
            Placemark() {
              styleUrl("#red")
              def flashbasename = "${FilenameUtils.getBaseName(videoDataSet.mainFile?.name)}.flv"
              name(flashbasename)
              def createFlvUrl = tagLibBean.createLink(absolute: true, controller: "videoStreaming", action: "show", id: videoDataSet.indexId)
              def descriptionText = ""
              def bounds = videoDataSet.groundGeom?.bounds
              if ( embed )
              {
                descriptionText = """
                  <table width="720">
                    <caption><a href='${createFlvUrl}'>CLICK TO PLAY</a><br/></caption>
                    <tr><th align="right">START TIME:</th><td>${videoDataSet.startDate}</td></tr>
                    <tr><th align="right">END TIME:</th><td align="left">${videoDataSet.endDate}</td></tr>
                    <tr><th align="right">MIN LAT:</th><td align="left">${bounds?.minLat}</td></tr>
                    <tr><th align="right">MIN LON:</th><td align="left">${bounds?.minLon}</td></tr>
                    <tr><th align="right">MAX LAT:</th><td align="left">${bounds?.maxLat}</td></tr>
                    <tr><th align="right">MAX LON:</th><td align="left">${bounds?.maxLon}</td></tr>
                    <tr><td>
                      <embed type="application/x-shockwave-flash" src="${flashPlayerUrl}"
                        width="720" height="480" flashvars="file=${flvUrl}&autostart=true"</embed>
                    <td><tr>
                  </table>
                """
              }
              else
              {
                descriptionText = """
                  <table>
                    <caption><a href='${createFlvUrl}'>CLICK TO PLAY</a><br/></caption>
                    <tr><th align="right">START TIME:</th><td align="left">${videoDataSet.startDate}</td></tr>
                    <tr><th align="right">END TIME:</th><td align="left">${videoDataSet.endDate}</td></tr>
                    <tr><th align="right">MIN LAT:</th><td align="left">${bounds?.minLat}</td></tr>
                    <tr><th align="right">MIN LON: </th><td align="left">${bounds?.minLon}</td></tr>
                    <tr><th align="right">MAX LAT:</th><td align="left">${bounds?.maxLat}</td></tr>
                    <tr><th align="right">MAX LON:</th><td align="left">${bounds?.maxLon}</td></tr>
                  </table>
                """
              }

              description {
                mkp.yieldUnescaped("<![CDATA[${descriptionText}]]>")
              }

              Snippet { mkp.yieldUnescaped("<![CDATA[<a href='${createFlvUrl}'>CLICK TO PLAY</a>]]>") }

              MultiGeometry() {
                polygons.each { polygon ->
                  Polygon() {
                    tessellate("1")
                    altitudeMode("relativeToGround")
                    outerBoundaryIs() {
                      LinearRing() {
                        coordinates("${polygon}")
                      }
                    }
                  }
                }
                Point() {
                  altitudeMode("relativeToGround")
                  coordinates("${point}")
                }
              } // END MultiGeometry()
              if ( videoDataSet?.startDate )
              {
                TimeStamp() {
                  when(osdf.format(new Date(isdf.parse(videoDataSet?.startDate as String) as String)))
                }
              }
            }
          }
        } // END Document
      }// END kml
    }
    return kmlbuilder.bind(kmlnode).toString()
  }

  String createTopImagesKml(Map params)
  {
    def kmlQueryUrl = tagLibBean.createLink(absolute: true, controller: "kmlQuery", action: "getImagesKml", params: params)
    def kmlbuilder = new StreamingMarkupBuilder()

    kmlbuilder.encoding = "UTF-8"

    def kmlnode = {
      mkp.xmlDeclaration()
      kml("xmlns", "http://earth.google.com/kml/2.1") {
        NetworkLink() {
          name("OMAR Last ${params.max} Images For View")
          Link() {
            href {
              mkp.yieldUnescaped("<![CDATA[${kmlQueryUrl}]]>")
            }
            httpQuery("googleClientVersion=[clientVersion];")
            viewRefreshMode("onRequest")
          }
        }
      }
    }

    def kmlwriter = new StringWriter()

    kmlwriter << kmlbuilder.bind(kmlnode)

    String kmlText = kmlwriter.buffer

    return kmlText
  }


  String createTopVideosKml(Map params)
  {
    def kmlQueryUrl = tagLibBean.createLink(absolute: true, controller: "kmlQuery", action: "getVideosKml", params: params)
    def kmlbuilder = new StreamingMarkupBuilder()

    kmlbuilder.encoding = "UTF-8"
    def kmlnode = {
      mkp.xmlDeclaration()
      kml("xmlns", "http://earth.google.com/kml/2.1") {
        NetworkLink() {
          name("OMAR Last ${params.max} Videos For View")
          Link() {
            href {
              mkp.yieldUnescaped("<![CDATA[${kmlQueryUrl}]]>")
            }
            httpQuery("googleClientVersion=[clientVersion]")
            viewRefreshMode("onRequest")
          }
        }
      }
    }
    return kmlbuilder.bind(kmlnode).toString()
  }

  String buildUrl(String url, Map params)
  {
    def String result;
    def list = []
    params.each {k, v ->
      list << "$k=$v"
    }
    list = list.join("&")
    if ( url.indexOf("?") == -1 )
    {
      result = "${url}?"
    }
    else
    {
      result = "${url}&"
    }
    return "${result}${list}"
  }


  String createImageFootprint(Map params)
  {
    def dateFormat = new SimpleDateFormat("yyyyMMdd");
    def date = new Date()
    def url = buildUrl(grailsApplication.config.wms.data.raster.url,
            [VERSION: "1.1.1",
                    REQUEST: "GetMap",
                    LAYERS: "${grailsApplication.config.wms.data.raster.options.footprintLayers}",
                    STYLES: "${grailsApplication.config.wms.data.raster.options.styles}",
                    SRS: "EPSG:4326",
                    WIDTH: "1024",
                    HEIGHT: "512",
                    TRANSPARENT: "TRUE",
                    TIME: "P${params.days}D/${dateFormat.format(date)}",
//            IMAGEFILTER: "acquisition_date>=(date(now())-integer'${params.imagedays}')",
                    FORMAT: "image/png"])

    def kmlbuilder = new StreamingMarkupBuilder()
    def kmlnode = {
      mkp.xmlDeclaration()
      kml("xmlns", "http://earth.google.com/kml/2.1") {
        GroundOverlay() {
          name("OMAR Last ${params.days} Days Imagery Coverage")
          open("1")
          visibility("1")
          Icon() {
            href(url)
            viewRefreshMode("onStop")
            viewRefreshTime("${grailsApplication.config.kml.viewRefreshTime}")
            viewBoundScale("1.0")
          }
          LatLonBox() {
            north(90)
            south(-90)
            east(180)
            west(-180)
          }
        }
      }
    }
    return kmlbuilder.bind(kmlnode).toString()
  }

  String createVideoFootprint(Map params)
  {
    def dateFormat = new SimpleDateFormat("yyyyMMdd");
    def date = new Date()
    def url = buildUrl(grailsApplication.config.wms.data.video.url,
            [VERSION: "1.1.1",
                    REQUEST: "GetMap",
                    LAYERS: "${grailsApplication.config.wms.data.video.options.footprintLayers}",
                    STYLES: "${grailsApplication.config.wms.data.video.options.styles}",
                    SRS: "EPSG:4326",
                    WIDTH: "1024",
                    HEIGHT: "512",
                    TIME: "P${params.days}D/${dateFormat.format(date)}",
                    TRANSPARENT: "TRUE",
//            VIDEOFILTER: "start_date>=(date(now())-integer'${params.videodays}')",
                    FORMAT: "image/png"])

    def kmlbuilder = new StreamingMarkupBuilder()
    def kmlnode = {
      mkp.xmlDeclaration()
      kml("xmlns": "http://earth.google.com/kml/2.1") {
        GroundOverlay() {
          name("OMAR Last ${params.days} Days Video Coverage")
          open("1")
          visibility("1")
          Icon() {
            href(url)
            viewRefreshMode("onStop")
            viewRefreshTime("${grailsApplication.config.kml.viewRefreshTime}")
            viewBoundScale("1.0")

          }
          LatLonBox() {
            north(90)
            south(-90)
            east(180)
            west(-180)
          }
        }
      }
    }
    return kmlbuilder.bind(kmlnode).toString()
  }

  public void afterPropertiesSet()
  {
    tagLibBean = applicationContext.getBean("org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib")
    flashDirRoot = grailsApplication.config.videoStreaming.flashDirRoot
    flashUrlRoot = grailsApplication.config.videoStreaming.flashUrlRoot
  }
}
