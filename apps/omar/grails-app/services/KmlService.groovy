import groovy.xml.StreamingMarkupBuilder
import java.text.SimpleDateFormat
import org.springframework.context.ApplicationContextAware
import org.springframework.context.ApplicationContext
import org.springframework.beans.factory.InitializingBean

class KmlService implements ApplicationContextAware, InitializingBean
{

  boolean transactional = false
  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
  ApplicationContext applicationContext
  def grailsApplication
  def tagLibBean
  def flashDirRoot
  def flashUrlRoot

  String createKml(List<RasterEntry> rasterEntries)
  {
    def kmlbuilder = new StreamingMarkupBuilder()
    def width = 1024;
    def height = 1024;

    kmlbuilder.encoding = "UTF-8"
    def kmlnode = {
      mkp.xmlDeclaration()
      kml("xmlns": "http://earth.google.com/kml/2.1") {
        Folder() {
          name("OMAR_WMS")
          rasterEntries?.each {rasterEntry ->
            def acquisition = (rasterEntry?.acquisitionDate) ? sdf.format(rasterEntry?.acquisitionDate) : null

            def groundCenterLon = (rasterEntry?.groundGeom?.bounds?.minLon + rasterEntry?.groundGeom?.bounds?.maxLon) * 0.5;
            def groundCenterLat = (rasterEntry?.groundGeom?.bounds?.minLat + rasterEntry?.groundGeom?.bounds?.maxLat) * 0.5;

            Folder() {
              name((rasterEntry.mainFile.name as File).name)
              Placemark() {
                name((rasterEntry.mainFile.name as File).name)

                Point() {
                  coordinates("${groundCenterLon},${groundCenterLat},0")
                }
              }
              GroundOverlay() {
                open("1")
                visibility("1")
                Icon() {
                  def wmsURL = tagLibBean.createLink(absolute: true, controller: "ogc", action: "wms", params: [
                      version: "1.1.1",
                      REQUEST: "GetMap",
                      layers: rasterEntry?.id,
                      SRS: "EPSG:4326",
                      WIDTH: "${width}",
                      HEIGHT: "${height}",
                      TRANSPARENT: "TRUE",
                      FORMAT: "image/png"
                  ])
                  href(wmsURL)
                  viewRefreshMode("onStop")
                  viewRefreshTime("2")
                  viewBoundScale("0.85")
                }
                LatLonBox() {
                  north(rasterEntry?.groundGeom?.bounds?.maxLat)
                  south(rasterEntry?.groundGeom?.bounds?.minLon)
                  east(rasterEntry?.groundGeom?.bounds?.maxLat)
                  west(rasterEntry?.groundGeom?.bounds?.minLat)
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
    }
    def kmlwriter = new StringWriter()

    kmlwriter << kmlbuilder.bind(kmlnode)

    String kmlText = kmlwriter.buffer
    return kmlText
  }

  String createImagesKml(List<RasterEntry> rasterEntries, Map wmsParams, Map params)
  {
    def kmlbuilder = new StreamingMarkupBuilder()
    def width = 1024;
    def height = 1024;

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
    def bounds = wmsParams?.bbox?.split(',')
    wmsParams?.srs = "EPSG:4326"
    wmsParams?.remove("bbox");
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
          Acquistion_Date: rasterEntry.acquisitionDate,
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
        Document() {
//          name("Omar WMS")
          rasterIdx = 0
//          Style(id:"checkHideChildrenExample"){
//              ListStyle(){
//                listItemType("checkHideChildren")
 //             }
  //        }
           rasterEntries?.each {rasterEntry ->
            def acquisition = (rasterEntry?.acquisitionDate) ? sdf.format(rasterEntry?.acquisitionDate) : null

            def groundCenterLon = (rasterEntry?.groundGeom?.bounds?.minLon + rasterEntry?.groundGeom?.bounds?.maxLon) * 0.5;
            def groundCenterLat = (rasterEntry?.groundGeom?.bounds?.minLat + rasterEntry?.groundGeom?.bounds?.maxLat) * 0.5;
            wmsParams?.layers = rasterEntry?.id

            def renderedHtml = "${descriptionMap.get(rasterIdx)}"
            rasterIdx++

              GroundOverlay() {
                name((rasterEntry.mainFile.name as File).name)
                Snippet(maxLines:"0", "")
                description(renderedHtml)

                LookAt(){
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
                  viewRefreshTime("2")
                  viewBoundScale("0.85")
                }

                LatLonBox() {
                  north(bounds[2])
                  south(bounds[1])
                  east(bounds[0])
                  west(bounds[3])
                }
                if ( acquisition )
                {
                  TimeStamp() {
                    when(acquisition)
                  }
                }
            }
          }
//          styleUrl("#checkHideChildrenExample")
        }
      }
    }
    def kmlwriter = new StringWriter()

    kmlwriter << kmlbuilder.bind(kmlnode)

    String kmlText = kmlwriter.buffer

    return kmlText
  }

  String createVideosKml(List<VideoDataSet> videoEntries)
  {
    def kmlbuilder = new StreamingMarkupBuilder()

    kmlbuilder.encoding = "UTF-8"

    def kmlnode = {
      mkp.xmlDeclaration()
      kml("xmlns": "http://earth.google.com/kml/2.1") {
        Document() {
          videoEntries?.each {videoDataSet ->
            def startDate = (videoDataSet?.startDate) ? sdf.format(videoDataSet?.startDate) : null
            def endDate = (videoDataSet?.endDate) ? sdf.format(videoDataSet?.endDate) : null

            def bounds = videoDataSet?.groundGeom?.bounds

            def groundCenterLon = (bounds?.minLon + bounds?.maxLon) * 0.5;
            def groundCenterLat = (bounds?.minLat + bounds?.maxLat) * 0.5;
            File mpegFile = videoDataSet.mainFile.name as File
            File flvFile = "${flashDirRoot}/${mpegFile.name}.flv" as File
            URL flvUrl = new URL("${flashUrlRoot}/${flvFile.name}")
            def flashPlayerUrl = tagLibBean.createLinkTo(dir: "js", file: "player.swf", absolute: true)


            def descriptionText = "<embed type='application/x-shockwave-flash' src='${flashPlayerUrl}' width='${videoDataSet.width}' height='${videoDataSet.height}' flashvars='file=${flvUrl}'></embed>"

            Placemark() {
              name((videoDataSet.mainFile.name as File).name)
              Snippet(maxLines:"0", "")
              description(descriptionText)
              Point() {
                coordinates("${groundCenterLon},${groundCenterLat},0")
              }
              if ( startDate && endDate )
              {
                TimeSpan() {
                  begin(startDate)
                  end(endDate)
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
  
  String createTopImagesKml(Map params)
  {
    def kmlQueryUrl = tagLibBean.createLink(absolute: true, controller: "kmlQuery", action: "getImagesKml", params: params)
    def kmlbuilder = new StreamingMarkupBuilder()

    kmlbuilder.encoding = "UTF-8"
    def kmlnode = {
      mkp.xmlDeclaration()
      kml("xmlns", "http://earth.google.com/kml/2.1"){
        NetworkLink() {
           name ("OMAR Last ${params.max} Images For View")
           Link (){
             href(kmlQueryUrl)
             viewRefreshMode ("onRequest")
           }
         }
        
      }
     }
    def kmlwriter = new StringWriter()

    kmlwriter << kmlbuilder.bind(kmlnode)
    String kmlText = kmlwriter.buffer
    return kmlText
  }
/*
  String createTopImagesKml()
  {
    def kmlQueryUrl = tagLibBean.createLink(absolute: true, controller: "kmlQuery", action: "getImagesKml")
    def kmlbuilder = new StreamingMarkupBuilder()

    kmlbuilder.encoding = "UTF-8"
    def kmlnode = {
      mkp.xmlDeclaration()
      kml("xmlns": "http://earth.google.com/kml/2.1") {
        NetworkLink() {
          name("Top Images")
          Link() {
            href(kmlQueryUrl)
            viewRefreshMode("onStop")
            viewRefreshTime("2")
          }
        }
      }
    }
    def kmlwriter = new StringWriter()

    kmlwriter << kmlbuilder.bind(kmlnode)

    String kmlText = kmlwriter.buffer
    return kmlText
  }
*/
  String createTopVideosKml(Map params)
  {
    def kmlQueryUrl = tagLibBean.createLink(absolute: true, controller: "kmlQuery", action: "getVideosKml", params: params)
    def kmlbuilder = new StreamingMarkupBuilder()

    kmlbuilder.encoding = "UTF-8"
    def kmlnode = {
      mkp.xmlDeclaration()
      kml("xmlns" , "http://earth.google.com/kml/2.1") {
       NetworkLink() {
        name("OMAR Last ${params.max} Videos For View")
        Link () {
          href(kmlQueryUrl)
          viewRefreshMode ("onRequest")
        }
       }
      }
    }
    return kmlbuilder.bind(kmlnode).toString()
  }


/*
  String createTopVideosKml()
  {
    def kmlQueryUrl = tagLibBean.createLink(absolute: true, controller: "kmlQuery", action: "getVideosKml")
    def kmlbuilder = new StreamingMarkupBuilder()

    kmlbuilder.encoding = "UTF-8"
    def kmlnode = {
      mkp.xmlDeclaration()
      kml("xmlns": "http://earth.google.com/kml/2.1") {
        NetworkLink() {
          name("Top Videos")
          Link() {
            href(kmlQueryUrl)
            viewRefreshMode("onStop")
            viewRefreshTime("2")
          }
        }
      }
    }
    return kmlbuilder.bind(kmlnode).toString()
  }
*/  
  String buildUrl(String url, Map params)
  {
    def String result;
    def list =[]
      params.each{k,v->
        list << "$k=$v"
      }
    list=list.join("&")
    if(url.indexOf("?") == -1)
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
    def url = buildUrl(grailsApplication.config.wms.data.raster.url,
            [VERSION: "1.1.1",
                    REQUEST: "GetMap",
                    LAYERS: "${grailsApplication.config.wms.data.raster.footprintLayers}",
                    SRS: "EPSG:4326",
                    WIDTH: "1024",
                    HEIGHT: "512",
                    TRANSPARENT: "TRUE",
                    IMAGEFILTER: "acquisition_date>=(date(now())-integer'${params.imagedays}')",
                    FORMAT: "image/png"])

    def kmlbuilder = new StreamingMarkupBuilder()
    def kmlnode ={
      mkp.xmlDeclaration()
      kml("xmlns", "http://earth.google.com/kml/2.1") {
        GroundOverlay() {
          name ("OMAR Last ${params.imagedays} Days Imagery Coverage")
          open ("1")
          visibility("1")
          Icon () {
            href (url)
            viewRefreshMode("onStop")
            viewRefreshTime("${grailsApplication.config.kml.viewRefreshTime}")
            viewBoundScale("1.0")
          }
          LatLonBox () {
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
  /*
  String createImageFootprint()
  {
    def url = buildUrl(grailsApplication.config.wms.data.raster.url,
                           [VERSION: "1.1.1",
                            REQUEST: "GetMap",
                            LAYERS: "${grailsApplication.config.wms.data.raster.footprintLayers}",
                            SRS: "EPSG:4326",
                            WIDTH: "1024",
                            HEIGHT: "1024",
                            TRANSPARENT: "TRUE",
                            FORMAT: "image/png"])
    def kmlbuilder = new StreamingMarkupBuilder()
    def kmlnode = {
      mkp.xmlDeclaration()
       kml("xmlns": "http://earth.google.com/kml/2.1") {
           GroundOverlay() {
             name("${grailsApplication.config.wms.data.raster.title}")
            open("1")
            visibility("1")
            Icon() {
              href(url)
              viewRefreshMode("onStop")
              viewRefreshTime("2")
              viewBoundScale("1.0")
            }
             LatLonBox(){
               north(90)
               south(-90)
               east(-180)
               west(180)
             }
          }
       }
     }
    return kmlbuilder.bind(kmlnode).toString()
  }
  */
  String createVideoFootprint(Map params)
  {
    def url = buildUrl(grailsApplication.config.wms.data.video.url,
       [VERSION: "1.1.1",
        REQUEST: "GetMap",
        LAYERS: "${grailsApplication.config.wms.data.video.footprintLayers}",
        SRS: "EPSG:4326",
        WIDTH: "1024",
        HEIGHT: "512",
        TRANSPARENT: "TRUE",
        VIDEOFILTER: "start_date>=(date(now())-integer'${params.videodays}')",
        FORMAT: "image/png"])

    def kmlbuilder = new StreamingMarkupBuilder()
    def kmlnode = {
      mkp.xmlDeclaration()
      kml("xmlns": "http://earth.google.com/kml/2.1") {
        GroundOverlay() {
          name("OMAR Last ${params.imagedays} Days Video Coverage")
          open("1")
          visibility("1")
          Icon() {
            href(url)
            viewRefreshMode ("onStop")
            viewRefreshTime ("${grailsApplication.config.kml.viewRefreshTime}")
            viewBoundScale ("1.0")
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
/*
  String createVideoFootprint()
  {
    def url = buildUrl(grailsApplication.config.wms.data.video.url,
                           [VERSION: "1.1.1",
                            REQUEST: "GetMap",
                            LAYERS: "${grailsApplication.config.wms.data.video.footprintLayers}",
                            SRS: "EPSG:4326",
                            WIDTH: "1024",
                            HEIGHT: "1024",
                            TRANSPARENT: "TRUE",
                            FORMAT: "image/png"])
    def kmlbuilder = new StreamingMarkupBuilder()
    def kmlnode = {
      mkp.xmlDeclaration()
       kml("xmlns": "http://earth.google.com/kml/2.1") {
           GroundOverlay() {
             name("${grailsApplication.config.wms.data.video.title}")
            open("1")
            visibility("1")
            Icon() {
              href(url)
              viewRefreshMode("onStop")
              viewRefreshTime("2")
              viewBoundScale("1.0")
            }
             LatLonBox(){
               north(90)
               south(-90)
               east(-180)
               west(180)
             }
          }
       }
     }
    return kmlbuilder.bind(kmlnode).toString()
  }
  */
  public void afterPropertiesSet()
  {
    tagLibBean = applicationContext.getBean("org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib")
    flashDirRoot = grailsApplication.config.videoStreaming.flashDirRoot
    flashUrlRoot = grailsApplication.config.videoStreaming.flashUrlRoot
  }
}
