package org.ossim.omar

/**
 * Created by IntelliJ IDEA.
 * User: gpotts
 * Date: 5/16/11
 * Time: 11:02 AM
 * To change this template use File | Settings | File Templates.
 */
class WcsCommand {
    static final def OUTPUT_FORMATS = ["jpeg","image/jpeg", "png", "image/png", "png_uint8",
            "image/tiff", "geotiff", "geotiff_uint8", "geojp2_uint8",
            "geojp2"]
    String bbox
    String width
    String height
    String depth
    String resx
    String resy
    String resz
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
	String brightness
	String contrast

    static constraints = {
        bbox(validator:{val,obj->
            def message = true
            if(obj.request?.toLowerCase() == "getcoverage")
            {
                if(!val)
                {
                    message = "BBOX parameter not found.  This is a required parameter."
                }
                else
                {
                    def box = val.split(",");
                    if(box.length != 4)
                    {
                        message = "BBOX must be formatted with 4 parameters separated by commas and matching the form minx,miny,maxx,maxy in \n"
                        message += "the units of the crs code"
                    }
                }
            }
            else
            {

            }
            message
          }
        )
        width(validator:{val, obj->
            def message = true
            if(obj.request?.toLowerCase() == "getcoverage")
            {
              if((!val) && ((!obj.resx)&&(!obj.resy)))
              {
                  message = "WIDTH parameter not found.  You are required to specify WIDTH, HEIGHT pair or RESX, RESY pair."
              }
            }
            message
        })
        height(validator:{val, obj->
            def message = true
            if(obj.request?.toLowerCase() == "getcoverage")
            {
              if((!val) && ((!obj.resx)&&(!obj.resy)))
              {
                  message = "HEIGHT parameter not found.  You are required to specify WIDTH, HEIGHT pair or RESX, RESY pair."
              }
            }
            message
        })
        resx(validator:{val, obj->
            def message = true
            if(obj.request?.toLowerCase() == "getcoverage")
            {
              if((!val) && ((!obj.width)&&(!obj.height)))
              {
                  message = "RESX parameter not found.  You are required to specify WIDTH, HEIGHT pair or RESX, RESY pair."
              }
            }
            message
        })
        resy(validator:{val, obj->
            def message = true
            if(obj.request?.toLowerCase() == "getcoverage")
            {
              if((!val) && ((!obj.width)&&(!obj.height)))
              {
                  message = "RESY parameter not found.  You are required to specify WIDTH, HEIGHT pair or RESX, RESY pair."
              }
            }
            message
        })
        format(validator:{val,obj->
            def message = true
            if(obj.request?.toLowerCase() == "getcoverage")
            {
                if(!val)
                {
                   message = "FORMAT parameter not found.  This is a required parameter."
                }
                else if(!(val.toLowerCase() in  OUTPUT_FORMATS))
                {
                    message = "FORMAT parameter ${val} not supported.  Values can only be ${OUTPUT_FORMATS.join(', ')}"
                }
            }

            message
        })
        coverage(validator:{val, obj->
            def message = true
            if(obj.request?.toLowerCase() == "getcoverage")
            {
              if(!val)
              {
                  message = "COVERAGE parameter not found.  This is a required parameter."
              }
            }
            message
        })
        crs(validator:{val, obj->
            def message = true
            if(obj.request?.toLowerCase() == "getcoverage")
            {
                if(!val)
                {
                    message = "CRS parameter not found.  This is a required parameter."
                }
                else
                {
                    if(val.toLowerCase().trim() != "epsg:4326")
                    {
                        message = "CRS parameter ${val} not supported.  We only support value of EPSG:4326"
                    }
                }
            }
            message
        })
        response_crs(validator:{val, obj->
            def message = true
            if(obj.request?.toLowerCase() == "getcoverage")
            {
                if(val && (val != obj.crs))
                {
                    message = "RESPONSE_CRS is specified and is not equal to the CRS parameter.  We currently do not support changing the response crs."
                }
            }
            message
        })
        service(validator:{val, obj->
            true
        })
        version(validator:{val, obj->
            true
        })
        request(validator:{val,obj->
            def message = true

            if(!val)
            {
                message = "REQUEST parameter not found.  Values can be getcoverage, getcapabilities, or describecoverage"
            }
            else if(!(val.toLowerCase() in ["getcoverage", "getcapabilities", "describecoverage"]))
            {
                message = "REQUEST parameter ${val} is invalid, values can only be getcoverage, getcapabilities, or describecoverage"
            }
            message
        })

    }
    def toMap()
    {
       return [bbox: bbox, bands:bands, width: width as Integer, height: height as Integer,
               format: format, resx:resx as Double, resy: resy as Double,
               coverage: coverage, crs: crs, response_crs:response_crs, service: service,
               version: version, request: request, stretch_mode: stretch_mode, interpolation:interpolation,
               stretch_mode_region: stretch_mode_region, sharpen_mode: sharpen_mode,
               sharpen_width: sharpen_width as Double, sharpen_sigma: sharpen_sigma as Double,
               time: time, null_flip: null_flip, exception: exception, filter:filter,
               quicklook: quicklook, max:max, brightness:brightness, contrast:contrast].sort { it.key }
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
    def createErrorString()
    {
        def errorString = ""
        errors?.each{err->
            errorString +=  (err.getFieldError("${err.fieldError.arguments[0]}")?.code + "\n")
        }

        errorString
    }
}
