package org.ossim.omar.app

import org.springframework.beans.factory.InitializingBean
import javax.media.jai.JAI
import org.ossim.omar.raster.WMSQuery
import org.ossim.omar.raster.RasterEntryFile
import org.ossim.omar.raster.RasterEntry
import org.ossim.omar.stager.StagerQueueJob
import org.ossim.omar.stager.StageImageJob

class MapViewController implements InitializingBean
{
  def grailsApplication
  def format
  def baseWMS
  def dataWMS
  def webMappingService
  def imageSpaceService
  def rasterEntrySearchService
  def stageImageService
  def afterInterceptor = { model, modelAndView ->
    if ( request['isMobile'] )
    {
      modelAndView.viewName = modelAndView.viewName + "_mobile"
    }
  }

  def index( )
  {
    WMSQuery query = new WMSQuery();
    def rasterEntries = []
    if ( params.layers )
    {
      query.layers = params.layers

      rasterEntries = rasterEntrySearchService.findRasterEntries( params.layers?.split( ',' ) )

      if ( !rasterEntries )
      {
        render "Alert: No raster matched with layer param: " + params.layers
        return
      }
    }

    def kmlOverlays = []

    def mainFile;
      def entryId;
      def azimuth;
      def imageIds = ""
      def numberOfResLevels = 9999
      rasterEntries.each { rasterEntry ->
          stageImageService.checkAndAddStageImageJob(rasterEntry)
        if(rasterEntry.numberOfResLevels < numberOfResLevels) numberOfResLevels = rasterEntry.numberOfResLevels
        mainFile=rasterEntry.mainFile.name;
        entryId = rasterEntry.entryId as Integer;
        //azimuth = rasterEntry.azimuthAngle;
          if(rasterEntry.azimuthAngle) azimuth=rasterEntry.azimuthAngle;
          if(rasterEntry.title)
          {
              imageIds = imageIds?"${imageIds}, ${rasterEntry.title}": rasterEntry.title
          }
          else if(rasterEntry.filename)
          {
              imageIds = imageIds?"${imageIds}, ${(rasterEntry.filename as File).name}": (rasterEntry.filename as File).name
          }
      if ( ( rasterEntry.validModel != null ) &&
           ( rasterEntry.validModel < 1 ) )
      {
        flash.message = "Valid rigorous model is not supported defaulting to a simple model."
      }
          def overlays = RasterEntryFile.findAllByTypeAndRasterEntry( "kml", rasterEntry )
         overlays?.each {overlay ->

        def kmlOverlay = [:]

        kmlOverlay.name = overlay.name
        kmlOverlay.url = createLink( action: 'getKML', params: [id: overlay?.id] )


        kmlOverlays << kmlOverlay
      }
    }

    def model = [:]


      model.rasterEntries = rasterEntries
    model.imageIds=imageIds
      model.numberOfResLevels = numberOfResLevels
    model.kmlOverlays = kmlOverlays
    model.upIsUpAngle= imageSpaceService?.computeUpIsUp( mainFile, entryId)
    model.azimuthAngle=azimuth
    model.onDemand="${grailsApplication.config.stager.onDemand}"
    model.putAll( webMappingService.computeScales( rasterEntries ) )
    model.putAll( webMappingService.computeBounds( rasterEntries ) )

    return model
  }

  def getKML( )
  {

    def kmlFile = RasterEntryFile.get( params.id )

    if ( !kmlFile )
    {
      flash.message = "RasterEntryFile not found with id ${params.id}"
      redirect( action: 'index' )
    }
    else
    {
      def kmlSource = null

      if ( kmlFile?.name?.startsWith( "http://" ) )
      {
        kmlSource = new URL( kmlFile?.name )
      }
      else
      {
        kmlSource = new File( kmlFile?.name )
      }

      def kml = kmlSource?.text
      //response.setHeader("Content-disposition", "attachment; filename=foo.kml")
      render( contentType: "application/vnd.google-earth.kml+xml", text: kml, encoding: "UTF-8" )
    }
  }

  def multiLayer( )
  {
    WMSQuery query = new WMSQuery();
    def rasterEntries = []
    if ( params.layers )
    {
      query.layers = params.layers

      rasterEntries = rasterEntrySearchService.findRasterEntries( params.layers?.split( ',' ) )

      if ( !rasterEntries )
      {
        render "Alert: No raster matched with layer param: " + params.layers
        return
      }
    }
    def kmlOverlays = []

    rasterEntries.each { rasterEntry ->

      RasterEntryFile.findAllByTypeAndRasterEntry( "kml", rasterEntry )?.each {kmlFile ->
        kmlOverlays << kmlFile
      }
    }

    def model = [:]
    model.rasterEntries = rasterEntries
    model.kmlOverlays = kmlOverlays
    model.baseWMS = baseWMS
    model.format = format ?: "image/png"
    model.putAll( webMappingService.computeScales( rasterEntries ) )
    model.putAll( webMappingService.computeBounds( rasterEntries ) )

    return model
  }

  def test( )
  {
    [baseWMS: baseWMS, dataWMS: dataWMS]
  }

  def imageSpace( )
  {

    def layers = params?.layers?.split( ',' )
    def rasterEntries = rasterEntrySearchService.findRasterEntries( layers )


    if ( rasterEntries )
    {
      def rasterEntry = rasterEntries?.first()
      def imageIds = rasterEntry.title?:(rasterEntry.filename as File).name
       def nAdded = 0
        for (entry in rasterEntries)
      {
          nAdded = stageImageService.checkAndAddStageImageJob(entry)
      }
        def model = [
              onDemand:"${grailsApplication.config.stager.onDemand}",
              rasterEntry: rasterEntry,
              stagingImagery: (nAdded > 0),
              imageIds: imageIds,
              upIsUpRotation: imageSpaceService?.computeUpIsUp( rasterEntry.mainFile.name, rasterEntry.entryId as Integer )
      ]
      return model
    }
    else
    {
      render contentType: 'text/plain', text: "Alert: No raster matched with layer param: ${layers}"
    }
  }

  public void afterPropertiesSet( )
  {
    baseWMS = grailsApplication.config.wms.base.layers
    dataWMS = grailsApplication.config.wms.data.raster
    format = grailsApplication.config.wms.supportIE6 ? "image/gif" : "image/png"
  }

  def iview( )
  {
    def rasterEntry = RasterEntry.get( params.id )

    def inputFile = rasterEntry.mainFile.name
    def width
    def height

    def mode = "OSSIM"

    switch ( mode )
    {
    case "JAI":
      def image = JAI.create( "imageread", inputFile )
      width = image.width
      height = image.height
      break

    case "OSSIM":

      width = rasterEntry?.width
      height = rasterEntry?.height

      break
    }

    def numRLevels = 1
    def tileSize = 256

    while ( width > tileSize )
    {
      width /= 2
      height /= 2
      numRLevels++
    }


    [width: rasterEntry?.width, height: rasterEntry?.height, numRLevels: numRLevels, rasterEntry: rasterEntry]

  }

  def shareLink( )
  {
    render( view: 'imageLink' )
  }
}
