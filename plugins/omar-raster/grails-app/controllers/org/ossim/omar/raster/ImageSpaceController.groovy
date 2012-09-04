package org.ossim.omar.raster

import javax.imageio.ImageIO
import java.awt.Rectangle
import java.awt.image.BufferedImage
import org.apache.commons.collections.map.CaseInsensitiveMap
import org.ossim.omar.raster.RasterEntry
import org.ossim.omar.oms.ProjectionService
import grails.converters.JSON
import org.ossim.omar.raster.ImageSpaceService
import org.ossim.omar.security.SecUser

class ImageSpaceController
{
    def imageSpaceService
    def projectionService
    def getTileLogService
    def springSecurityService
    def index = { }


    def getTile( )
    {
        //  println params

        def paramsIgnoreCase = new CaseInsensitiveMap( params )
        def image = null
        def starttime = System.currentTimeMillis()
        def internaltime = starttime
        def endtime = starttime
        def getTileLogParams = [x:paramsIgnoreCase.x,
                y:paramsIgnoreCase.y,
                width:paramsIgnoreCase.width,
                height:paramsIgnoreCase.height,
                format:paramsIgnoreCase.format,
                layers:paramsIgnoreCase.id,
                scale:paramsIgnoreCase.scale,
                internalTime:0.0,
                renderTime:0.0,
                totalTime:0.0,
                startDate:new Date(),
                endDate:null,
                userName:"",
                ip:"",
                url:""]

        def width = 0
        def height = 0
        def x = 0
        def y = 0
        def format = paramsIgnoreCase.format ?: "image/jpeg"
        def ext = ".jpg"
        getTileLogParams.format = format
        def principal = springSecurityService?.principal
        def hasUserInformation = !( springSecurityService?.principal instanceof String )
        def secUser = hasUserInformation ? SecUser.findByUsername( principal.username ) : null
        getTileLogParams.userName = secUser ? secUser.username : principal
        getTileLogParams.domain = ""
        def domain = null
        def clientIp = request.getHeader( 'Client-ip' )
        def XForwarded = request.getHeader( 'X-Forwarded-For' )
        getTileLogParams.ip = XForwarded
        if ( clientIp )
        {
            if ( getTileLogParams.ip )
            {
                getTileLogParams.ip += ", ${ clientIp }"
            }
            else
            {
                getTileLogParams.ip = clientIp
            }
        }

        if ( !getTileLogParams.ip )
        {
            getTileLogParams.ip = request.getRemoteAddr()
        }


        switch ( paramsIgnoreCase.format?.toLowerCase() )
        {
            case "jpeg":
            case "jpg":
            case "image/jpeg":
            case "image/jpg":
                format = "image/jpeg"
                ext = ".jpg"
                break
            case "png":
            case "image/png":
                format = "image/png"
                ext = ".png"
                break
            case "gif":
            case "image/gif":
                format = "image/gif"
                ext = ".gif"
                break
            default:
                format = "image/jpeg"
                ext = ".jpg"
                break;
        }
        try
        {
            if ( !params.id )
            {
                throw new Exception( "No 'id' value given to chip a tile from, please use id=<value>" )
            }
            // check if minimal params are available
            if ( paramsIgnoreCase.startLine && paramsIgnoreCase.startSample &&
                    paramsIgnoreCase.endLine && paramsIgnoreCase.endSample )
            {
                def line = [paramsIgnoreCase.startLine as Integer,
                        paramsIgnoreCase.endLine as Integer]
                def sample = [paramsIgnoreCase.startSample as Integer,
                        paramsIgnoreCase.endSample as Integer]
                if ( line[1] < line[0] ) line = [line[1], line[0]]
                if ( sample[1] < sample[0] ) sample = [sample[1], sample[0]]
                width = ( sample[1] - sample[0] ) + 1
                height = ( line[1] - line[0] ) + 1
                x = sample[0]
                y = line[0]
            }
            else if ( paramsIgnoreCase.width && paramsIgnoreCase.height &&
                    paramsIgnoreCase.x && paramsIgnoreCase.y )
            {
                width = paramsIgnoreCase.width?.toDouble() as Integer
                height = paramsIgnoreCase.height?.toDouble() as Integer
                x = paramsIgnoreCase.x?.toDouble() as Integer
                y = paramsIgnoreCase.y?.toDouble() as Integer
            }
            else
            {
                throw new Exception( "Improper argument list\nRequires x,y,width,height or startLine, endLine, startSample, endSample" )
            }
        }
        catch ( Exception e )
        {
            log.error( e.message )
            println e.message
            response.contentType = "text/plain"
            return null
        }
        def scale = paramsIgnoreCase.scale ? paramsIgnoreCase.scale.toDouble() : 1.0
        try
        {
            Rectangle rect = new Rectangle( x, y, width, height )
            getTileLogParams.x = x
            getTileLogParams.y = y
            getTileLogParams.width = width
            getTileLogParams.height = height
            def rasterEntry = RasterEntry.findByIndexId( paramsIgnoreCase.id ) ?: RasterEntry.findByTitle( paramsIgnoreCase.id ) ?: RasterEntry.findById( paramsIgnoreCase.id )
            if ( rasterEntry == null )
            {
                image = new BufferedImage( width, height, BufferedImage.TYPE_INT_RGB )
            }
            else
            {
                String inputFile = rasterEntry?.mainFile.name

                width = rasterEntry?.width
                height = rasterEntry?.height
                def bands = rasterEntry?.numberOfBands

                int entry = rasterEntry.entryId?.toInteger()

                paramsIgnoreCase.scale = "${scale}"
                if ( !paramsIgnoreCase.pivot )
                {

                    paramsIgnoreCase.pivot = "${rasterEntry.width * 0.5},${rasterEntry.height * 0.5}"
                }

                image = imageSpaceService.getPixels(
                        rect,
                        rasterEntry,
                        paramsIgnoreCase )

                internaltime = System.currentTimeMillis()
                response.contentType = format
                def attachment = "filename=getTile${ext}"
                response.setHeader( "Content-disposition", "attachment; ${attachment}" )
                ImageIO.write( image, response.contentType?.split( "/" )[-1], response.outputStream )
                getTileLogParams.endDate = new Date();

            }
        }
        catch ( Exception e )
        {
            log.error( e.message )
        }
        endtime = System.currentTimeMillis()
        getTileLogParams.url = createLink( [controller: 'ImageSpace', action: 'getTile', base: "${ grailsApplication.config.omar.serverURL }", absolute: true, params: params] )

        getTileLogParams.internalTime = (internaltime-starttime)/1000.0;
        getTileLogParams.renderTime = (endtime-starttime)/1000.0;
        getTileLogParams.totalTime  = (getTileLogParams.internalTime + getTileLogParams.renderTime)
        getTileLogService.logParams(getTileLogParams);
        return null;
    }

    /**
     * This can take a request formated with either:
     *  id=<index|record|image>&x= & y= &
     * or you can pass a JSON formatted text string listing a sequence of points to
     * transform
     *  id=<index|record|image>
     *
     *  POST data fiels format:
     *
     *
     *{id:${rasterEntry.id},
     *    imagePoints:[{"x":<x value>, "y":<y value>}]
     *}*
     * you can optionally pass the id in the json text or as a parameter to the method.  The imagePoints is
     * a formatted list of jason x,y point objects.
     *
     * @return a list of Json objects with properties x,y of the original request and the lat, lon, hgt values
     *           [ {x:pt.x,
     *                          y:pt.y,
     *                          lat:groundPoint.latd(),
     *                          lon:groundPoint.lond(),
     *                          hgt:groundPoint.height()}, ..... ]
     */
    def imageToGround( )
    {
        // support reading from a post data variable or from URL
        // bound params
        //
        def data = request.reader.text;
        def result = [:]
        def paramsIgnoreCase = new CaseInsensitiveMap( params )
        def jsonData
        def jsonPoints
        if ( data )
        {
            jsonData = JSON.parse( data );
        }
        if ( !paramsIgnoreCase.id )
        {
            paramsIgnoreCase.id = jsonData?.id
        }
        if ( !paramsIgnoreCase.imagePoints )
        {
            jsonPoints = jsonData.imagePoints
        }
        else
        {
            jsonPoints = JSON.parse( paramsIgnoreCase.imagePoints )
        }
        def rasterEntry = RasterEntry.findByIndexId( paramsIgnoreCase.id ) ?:
            RasterEntry.findByTitle( paramsIgnoreCase.id ) ?:
                RasterEntry.findById( paramsIgnoreCase.id )

        if ( rasterEntry )
        {
            String inputFile = rasterEntry?.mainFile.name
            if ( paramsIgnoreCase.x && paramsIgnoreCase.y )
            {
                result = projectionService.imageSpaceToGroundSpace( inputFile,
                        paramsIgnoreCase.x as double,
                        paramsIgnoreCase.y as double,
                        rasterEntry.entryId as Integer )
            }
            else if ( jsonPoints )
            {
                result = projectionService.imageSpaceListToGroundSpace( inputFile,
                        jsonPoints,
                        rasterEntry.entryId as Integer )
            }
        }
        render( result as JSON );
    }

    /**
     * This can take a request formatted with either:
     *  id=<index|record|image>&x= & y= &
     * or you can pass a JSON formatted text string listing a sequence of points to
     * transform
     *  id=<index|record|image>
     *
     *  POST data fields format:
     *
     *
     *{id:${rasterEntry.id},
     *    imagePoints:[{"x":<x value>, "y":<y value>}]
     *}*
     * you can optionally pass the id in the JSON text or as a parameter to the method.  The imagePoints is
     * a formatted list of JSON x,y point objects.
     *
     * @return a list of JSON objects with properties x,y of the original request and the lat, lon, hgt values
     *           [ {x:pt.x,
     *                          y:pt.y,
     *                          lat:groundPoint.latd(),
     *                          lon:groundPoint.lond(),
     *                          hgt:groundPoint.height()}, ..... ]
     */
    def imageToGroundFull( )
    {
        // support reading from a post data variable or from URL
        // bound params
        //
        def data = request.reader.text;
        def result = [:]
        def paramsIgnoreCase = new CaseInsensitiveMap( params )
        def jsonData
        def jsonPoints
        if ( data )
        {
            jsonData = JSON.parse( data );
        }
        if ( !paramsIgnoreCase.id )
        {
            paramsIgnoreCase.id = jsonData?.id
        }
        if ( !paramsIgnoreCase.imagePoints )
        {
            jsonPoints = jsonData.imagePoints
        }
        else
        {
            jsonPoints = JSON.parse( paramsIgnoreCase.imagePoints )
        }
        def rasterEntry = RasterEntry.findByIndexId( paramsIgnoreCase.id ) ?:
            RasterEntry.findByTitle( paramsIgnoreCase.id ) ?:
                RasterEntry.findById( paramsIgnoreCase.id )

        if ( rasterEntry )
        {
            String inputFile = rasterEntry?.mainFile.name
            if ( paramsIgnoreCase.x && paramsIgnoreCase.y )
            {
                result = projectionService.imageSpaceToGroundSpace( inputFile,
                        paramsIgnoreCase.x as double,
                        paramsIgnoreCase.y as double,
                        rasterEntry.entryId as Integer )
            }
            else if ( jsonPoints )
            {
                // Angular increment for plotting ellipse polygon in image space
                def ellAngInc = 10.0

                // Probability level (.5P, .9P, .95P)
                def pLevel = jsonData?.pLevel ?: 0.9

                String xs = jsonPoints.x
                String xss = xs.replace( '[', '' ).replace( ']', '' )
                String ys = jsonPoints.y
                String yss = ys.replace( '[', '' ).replace( ']', '' )
                def pt = [x: xss as double, y: yss as double]
                result = projectionService.imageSpaceToGroundSpace( inputFile,
                        pt,
                        pLevel,
                        ellAngInc,
                        rasterEntry.entryId as Integer )
            }
        }
        render( result as JSON );
    }

    /**
     * This can take a request formated with either:
     *  id=<index|record|image>&lat= & lon= & hgt=
     * or you can pass a JSON formatted text string listing a sequence of points to
     * transform
     *  id=<index|record|image>
     *
     *  POST data fiels format:
     *
     *
     *{id:${rasterEntry.id},
     *    groundPoints:[{"lat":<latitude in degrees>, "lon":<longitude in degrees>, "hgt":<height in meters>}]
     *}*
     * you can optionally pass the id in the json text or as a parameter to the method.  The imagePoints is
     * a formatted list of jason x,y point objects.
     *
     * @return a list of Json objects with properties lat,lon,hgt of the original request and the x,y resultValues
     *           [ {x:pt.x,
     *                          y:pt.y,
     *                          lat:groundPoint.latd(),
     *                          lon:groundPoint.lond(),
     *                          hgt:groundPoint.height()}, ..... ]
     */
    def groundToImage( )
    {
        // support reading from a post data variable or from URL
        // bound params
        //
        def data = request.reader.text;
        def result = [:]
        def paramsIgnoreCase = new CaseInsensitiveMap( params )
        def jsonData
        def jsonPoints
        if ( data )
        {
            jsonData = JSON.parse( data );
        }
        if ( !paramsIgnoreCase.id )
        {
            paramsIgnoreCase.id = jsonData?.id
        }
        if ( !paramsIgnoreCase.groundPoints )
        {
            jsonPoints = jsonData.groundPoints
        }
        else
        {
            jsonPoints = JSON.parse( paramsIgnoreCase.groundPoints ).groundPoints
        }
        def rasterEntry = RasterEntry.findByIndexId( paramsIgnoreCase.id ) ?:
            RasterEntry.findByTitle( paramsIgnoreCase.id ) ?:
                RasterEntry.findById( paramsIgnoreCase.id )

        if ( rasterEntry )
        {
            String inputFile = rasterEntry?.mainFile.name
            def hgt = paramsIgnoreCase.hgt ?: 0.0
            if ( paramsIgnoreCase.lat && paramsIgnoreCase.lon )
            {
                result = projectionService.groundSpaceToImageSpace( inputFile,
                        paramsIgnoreCase.lat as double,
                        paramsIgnoreCase.lon as double,
                        hgt,
                        rasterEntry.entryId as Integer )
            }
            else if ( jsonPoints )
            {
                result = projectionService.groundSpaceListToImageSpace( inputFile,
                        jsonPoints,
                        rasterEntry.entryId as Integer )
            }
        }
        render( result as JSON );
    }

    def measure( )
    {
        def data = request.reader.text;
        def paramsIgnoreCase = new CaseInsensitiveMap( params )
        def jsonData
        def jsonFeature

        if ( data )
        {
            jsonData = JSON.parse( data );
        }
        if ( !paramsIgnoreCase.id )
        {
            paramsIgnoreCase.id = jsonData?.id
        }
        if ( !params.feature )
        {
            jsonFeature = jsonData?.feature
        }
        else
        {
            jsonFeature = JSON.parse( paramsIgnoreCase.feature )
        }

        def result = [:]
        def rasterEntry = RasterEntry.findByIndexId( paramsIgnoreCase.id ) ?:
            RasterEntry.findByTitle( paramsIgnoreCase.id ) ?:
                RasterEntry.findById( paramsIgnoreCase.id )
        if ( rasterEntry )
        {
            String inputFile = rasterEntry?.mainFile.name;
            result = projectionService.imageSpaceWKTMeasure( [filename: inputFile,
                    wkt: jsonFeature.wkt,
                    entryId: rasterEntry.entryId as Integer] );
        }
        render( result as JSON );
    }
}
