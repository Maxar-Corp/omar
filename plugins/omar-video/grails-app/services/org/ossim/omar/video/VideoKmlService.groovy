package org.ossim.omar.video

import groovy.xml.StreamingMarkupBuilder
import java.text.SimpleDateFormat
import org.apache.commons.io.FilenameUtils
import org.ossim.omar.ogc.KmlService
import org.ossim.omar.video.VideoDataSet

class VideoKmlService extends KmlService
{
  def flashDirRoot
  def flashUrlRoot
  def grailsApplication
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
            def flashPlayerUrl = tagLibBean.createLinkTo(dir: "js", file: "player.swf", base: "${grailsApplication.config.omar.serverURL}", absolute: true)
            Placemark() {
              styleUrl("#red")
              def flashbasename = "${FilenameUtils.getBaseName(videoDataSet.mainFile?.name)}.flv"
              name(flashbasename)
              def createFlvUrl = tagLibBean.createLink(absolute: true, base: "${grailsApplication.config.omar.serverURL}",controller: "videoStreaming", action: "show", id: videoDataSet.indexId)
              def descriptionText = ""
              def bounds = videoDataSet.groundGeom?.bounds
              def logoUrl = "${grailsApplication.config.omar.serverURL}/images/omarLogo.png"
              def thumbnailUrl = tagLibBean.createLink(absolute: true, base: "${grailsApplication.config.omar.serverURL}", controller: "thumbnail", action: "frame", id: videoDataSet.id, params: [size: 128])

              if ( embed )
              {
                descriptionText = """
                  <table border="1" width="720">
                    <tr><th align="right">Thumbnail:</th>
			<td> <a href='${createFlvUrl}'><img src='${thumbnailUrl}'/></a></td>
		    </tr>
                    <tr><th align="right">Start Time:</th><td>${videoDataSet.startDate}</td></tr>
                    <tr><th align="right">End Time:</th><td align="left">${videoDataSet.endDate}</td></tr>
                    <tr><th align="right">Min Lat:</th><td align="left">${bounds?.minLat}</td></tr>
                    <tr><th align="right">Min Lin:</th><td align="left">${bounds?.minLon}</td></tr>
                    <tr><th align="right">Max Lat:</th><td align="left">${bounds?.maxLat}</td></tr>
                    <tr><th align="right">Max Lon:</th><td align="left">${bounds?.maxLon}</td></tr>
                    <tr><td>
                      <embed type="application/x-shockwave-flash" src="${flashPlayerUrl}"
                        width="720" height="480" flashvars="file=${flvUrl}&autostart=true"</embed>
                    </td></tr>
                    <tfoot>
                      <tr><td colspan="2">
                         <a href='${grailsApplication.config.omar.serverURL}'><img src='${logoUrl}'/></a>
                      </td></tr>
                    </tfoot>
                  </table>
                """
              }
              else
              {
                descriptionText = """
                  <table border="1">
                    <tr> <th align="right">Thumbnail:</th>
			<td><a href='${createFlvUrl}'><img src='${thumbnailUrl}'/></a></td>
                    </tr>
                    <tr><th align="right">Start Time:</th><td align="left">${videoDataSet.startDate}</td></tr>
                    <tr><th align="right">End Time:</th><td align="left">${videoDataSet.endDate}</td></tr>
                    <tr><th align="right">Min Lat:</th><td align="left">${bounds?.minLat}</td></tr>
                    <tr><th align="right">Min Lon: </th><td align="left">${bounds?.minLon}</td></tr>
                    <tr><th align="right">Max Lat:</th><td align="left">${bounds?.maxLat}</td></tr>
                    <tr><th align="right">Max Lon:</th><td align="left">${bounds?.maxLon}</td></tr>
                    <tfoot>
                      <tr><td colspan="2">
                         <a href='${grailsApplication.config.grails.omar.serverURL}'><img src='${logoUrl}'/></a>
                      </td></tr>
                    </tfoot>
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

  String createTopVideosKml(Map params)
  {
    def kmlQueryUrl = tagLibBean.createLink(absolute: true, base: "${grailsApplication.config.omar.serverURL}", controller: "videoKmlQuery", action: "getVideosKml", params: params)
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
    super.afterPropertiesSet()
    flashDirRoot = grailsApplication.config.videoStreaming.flashDirRoot
    flashUrlRoot = grailsApplication.config.videoStreaming.flashUrlRoot
  }
}
