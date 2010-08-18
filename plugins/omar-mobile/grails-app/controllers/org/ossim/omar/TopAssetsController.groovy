package org.ossim.omar
import groovy.xml.MarkupBuilder

class TopAssetsController {

  def index = { }

  def topImages = {
    def writer = new StringWriter()
    def builder = new MarkupBuilder(writer)

    def rasterEntries = RasterEntry.createCriteria().list {
      projections {
        property("id")
        property("acquisitionDate")
        property("groundGeom")
      }

      maxResults(25)
      order("acquisitionDate", "desc")
      isNotNull("acquisitionDate")
    }

    builder.list {
      rasterEntries?.each { result ->
        entry {
          image(result[0])

          acquisition(result[1])

          def bounds = result[2].bounds
          def centerLat = (((bounds.maxLat - bounds.minLat) / 2) + bounds.minLat)
          def centerLon = (((bounds.maxLon - bounds.minLon) / 2) + bounds.minLon)
          center(centerLat + ' ' + centerLon)
          topLeft(bounds.minLon + ' ' + bounds.maxLat)
          bottomLeft(bounds.minLon + ' ' + bounds.minLat)
          topRight(bounds.maxLon + ' ' + bounds.maxLat)
          bottomRight(bounds.maxLon + ' ' + bounds.minLat)
        }
      }
    }

    render(contentType:"text/plain",text:writer.toString());
  }

  def topVideos = {
    def writer = new StringWriter()
    def builder = new MarkupBuilder(writer)

    def videoEntries = VideoDataSet.createCriteria().list {
      projections {
        property("id")
        property("groundGeom")
      }

      maxResults(25)
    }

    builder.list {
      videoEntries?.each { result ->
        entry {
          video(result[0])

          def bounds = result[1].bounds
          def centerLat = (((bounds.maxLat - bounds.minLat) / 2) + bounds.minLat)
          def centerLon = (((bounds.maxLon - bounds.minLon) / 2) + bounds.minLon)
          center(centerLat + ' ' + centerLon)
        }
      }
    }

    render(contentType:"text/plain",text:writer.toString());
  }

}