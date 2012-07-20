package org.ossim.omar.raster

import org.ossim.omar.core.Utility
import org.ossim.omar.ogc.OgcController
import org.ossim.omar.ogc.WcsCommand
import org.apache.commons.collections.map.CaseInsensitiveMap

class WcsController extends OgcController
{
  def webCoverageService
  def rasterEntrySearchService

  def wcs( )
  {
    WcsCommand cmd = new WcsCommand()

    bindData( cmd, new CaseInsensitiveMap( params ) )

    //cmd.clearErrors()  // because validation happens on entry so clear errors and re-bind
    // for now until we can develop a plugin for the WCS
    // we will hardcode the output format test list here

    def starttime = System.currentTimeMillis()
    def internaltime = starttime
    def endtime = starttime

    //Utility.simpleCaseInsensitiveBind( cmd, params )

    //  if ( !cmd.validate( [
    //          'width', 'height', 'format', 'crs', 'coverage', 'bbox'
    //  ] ) )
    if ( !cmd.validate() )
    {
      log.error( cmd.createErrorString() )
      ogcExceptionService.writeResponse( response, ogcExceptionService.formatWcsException( cmd ) )
    }
    else
    {
      try
      {
        switch ( cmd?.request?.toLowerCase() )
        {
        case "getcoverage":
          def wmsQuery = new WMSQuery()
          def wcsParams = cmd.toMap();

          // we will use layers for for the WMSQuery object is already setup to use
          // layers param and there is not much difference for query params
          // given WCS or WMS
          wcsParams.layers = params.coverage;
          if ( wcsParams.layers && wcsParams.layers.toLowerCase() == "raster_entry" )
          {
            wcsParams.layers = ""
          }
          // for now I will hard code a max mosaic size
          //
          def max = params.max ? params.max as Integer : 10
          if ( max > 10 ) max = 10
          Utility.simpleCaseInsensitiveBind( wmsQuery, wcsParams )
          wmsQuery.max = max

          // for now we will sort by the date field if no layers are given
          //
          if ( !wmsQuery.layers )
          {
            wmsQuery.sort = wmsQuery.sort ?: "acquisitionDate"
            wmsQuery.order = wmsQuery.order ?: "desc"
          }

          def rasterEntries = rasterEntrySearchService.findRasterEntries( wcsParams?.layers?.split( ',' ) )

          if ( rasterEntries )
          {
            rasterEntries = rasterEntries?.reverse()
          }
          if ( !rasterEntries )
          {
            def message = "WCS server Error: No coverage found for ${coverage}"
            // no data to process
            log.error( message )

            def ogcFormattedException = ogcExceptionService.formatOgcException( cmd.toMap(), message )
            ogcExceptionService.writeResponse( response, ogcFormattedException )
          }
          else
          {
            def result = webCoverageService.getCoverage( rasterEntries, cmd )
            if ( result )
            {
              def imageFile = result.file
              def attachment = result.outputName ? "filename=${result.outputName}" : ""
              response.setHeader( "Content-disposition", "attachment; ${attachment}" )
              response.contentType = result.contentType
              try
              {
                Utility.writeFileToOutputStream( imageFile, response.outputStream, 4096 );
              }
              catch ( Exception e )
              {
                log.error( e )
              }
              response.outputStream.flush()
              response.outputStream.close()

              imageFile.delete()
            }
          }
          break
        default:
          break
        }
      }
      catch ( Exception e )
      {
        e.printStackTrace()
        log.error( e )
      }
    }
    null
  }
}
