package org.ossim.omar

import groovy.xml.StreamingMarkupBuilder
import java.text.SimpleDateFormat
import org.springframework.context.ApplicationContextAware
import org.springframework.context.ApplicationContext
import org.springframework.beans.factory.InitializingBean

import org.ossim.omar.RasterEntry
import org.ossim.omar.VideoDataSet

class KmlService implements ApplicationContextAware, InitializingBean
{

  boolean transactional = false
  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
  ApplicationContext applicationContext
  def grailsApplication
  def tagLibBean
  def flashDirRoot
  def flashUrlRoot

  String createKml(List<RasterEntry> rasterEntries, Map wmsParams)
  {
    def kmlbuilder = new StreamingMarkupBuilder()
    def rasterIdx = 0
    def descriptionMap = [:]
    rasterEntries?.each {rasterEntry ->
      def mpp = rasterEntry.getMetersPerPixel()
      def fieldMap = [File: (rasterEntry.mainFile.name as File).name,
              Entry_Id: rasterEntry.entryId,
              Width: rasterEntry.width,
              Height: rasterEntry.height,
              Bands: rasterEntry.numberOfBands,
              Acquistion_Date: rasterEntry.acquisitionDate,
              Meters_per_pixel: mpp]
      def imageUrl = tagLibBean.createLink(absolute: true, controller: "mapView", params: [rasterEntryIds: rasterEntry.imageId])

      def descriptionBuilder = new StreamingMarkupBuilder().bind {
        body() {
          table() {
            tr() {
              td("Image link:")
              td() {
                p() {
                  font(size: 5)
                  a(href: imageUrl, "Browse image")
                }
              }
            }
            fieldMap.each {k, v ->
              tr() {
                td("${k}:")
                td(v)
              }
            }
          }
        }
      }
      descriptionMap.put(rasterIdx, descriptionBuilder.toString())
    }
    if ( !wmsParams?.width )
    {
      wmsParams?.width = "1024"
    }
    if ( !wmsParams?.height )
    {
      wmsParams?.height = "1024"
    }
    wmsParams.remove("action")
    wmsParams.remove("controller")
    wmsParams?.version = "1.1.1"
    wmsParams?.request = "GetMap"
    wmsParams?.srs = "EPSG:4326"
    wmsParams?.transparent = "TRUE"
    wmsParams?.format = "image/png"
    kmlbuilder.encoding = "UTF-8"
    def bbox = wmsParams?.bbox;
    wmsParams?.remove("bbox");
    def kmlnode = {
      mkp.xmlDeclaration()
      kml("xmlns": "http://earth.google.com/kml/2.1") {
        Folder() {
          name("OMAR_WMS")
          rasterEntries?.each {rasterEntry ->
            wmsParams?.layers = "${rasterEntry?.imageId}"
            def acquisition = (rasterEntry?.acquisitionDate) ? sdf.format(rasterEntry?.acquisitionDate) : null

            def groundCenterLon = (rasterEntry?.groundGeom?.bounds?.minLon + rasterEntry?.groundGeom?.bounds?.maxLon) * 0.5;
            def groundCenterLat = (rasterEntry?.groundGeom?.bounds?.minLat + rasterEntry?.groundGeom?.bounds?.maxLat) * 0.5;

            def renderedHtml = "${descriptionMap.get(rasterIdx)}"
            rasterIdx++

            GroundOverlay() {
              name((rasterEntry.mainFile.name as File).name)
              Snippet(maxLines: "0", "")
              description(renderedHtml)
              LookAt() {
                longitude(groundCenterLon)
                latitude(groundCenterLat)
                altitude(0.0)
                heading(0.0)
                tilt(0.0)
                range(15000)
                altitudeMode("clampToGround")
              }
              open("1")
              visibility("1")
              Icon() {
                def wmsURL = tagLibBean.createLink(absolute: true, controller: "ogc", action: "wms", params: wmsParams)
//                   println wmsURL
                href(wmsURL)
                viewRefreshMode("onStop")
                viewRefreshTime("1")
                viewBoundScale("0.85")
              }

              LatLonBox() {
                if ( bbox )
                {
                  north(bbox[3])
                  south(bbox[1])
                  east(bbox[2])
                  west(bbox[0])
                }
                else
                {
                  def bounds = rasterEntry?.groundGeom?.bounds
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
        }
      }
    }

    def kmlwriter = new StringWriter()

    kmlwriter << kmlbuilder.bind(kmlnode)

    String kmlText = kmlwriter.buffer
    return kmlText
  }

  String createImagesKml(List<RasterEntry> rasterEntries, Map wmsParams, Map params)
  {
    def kmlbuilder = new StreamingMarkupBuilder()
//    def width = 1024;
//    def height = 1024;

    kmlbuilder.encoding = "UTF-8"

    wmsParams?.request = "GetMap"
    if ( !params?.containsKey("version") )
    {
      wmsParams.version = "1.1.1"
    }
    if ( !params?.containsKey("width") )
    {
      wmsParams.width = "1024"
    }
    if ( !params?.containsKey("height") )
    {
      wmsParams.height = "1024"
    }
    if ( !params?.containsKey("format") )
    {
      wmsParams.format = "image/png"
    }
    if ( !params?.containsKey("transparent") )
    {
      wmsParams.transparent = "TRUE"
    }
    wmsParams?.srs = "EPSG:4326"
    def bbox = wmsParams?.bbox;
    wmsParams?.remove("bbox");
    //  wmsParams?.remove("width");
    //  wmsParams?.remove("height");
    wmsParams.remove("action")
    wmsParams.remove("controller")
    def rasterIdx = 0
    def descriptionMap = [:]
    rasterEntries?.each {rasterEntry ->
      def mpp = rasterEntry.getMetersPerPixel()
      def fieldMap = [File: (rasterEntry.mainFile.name as File).name,
              Entry_Id: rasterEntry.entryId,
              Width: rasterEntry.width,
              Height: rasterEntry.height,
              Bands: rasterEntry.numberOfBands,
              Acquistion_Date: rasterEntry?.acquisitionDate,
              Meters_per_pixel: mpp]
      def imageUrl = tagLibBean.createLink(absolute: true, controller: "mapView", params: [rasterEntryIds: rasterEntry.id])

      def descriptionBuilder = new StreamingMarkupBuilder().bind {
        body() {
          table() {
            tr() {
              td("Image link:")
              td() {
                p() {
                  font(size: 5)
                  a(href: imageUrl, "Browse image")
                }
              }
            }
            fieldMap.each {k, v ->
              tr() {
                td("${k}:")
                td(v)
              }
            }
          }
        }
      }
      descriptionMap.put(rasterIdx, descriptionBuilder.toString())
      rasterIdx++;
    }
    def kmlnode = {
      mkp.xmlDeclaration()
      kml("xmlns": "http://earth.google.com/kml/2.1") {
        Folder() {
          name("Omar WMS")
          rasterIdx = 0
          rasterEntries?.each {rasterEntry ->
            def acquisition = (rasterEntry?.acquisitionDate) ? sdf.format(rasterEntry?.acquisitionDate) : null

            def groundCenterLon = (rasterEntry?.groundGeom?.bounds?.minLon + rasterEntry?.groundGeom?.bounds?.maxLon) * 0.5;
            def groundCenterLat = (rasterEntry?.groundGeom?.bounds?.minLat + rasterEntry?.groundGeom?.bounds?.maxLat) * 0.5;
            wmsParams?.layers = rasterEntry?.imageId

            def renderedHtml = "${descriptionMap.get(rasterIdx)}"
            rasterIdx++

            GroundOverlay() {
              name((rasterEntry.mainFile.name as File).name)
              Snippet(maxLines: "0", "")
              description(renderedHtml)
              LookAt() {
                longitude(groundCenterLon)
                latitude(groundCenterLat)
                altitude(0.0)
                heading(0.0)
                tilt(0.0)
                range(15000)
                altitudeMode("clampToGround")
              }
              open("1")
              visibility("1")
              Icon() {
                def wmsURL = tagLibBean.createLink(absolute: true, controller: "ogc", action: "wms", params: wmsParams)

                href(wmsURL)
                viewRefreshMode("onStop")
                viewRefreshTime("1")
                viewBoundScale("0.85")
//                viewFormat("BBOX=[bboxWest],[bboxSouth],[bboxEast],[bboxNorth]&amp;width=[horizPixels]&amp;height=[vertPixels]")
              }
              LatLonBox() {
                if ( bbox )
                {
                  north(bbox[3])
                  south(bbox[1])
                  east(bbox[2])
                  west(bbox[0])
                }
                else
                {
                  def bounds = rasterEntry?.groundGeom?.bounds
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
        }
      }
    }
    def kmlwriter = new StringWriter()

    kmlwriter << kmlbuilder.bind(kmlnode)

    String kmlText = kmlwriter.buffer
    return kmlText
  }

  String createVideosKml(List<VideoDataSet> videoEntries, Map params)
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
            def filename = videoDataSet.mainFile?.name
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
              def flashbasename = filename.split("/")[-1] + ".flv"
              name(flashbasename)
              def createFlvUrl = tagLibBean.createLink(absolute: true, controller: "videoStreaming", action: "show", id: videoDataSet.id)
              if ( embed )
              {
                description("<table width=\"720\"><tr><td><a href='${createFlvUrl}'>CLICK TO PLAY</a></td></tr><tr><td></td></tr><tr><td><b>START TIME:</b> ${videoDataSet.startDate}</td></tr><tr><td><b>END TIME:</b> ${videoDataSet.endDate}</td></tr><tr><td><b>MIN LAT:</b> ${videoDataSet.groundGeom?.bounds?.minLat}</td></tr><tr><td><b>MIN LON: </b> ${videoDataSet.groundGeom?.bounds?.minLon}</td></tr><tr><td><b>MAX LAT:</b> ${videoDataSet.groundGeom?.bounds?.maxLat}</td></tr><tr><td><b>MAX LON:</b> ${videoDataSet.groundGeom?.bounds?.maxLon}</td></tr><tr><td><embed type=\"application/x-shockwave-flash\" src=\"${flashPlayerUrl}\" width=\"720\" height=\"480\" flashvars=\"file=${flvUrl}&autostart=true\"</embed><td><tr></table>")
              }
              else
              {
                description("<table><tr><td><a href='${createFlvUrl}'>CLICK TO PLAY</a></td></tr><tr><td></td></tr><tr><td><b>START TIME:</b> ${videoDataSet.startDate}</td></tr><tr><td><b>END TIME:</b> ${videoDataSet.endDate}</td></tr><tr><td><b>MIN LAT:</b> ${videoDataSet.groundGeom?.bounds?.minLat}</td></tr><tr><td><b>MIN LON: </b> ${videoDataSet.groundGeom?.bounds?.minLon}</td></tr><tr><td><b>MAX LAT:</b> ${videoDataSet.groundGeom?.bounds?.maxLat}</td></tr><tr><td><b>MAX LON:</b> ${videoDataSet.groundGeom?.bounds?.maxLon}</td></tr></table>")
              }
              Snippet("<a href='${createFlvUrl}'>CLICK TO PLAY</a>")
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
            href(kmlQueryUrl)
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
            href(kmlQueryUrl)
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
                    LAYERS: "${grailsApplication.config.wms.data.raster.footprintLayers}",
                    STYLES: "${grailsApplication.config.wms.data.raster.styles}",
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
                    LAYERS: "${grailsApplication.config.wms.data.video.footprintLayers}",
                    STYLES: "${grailsApplication.config.wms.data.video.styles}",
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
