package org.ossim.omar

/**
 * Created by IntelliJ IDEA.
 * User: gpotts
 * Date: 2/16/11
 * Time: 12:31 PM
 * To change this template use File | Settings | File Templates.
 */
class WCSRequest
{
    String bbox
    String width
    String height
    String resx
    String resy
    String format
    String coverage
    String crs
    String response_crs
    String service
    String version
    String request
    String stretch_mode
    String stretch_mode_region
    String sharpen_mode
    String sharpen_width
    String sharpen_sigma
    String quicklook
    String null_flip
    String exception
    String bands
    String time
    String interpolation
    String filter
    String max
    def toMap()
    {
       return [bbox: bbox, width: width as Integer, height: height as Integer,
               format: format, resx:resx as Double, resy: resy as Double,
               coverage: coverage, crs: crs, response_crs:response_crs, service: service,
               version: version, request: request, stretch_mode: stretch_mode, interpolation:interpolation,
               stretch_mode_region: stretch_mode_region, sharpen_mode: sharpen_mode,
               sharpen_width: sharpen_width as Double, sharpen_sigma: sharpen_sigma as Double,
               time: time, null_flip: null_flip, exception: exception, filter:filter,
               quicklook: quicklook, max:max].sort { it.key }
    }

    String[] getDates()
    {
      return (time) ? time.split(",") : []
    }
    def getBounds()
    {
        def result = null
        if(bbox)
        {
           def splitBbox = bbox.split(",")
            try{
                def minx = splitBbox[0] as Double
                def miny = splitBbox[1] as Double
                def maxx = splitBbox[2] as Double
                def maxy = splitBbox[3] as Double
                def w = width
                def h = height
                if(!(w&&h)&&resx&&resy)
                {
                    def rx = resx as Double
                    def ry = resy as Double
                    w = (maxx-minx)/rx
                    h = (maxy-miny)/ry
                }
                result = [minx:minx,
                          miny:miny,
                          maxx:maxx,
                          maxy:maxy,
                          width:w as Integer,
                          height:h as Integer]
            }
            catch(Exception e)
            {
              result = null
            }
        }
        result
    }
    def getDateRange()
    {
      def result = []
      def dates = this.dates

      if ( dates )
      {
        (0..<dates.size()).each {
          def range = ISO8601DateParser.getDateRange(dates[it])

          if ( range.size() > 0 )
          {
            result.add(range[0])
            if ( range.size() > 1 )
            {
              result.add(range[1])
            }
          }
        }
      }

      return result
    }
}

