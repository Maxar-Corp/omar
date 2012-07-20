package org.ossim.omar.ogc

class OgcController
{
  def grailsApplication
  def ogcExceptionService

  def springSecurityService


  def getTile( )
  {
    log.warn( "OgcController getTile is deprecated and image space operations should go through ../icp/getTile\ninstead of /ogc/getTile" )
    redirect( controller: "icp", action: "getTile", params: params )
  }

  def imageUtilService


  def chip( )
  {
    def url = ""
    if ( params.type == "tile" )
    {
      url = "http://localhost"
      url += "${createLink( controller: 'icp', action: 'getTileOpenLayers' )}" + "?"
      url += "res=" + params.res + "&"
      url += "x=" + params.x + "&"
      url += "y=" + params.y + "&"
      url += "z=" + params.z + "&"
      url += "tileWidth=" + params.tileWidth + "&"
      url += "tileHeight=" + params.tileHeight + "&"
      url += "id=" + params.id + "&"

    }
    else
    {
      println( "cheese" )
    }

    // def url = "http://localhost"+ "${createLink(controller: "ogc", action: "wms")}" + "?"
    // url += "request=GetMap&"
    // url += "layers=" + params.layers + "&"
    // url += "bbox=" + params.bbox + "&"
    url += "interpolation=" + params.interpolation + "&"
    url += "brightness=" + params.brightness + "&"
    url += "contrast=" + params.contrast + "&"
    url += "sharpen_mode=" + params.sharpen_mode + "&"
    url += "stretch_mode=" + params.stretch_mode + "&"
    url += "stretch_mode_region=" + params.stretch_mode_region + "&"
    url += "bands=" + params.bands + "&"
    // url += "transparent=true&"
    // url += "srs=epsg:4326&"
    // url += "width=" + params.width + "&"
    // url += "height=" + params.height + "&"
    // url += "format=image/png"

    def inputImage = imageUtilService.readImage( url.toURL() )
    def outputImage = imageUtilService.rotateImage( inputImage, params.double( 'imageHeight' ), params.double( 'imageWidth' ), params.double( 'angle' ) )
    def format = params.format ?: "jpeg"
    def ostream = response.outputStream

    def filename = File.createTempFile( "foo", ".${format}" )

    response.setHeader( "Content-disposition", "attachment; filename=${filename}" );
    response.contentType = "image/${format}"
    imageUtilService.writeImageToStream( outputImage, format, ostream )

    ostream.close()
  }

}
