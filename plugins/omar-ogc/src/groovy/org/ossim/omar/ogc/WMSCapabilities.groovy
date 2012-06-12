package org.ossim.omar.ogc

import groovy.xml.StreamingMarkupBuilder
import java.text.SimpleDateFormat

/**
 * @author sbortman
 *
 */
public class WMSCapabilities
{
  public static final String version = "1.1.1"

  public static final String DOCTYPE = """
    <!DOCTYPE WMT_MS_Capabilities SYSTEM "http://schemas.opengis.net/wms/1.1.1/WMS_MS_Capabilities.dtd"
    [
    <!ELEMENT VendorSpecificCapabilities EMPTY>
    ]>
	"""

  def sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")

  def getCapabilitiesFormats = [
          "application/vnd.ogc.wms_xml"
  ]

  def getMapFormats = [
          "image/png",
          //"image/gif",
          "image/jpeg",
          //"image/tiff",
          //"image/nitf",
          //"image/jp2",
          //"image/vnd.wap.wbmp",
          //"image/svg+xml"
  ]

  def getFeatureInfoFormats = [
          "text/csv",
          "shp"
 //         "application/vnd.ogc.gml"
  ]

  def describeLayerFormats = [
          "text/xml"
  ]

  def getLegendGraphicFormats = [
          "image/png",
          "image/gif",
          "image/jpeg",
          //"image/vnd.wap.wbmp"
  ]

  def getStylesFormats = [
          "text/xml"
  ]

  def exceptionFormats = [
          "application/vnd.ogc.se_xml",
          "application/vnd.ogc.se_inimage",
          "application/vnd.ogc.se_blank"
  ]

  def map

  public WMSCapabilities(def layers, def serviceAddress)
  {

    //println "${layers} ${serviceAddress}"

    map = new MapObject(
            name: "raster_entry",
            title: "Imagery from OMAR",
            srs: "EPSG:4326",
            minX: -180,
            minY: -90,
            maxX: 180,
            maxY: 90,
            getCapabilitiesURL: "${serviceAddress}?layers=${layers?.indexId?.join(',')}&",
            getMapURL: serviceAddress
    )

    layers?.each {rasterEntry ->
      if ( rasterEntry )
      {
        def entryId = rasterEntry.entryId
        def srs = "EPSG:4326"//rasterEntry?.groundGeom?.srs
//        def bounds = rasterEntry?.groundGeom?.bounds
        def bounds = rasterEntry?.groundGeom?.bounds
        def file = rasterEntry?.mainFile
//        def acquisition = (rasterEntry?.acquisitionDate) ? sdf.format(rasterEntry?.acquisitionDate) : ""
        def acquisition = (rasterEntry?.acquisitionDate) ? sdf.format(rasterEntry?.acquisitionDate) : ""

        def filename = file.name
        def indexId = rasterEntry.indexId

        // If there is an indexId,  display that as title for KML
//        if ( indexId && indexId.value )
//        {
//          filename = indexId.value
//        }

        map.layers << new LayerObject(
                name: rasterEntry.indexId,
                title: rasterEntry.title,
                description: "The absolute path to this file is ${file.name} and its entry id is ${entryId}",
                srs: srs,
                minX: bounds.minLon,
                minY: bounds.minLat,
                maxX: bounds.maxLon,
                maxY: bounds.maxLat,
                filename: filename,
                acquisition: acquisition
        )
      }
    }
  }

  def getCapabilities()
  {
    def builder = new StreamingMarkupBuilder()

    builder.encoding = "UTF-8"

    def capabilities = {
      mkp.xmlDeclaration()
      //mkp.yieldUnescaped(DOCTYPE)

      WMT_MS_Capabilities(version: version) {
        Service() {
          Name("WMS")
          Title()
          Abstract()
          KeywordList() {
            map?.keywords?.each {keyword ->
              Keyword(keyword)
            }
          }
          OnlineResource(
                  "xmlns:xlink": "http://www.w3.org/1999/xlink",
                  "xlink:href": "${map.getMapURL}"
          )
          ContactInformation() {
            ContactPersonPrimary() {
              ContactPerson()
              ContactOrganization()
            }
            ContactAddress() {
              AddressType()
              Address()
              City()
              StateOrProvince()
              PostCode()
              Country()
            }
          }
          Fees()
          AccessConstraints()
        }
        Capability() {
          Request() {
            GetCapabilities() {
              getCapabilitiesFormats.each {format ->
                Format(format)
              }
              DCPType() {
                HTTP() {
                  Get() {
                    OnlineResource(
                            "xmlns:xlink": "http://www.w3.org/1999/xlink",
                            "xlink:href": map.getCapabilitiesURL //.replace("&", "&amp;")
                    )
                  }
                  Post {
                    OnlineResource(
                            "xmlns:xlink": "http://www.w3.org/1999/xlink",
                            "xlink:href": map.getCapabilitiesURL //.replace("&", "&amp;")
                    )
                  }
                }
              }
            }
            GetMap() {
              getMapFormats.each {format ->
                Format(format)
              }
              DCPType() {
                HTTP() {
                  Get() {
                    OnlineResource(
                            "xmlns:xlink": "http://www.w3.org/1999/xlink",
                            "xlink:href": map.getMapURL//.replace("&", "&amp;")
                    )
                  }
                  Post {
                    OnlineResource(
                            "xmlns:xlink": "http://www.w3.org/1999/xlink",
                            "xlink:href": map.getMapURL //.replace("&", "&amp;")
                    )
                  }
                }
              }
            }
            GetFeatureInfo() {
              getFeatureInfoFormats.each {format ->
                Format(format)
              }

              DCPType() {
                HTTP() {
                  Get() {
                    OnlineResource(
                            "xmlns:xlink": "http://www.w3.org/1999/xlink",
                            "xlink:href": "${map.getMapURL}"
                    )
                  }
                  Post {
                    OnlineResource(
                            "xmlns:xlink": "http://www.w3.org/1999/xlink",
                            "xlink:href": "${map.getMapURL}"
                    )
                  }
                }
              }
            }
            DescribeLayer() {
              describeLayerFormats.each {format ->
                Format(format)
              }
              DCPType() {
                HTTP() {
                  Get() {
                    OnlineResource(
                            "xmlns:xlink": "http://www.w3.org/1999/xlink",
                            "xlink:href": "${map.getMapURL}"
                    )
                  }
                  Post {
                    OnlineResource(
                            "xmlns:xlink": "http://www.w3.org/1999/xlink",
                            "xlink:href": "${map.getMapURL}"
                    )
                  }
                }
              }
            }
            GetLegendGraphic() {
              getLegendGraphicFormats.each {format ->
                Format(format)
              }
              DCPType() {
                HTTP() {
                  Get() {
                    OnlineResource(
                            "xmlns:xlink": "http://www.w3.org/1999/xlink",
                            "xlink:href": "${map.getMapURL}"
                    )
                  }
                  Post {
                    OnlineResource(
                            "xmlns:xlink": "http://www.w3.org/1999/xlink",
                            "xlink:href": "${map.getMapURL}"
                    )
                  }
                }
              }
            }
            GetStyles() {
              getStylesFormats.each {format ->
                Format(format)
              }
              DCPType() {
                HTTP() {
                  Get() {
                    OnlineResource(
                            "xmlns:xlink": "http://www.w3.org/1999/xlink",
                            "xlink:href": "${map.getMapURL}"
                    )
                  }
                  Post {
                    OnlineResource(
                            "xmlns:xlink": "http://www.w3.org/1999/xlink",
                            "xlink:href": "${map.getMapURL}"
                    )
                  }
                }
              }
            }
          }
          Exception() {
            exceptionFormats.each {format ->
              Format(format)
            }
          }
            VendorSpecificCapabilities(){
                stretch_mode(required:"0"){
                    Title("Histogram Stretch Type")
                    Abstract("""Histogram stretch types can be one of the following:
                                linear_auto_min_max,
                                linear_1std_from_mean,
                                linear_2std_from_mean,
                                linear_3std_from_mean""")
                }
                stretch_mode_region(required:"0"){
                    Title("Defines the region to use for stretching")
                    Abstract("""The values can be 'global' or 'viewport'.  Viewport says calculate a
                                histogram for the requesting BBOX.  If the stretch is 'global' then
                                use the global histogram for all BBOX requests for that image
                                being chipped.""")
                }
                sharpen_mode(required:"0"){
                    Title("Sharpen an image")
                    Abstract("""The image is sharpened based on the mode.  The mode can be
                             light or heavy
                             """)
                }
                sharpen_width(required:"0"){
                    Title("Defines the width of the sharpen kernel used to sharpen the image.")
                    Abstract("")
                }
                sharpen_sigma(required:"0"){
                    Title("This is a parameter used to calculate the weights for the kernel")
                    Abstract("")
                }
                rotate(required:"0"){
                    Title("Image rotation")
                    Abstract("")
                }
                quicklook(required:"0"){
                    Title("Specifies whether to use the full model to reproject the image or just a quick corner calculation.")
                    Abstract("")
                }
                null_flip(required:"0"){
                    Title("Flips null pixels to a valid pixel value.")
                    Abstract("""This will take the images null pixel value, typically 0, and flip it to a valid
                                pixel value, typically 1""")
                }
                bands(required:"0"){
                    Title("A comma separated list of band indices for the output product")
                    Abstract("""This is a comma separated list of band numbers
                                used to specify the output product.  Example: bands=2,1,0 defines
                                a 3 band output product where the first 3 bands are reversed""")
                }
                brightness(required:"0"){
                    Title("Parameter used to brighten the image")
                    Abstract("This is a normalized parameter between -1 and 1.")
                }
                contrast(required:"0"){
                    Title("This is a multiplier for te image pixel.")
                    Abstract("")
                }
                interpolation(required:"0"){
                    Title("Interpolation type to use")
                    Abstract("""This is the interpolation type to use when resampling an image.
                                The types supported can be: nearest neighbor, bilinear, gaussian, cubic,
                                hanning, hamming, lanczos, mitchell, catrom, blackman, sinc,
                                quadratic, hermite, bspline""")
                }
            }
          UserDefinedSymbolization()
          Layer(queryable:"1") {
            Name(map?.name)
            Title(map?.title)
            SRS(map?.srs)
            LatLonBoundingBox(minx: "${map?.minX}", miny: "${map?.minY}", maxx: "${map?.maxX}", maxy: "${map?.maxY}")
            BoundingBox(SRS: "${map?.srs}", minx: "${map?.minX}", miny: "${map?.minY}", maxx: "${map?.maxX}", maxy: "${map?.maxY}")
            map?.layers?.each {layer ->
              Layer(queryable:"1") {
                Name(layer?.name)
                Title(layer?.title)
                Abstract(layer?.description)
                SRS(layer?.srs)
                LatLonBoundingBox(minx: "${layer?.minX}", miny: "${layer?.minY}", maxx: "${layer?.maxX}", maxy: "${layer?.maxY}")
                BoundingBox(SRS: "${layer?.srs}", minx: "${layer?.minX}", miny: "${layer?.minY}", maxx: "${layer?.maxX}", maxy: "${layer?.maxY}")
              }
            }
          }
        }
      }
    }

    def writer = new StringWriter()

    writer << builder.bind(capabilities)

    return writer.buffer
  }



  def getKML()
  {
    def kmlbuilder = new StreamingMarkupBuilder()
    kmlbuilder.encoding = "UTF-8"

    def kmlnode = {
      mkp.xmlDeclaration()
      kml("xmlns": "http://earth.google.com/kml/2.1") {
        Folder() {
          name("OMAR_WMS")
          map?.layers?.each {layer ->
            GroundOverlay() {
              name(layer?.filename)
              open("1")
              visibility("1")
              Icon() {
                href("${map.getMapURL}?version=${version}&REQUEST=GetMap&layers=${layer?.name}&SRS=${layer?.srs}&TRANSPARENT=TRUE&FORMAT=image/png&")
                viewRefreshMode("onStop")
                viewRefreshTime("2")
                viewBoundScale("0.85")
                viewFormat("""BBOX=[bboxWest],[bboxSouth],[bboxEast],[bboxNorth]&width=[horizPixels]&height=[vertPixels]""")
              }
              LatLonBox() {
                north(layer?.maxY)
                south(layer?.minY)
                east(layer?.maxX)
                west(layer?.minX)
              }
              if ( layer?.acquisition )
              {
                TimeStamp() {
                  when(layer?.acquisition)
                }
              }
            }
          }
        }
      }
    }
    def kmlwriter = new StringWriter()

    kmlwriter << kmlbuilder.bind(kmlnode)

    //return kmlwriter.buffer.toString().replace("&amp;", "&")
    return kmlwriter.buffer.toString()
  }

  /**
   * @param args
   */
  public static void main(def args)
  {

    def layers = new File("/data/bmng").listFiles().collect {
      it.absolutePath
    }


    println layers

    WMSCapabilities wms = new WMSCapabilities(layers, "http://localhost/ServiceTest/ogc/wms")



    def writer = new File("test.xml")
    def xml = wms.getCapabilities()

    //println xml

    writer.write(xml as String)
  }

}
