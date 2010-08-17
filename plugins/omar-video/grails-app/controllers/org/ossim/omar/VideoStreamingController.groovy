package org.ossim.omar

import org.springframework.beans.factory.InitializingBean

class VideoStreamingController implements InitializingBean
{
  def grailsApplication

  def flashDirRoot
  def flashUrlRoot
  def kmlService

  def show = {
    def flvUrl
    def title

    def videoDataSet = VideoDataSet.findByVideoId(params.id)?:VideoDataSet.get(params.id)

    if ( videoDataSet )
    {
      VideoFile mainFile = videoDataSet.mainFile
      File videoFile = mainFile.name as File
      File flvFile = null

      switch ( mainFile.format?.toUpperCase() )
      {
        case "MPEG":
          flvFile = "${flashDirRoot}/${videoFile.name}.flv" as File

          if ( !flvFile.exists() )
          {
            def cmd = "ffmpeg -i ${videoFile.absolutePath} -an -vb 2048k -r 15 ${flvFile.absolutePath}"
            println cmd
            def process = cmd.execute()
            process.waitFor()
          }

          flvUrl = new URL("${flashUrlRoot}/${flvFile.name}")

          break

        case "FLV":
        case "SWF":
          //def flvContext = videoFile.absolutePath - videoDataSet.repository.baseDir
          def flvContext = videoFile.absolutePath - flashDirRoot

          flvUrl = new URL("${flashUrlRoot}/${flvContext}")
          break
      }

      title = videoFile.name
    }
    else
    {
      flash.message = "VideoDataSet not found with id ${params.id}"
      redirect(controller: "videoDataSet", action: "list", params: [flash: flash])
    }

    [flvUrl: flvUrl, videoDataSet: videoDataSet, title: title]
  }

  def getKML = {

    def videoDataSet = VideoDataSet.findByVideoId(params.id)?:VideoDataSet.get(params.id)
    def videoDataSetList = [videoDataSet]
    
    File mpegFile = videoDataSet.mainFile.name as File
    def kml = kmlService.createVideosKml(videoDataSetList, params)
    response.setHeader("Content-disposition", "attachment; filename=${mpegFile}.kml")
    render(contentType: "application/vnd.google-earth.kml+xml", text: "${kml}", encoding: "UTF-8")

/*
    def videoDataSet = org.ossim.omar.VideoDataSet.get(params.id)


    if ( videoDataSet )
    {
      def groundGeom = videoDataSet.groundGeom.toString()
      def list = []
      def point
      def polygons = []
      def kmlPolygon

      File mpegFile = videoDataSet.mainFile.name as File
      File flvFile = "${flashDirRoot}/${mpegFile.name}.flv" as File
      URL flvUrl = new URL("${flashUrlRoot}/${flvFile.name}")
      def flashPlayerUrl = resource(dir: "js", file: "player.swf", absolute: true)


      def descriptionText = "<![CDATA[<embed type='application/x-shockwave-flash' src='${flashPlayerUrl}' width='${videoDataSet.width}' height='${videoDataSet.height}' flashvars='file=${flvUrl}'></embed>]]>"

      // The code below translate the asText(ground_geom) from PostGIS to a KML polygon.  We will later
      // iterate over the polygon list to create the KML MultiGeometry

      def polygonMatch = /\({2,3}(.*?)\){2}/

      groundGeom.eachMatch(polygonMatch) {matcher ->
        matcher[1]?.toString().split(",").each {
          list << "${it.replaceFirst(' ', ",")},0"
        }
        kmlPolygon = list.join(" ")
        polygons << kmlPolygon
        point = list[0]
      }

      def kmlBuilder = new StreamingMarkupBuilder()

      kmlBuilder.encoding = "UTF-8"

      def kmlNode = {
        mkp.xmlDeclaration()
        kml("xmlns": "http://earth.google.com/kml/2.1") {
          Document() {
            Style("id": "sh_red") {
              LineStyle() {
                color("ff0000ff")
              }
              PolyStyle {
                color("7f00005f")
              }
              IconStyle() {
                color("ff00007f")
              }
              Icon() {
                href("http://maps.google.com/mapfiles/kml/pushpin/red-pushpin.png")
                hotspot("x": 20, "y": 2, "xunits": "pixels", "yunits": "pixels")
              }
            }
            StyleMap("id": "red") {
              Pair() {
                key("normal")
                styleUrl("#sh_red")
              }
              Pair() {
                key("highlight")
                styleUrl("#sh_red")
              }
            }
            Placemark() {
              styleUrl("#red")
              name(mpegFile.name)
              description {
                mkp.yieldUnescaped(descriptionText)
              }
              MultiGeometry() {
                polygons.each {polygon ->
                  Polygon() {
                    tessellate("1")
                    outerBoundryIs() {
                      LinearRing() {
                        coordinates("${polygon}")
                      }
                    }
                  }
                }
              }
              Point() {
                coordinates("${point}")
              }

            }
          }
        }
      }

      def kmlWriter = new StringWriter()

      kmlWriter << kmlBuilder.bind(kmlNode)
      response.setHeader("Content-disposition", "attachment; filename=foo.kml")
      render(contentType: "application/vnd.google-earth.kml+xml", text: kmlWriter.buffer, encoding: "UTF-8")
    }
    else
    {
      response.setHeader("Content-disposition", "attachment; filename=foo.kml")
      render(contentType: "application/vnd.google-earth.kml+xml", text: "", encoding: "UTF-8")
    }
*/
    
  }

  public void afterPropertiesSet()
  {
    flashDirRoot = grailsApplication.config.videoStreaming.flashDirRoot
    flashUrlRoot = grailsApplication.config.videoStreaming.flashUrlRoot
  }
}
