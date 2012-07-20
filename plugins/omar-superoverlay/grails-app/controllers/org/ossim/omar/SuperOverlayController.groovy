package org.ossim.omar

import joms.oms.WmsView
import joms.oms.ossimUnitType
import joms.oms.Chain
import org.springframework.beans.factory.InitializingBean

import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.imageio.ImageIO
import org.apache.commons.collections.map.CaseInsensitiveMap
import org.ossim.omar.raster.RasterEntry

class SuperOverlayController implements InitializingBean
{
  def baseDir
  def serverUrl
  def superOverlayService
  def outputKmz = false

  def index( )
  { render ""}

  def createKml( )
  {
    def rasterEntry = null
    try
    {
      if ( params.id )
      {
        rasterEntry = RasterEntry.findByIndexId( params.id ) ?:
          RasterEntry.findByTitle( params.id ) ?:
            RasterEntry.findById( params.id )
      }
    }
    catch ( Exception e )
    {
      rasterEntry = null
    }

    if ( rasterEntry )
    {
      def tempOutputKmz = outputKmz

      // There is currently a bug when a comma separated band list is given for kmz output
      // we will force to false if such a param is given.
      // For some reason if bands= is given and has more than one
      // band indicator it causes X to appear on the google earth window.
      // STill can't locate the reasoning so for now if KMZ is enabled and
      // we see a band= with more than 1 band indicator we will force to false and
      // output as a KML and add a href to the WMS chip service instead of embedding the chip
      // within the KMZ.
      //
      def caseInsensitiveParams = new CaseInsensitiveMap( params )
      if ( tempOutputKmz && caseInsensitiveParams.bands )
      {
        tempOutputKmz = caseInsensitiveParams.bands.split( "," ).length < 2
      }
      // we will return the root document if any level of detail param is null
      //
      def isRoot = ( ( params.level == null ) && ( params.row == null ) && ( params.col == null ) )
      if ( !tempOutputKmz )
      {
        if ( !isRoot )
        {
          def kmlString = superOverlayService.createTileKml( rasterEntry, params )
          //response.setDateHeader("Expires", System.currentTimeMillis()+(24*24*60*60*1000));
          // response.addHeader("Cache-Control", "max-age=120")
          //   response.setHeader("max-age", "120");
          render( contentType: "application/vnd.google-earth.kml+xml", text: kmlString,
                  encoding: "UTF-8" )
        }
        else
        {
          def kmlString = superOverlayService.createRootKml( rasterEntry, params )
          response.setHeader( "Content-disposition", "attachment; filename=doc.kml" )
          render( contentType: "application/vnd.google-earth.kml+xml",
                  text: kmlString,
                  encoding: "UTF-8" )
        }
      }
      else
      {
        if ( !isRoot )
        {
          def kmlInfoMap = superOverlayService.createTileKmzInfo( rasterEntry, params )
          response.contentType = "application/vnd.google-earth.kmz"
          response.setHeader( "Content-disposition", "attachment; filename=output.kmz" )

          def zos = new ZipOutputStream( response.outputStream )
          //create a new zip entry
          def anEntry = null

          anEntry = new ZipEntry( "doc.kml" );
          //place the zip entry in the ZipOutputStream object
          zos.putNextEntry( anEntry );

          zos << kmlInfoMap.kml
          if ( kmlInfoMap.imagePath )
          {
            anEntry = new ZipEntry( "${kmlInfoMap.imagePath}" );
            //place the zip entry in the ZipOutputStream object
            zos.putNextEntry( anEntry );
            if ( kmlInfoMap.image )
            {
              ImageIO.write( kmlInfoMap.image, kmlInfoMap.format, zos );
            }
          }
          zos.close();
          response.outputStream.close();
        }
        else
        {
          def kmlString = superOverlayService.createRootKml( rasterEntry, params )
          response.setHeader( "Content-disposition", "attachment; filename=doc.kml" )
          render( contentType: "application/vnd.google-earth.kml+xml",
                  text: kmlString,
                  encoding: "UTF-8" )
        }
      }
    }
    null
  }

  public void afterPropertiesSet( )
  {
    baseDir = grailsApplication.config.export?.superoverlay?.baseDir
    outputKmz = grailsApplication.config.export?.superoverlay?.outputKmz
    if ( outputKmz == null )
    {
      outputKmz = false // make it default to false
    }
    //serverUrl = grailsApplication.config.export?.superoverlay?.serverUrl
  }
}
