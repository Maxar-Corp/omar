package chipper

import java.awt.Point
import java.awt.Transparency
import java.awt.color.ColorSpace
import java.awt.image.BufferedImage
import java.awt.image.ComponentColorModel
import java.awt.image.DataBuffer
import java.awt.image.PixelInterleavedSampleModel
import java.awt.image.Raster

import javax.imageio.ImageIO
import joms.oms.Chipper
import joms.oms.ElevMgr
import joms.oms.StringVector


import geoscript.geom.Bounds

class ChipperService
{
  static transactional = false
  def grailsApplication

  enum RenderMode {
    BLANK, CHIPPER_NEW, CHIPPER
  }


  def getChip(Chip chpCmd)
  {
    // println chpCmd

    // def renderMode = RenderMode.BLANK
    def renderMode = RenderMode.CHIPPER
    def ostream = new ByteArrayOutputStream()

    switch ( renderMode )
    {
    case RenderMode.BLANK:
      //createBlankTile( chpCmd, ostream )
      break

    case RenderMode.CHIPPER_NEW:
      try
      {
        Map<String, String> chipperOptionsMap = chpCmd.createChipperOptions()

        def width = chpCmd.size.width as Integer
        def height = chpCmd.size.height as Integer
        def numBands = ( chpCmd.transparent ) ? 4 : 3
        def format = chpCmd.format
        def type = format?.split( '/' )[-1]
        def sampleModel = createSampleModel( width, height, numBands, chpCmd.transparent )
        def dataBuffer = populateTile( sampleModel, chipperOptionsMap, chpCmd.transparent )
        def image = createImage( sampleModel, dataBuffer, chpCmd.transparent )

        ImageIO.write( image, type, ostream )
      }
      catch ( e )
      {
        println e.message

        def image = new BufferedImage( chpCmd.size.width as Integer, chpCmd.size.height as Integer, BufferedImage.TYPE_3BYTE_BGR )

        ImageIO.write( image, 'jpeg', ostream )
      }
      break

    case RenderMode.CHIPPER:
      def cs = ColorSpace.getInstance( ColorSpace.CS_sRGB )

      def numBands = 4
      def mask = ( ( 0..<numBands ).collect { 8 } ) as int[]

      def colorModel = new ComponentColorModel( cs, mask,
          true, false, Transparency.TRANSLUCENT, DataBuffer.TYPE_BYTE )

      def width = chpCmd.size.width as Integer
      def height = chpCmd.size.height as Integer

      def sampleModel = new PixelInterleavedSampleModel(
          DataBuffer.TYPE_BYTE,
          width,                    // width
          height,                   // height
          numBands,                 // pixelStride
          width * numBands,         // scanlineStride
          ( 0..<numBands ) as int[] // band offsets
      )

      def dataBuffer = sampleModel.createDataBuffer()
      def chipperOptionsMap = chpCmd.createChipperOptions()
      def chipper = new Chipper()

      try
      {
        if ( chipper.initialize( chipperOptionsMap ) )
        {
          println "calling chipper.initialize( ${chipperOptionsMap} )"

          if ( chipper.getChip( dataBuffer.data, true ) )
          {
            println "chipper.getChip good..."
          }
        }
      }
      catch ( e )
      {
        e.printStackTrace()
      }
      finally
      {
        chipper.delete()
      }

      def raster = Raster.createRaster( sampleModel, dataBuffer, new Point( 0, 0 ) )
      def image = new BufferedImage( colorModel, raster, false, null )
      def outputFormat = chpCmd?.format?.split( '/' )[-1];
      //image = fixImageForOutputFormat(image, outputFormat)
      ImageIO.write( image, outputFormat, ostream )

      break
    // End: case RenderMode.CHIPPER:

    } // End: switch( renderMode

    [contentType: chpCmd?.format, buffer: ostream.toByteArray()]
  }

  private void createBlankTile(ChipCommand chpCmd, ByteArrayOutputStream ostream)
  {
    def image = new BufferedImage(
        chpCmd?.width, chpCmd?.height, BufferedImage.TYPE_INT_ARGB )
    ImageIO.write( image, chpCmd?.format?.split( '/' )[-1], ostream )
  }

  def populateTile(def sampleModel, def chipperOptionsMap, def addAlpha = false)
  {
    def dataBuffer = sampleModel.createDataBuffer()
    def chipper = new Chipper()

    if ( chipper.initialize( chipperOptionsMap ) )
    {
      if ( chipper.getChip( dataBuffer.data, addAlpha ) > 1 )
      {
        println "chipper.getChip good..."
      }
      else
      {
        println 'chipper.getChip not good...'
      }
    }
    else
    {
      println 'chipper.getChip not good...'
    }

    return dataBuffer
  }


  def getPSM(Chip chpCmd)
  {
    // println chpCmd

    // def renderMode = RenderMode.BLANK
    def renderMode = RenderMode.CHIPPER
    def ostream = new ByteArrayOutputStream()

    switch ( renderMode )
    {
    case RenderMode.BLANK:
      //createBlankTile( chpCmd, ostream )
      break

    case RenderMode.CHIPPER_NEW:
      Map<String, String> chipperOptionsMap = chpCmd.createChipperOptions()

      if ( chipperOptionsMap )
      {
        if ( !populateTile( chipperOptionsMap, chpCmd, ostream ) )
        {
          //createBlankTile( chpCmd, ostream )
        }
        // End: case RenderMode.CHIPPER:
      } // End: switch( renderMode
      break
    case RenderMode.CHIPPER:
      def cs = ColorSpace.getInstance( ColorSpace.CS_sRGB )

      def numBands = 4
      def mask = ( ( 0..<numBands ).collect { 8 } ) as int[]

      def colorModel = new ComponentColorModel( cs, mask,
          true, false, Transparency.TRANSLUCENT, DataBuffer.TYPE_BYTE )

      def width = chpCmd.size.width as Integer
      def height = chpCmd.size.height as Integer

      def sampleModel = new PixelInterleavedSampleModel(
          DataBuffer.TYPE_BYTE,
          width,                    // width
          height,                   // height
          numBands,                 // pixelStride
          width * numBands,         // scanlineStride
          ( 0..<numBands ) as int[] // band offsets
      )

      def dataBuffer = sampleModel.createDataBuffer()
      def chipperOptionsMap = chpCmd.createChipperOptions()
      def chipper = new Chipper()

      try
      {
        if ( chipper.initialize( chipperOptionsMap ) )
        {
          println "calling chipper.initialize( ${chipperOptionsMap} )"

          if ( chipper.getChip( dataBuffer.data, true ) )
          {
            println "chipper.getChip good..."
          }
        }
      }
      catch ( e )
      {
        e.printStackTrace()
      }
      finally
      {
        chipper.delete()
      }

      def raster = Raster.createRaster( sampleModel, dataBuffer, new Point( 0, 0 ) )
      def image = new BufferedImage( colorModel, raster, false, null )
      def outputFormat = chpCmd?.format?.split( '/' )[-1];
      //image = fixImageForOutputFormat(image, outputFormat)
      ImageIO.write( image, outputFormat, ostream )
      break
    }
    [contentType: chpCmd?.format, buffer: ostream.toByteArray()]
  } // End: def getPSM(def chpCmd)

  def get2CMV(TwoColorMultiView chpCmd)
  {
    // println chpCmd

    // def renderMode = RenderMode.BLANK
    def renderMode = RenderMode.CHIPPER
    def ostream = new ByteArrayOutputStream()

    switch ( renderMode )
    {
    case RenderMode.BLANK:
      //createBlankTile( chpCmd, ostream )
      break

    case RenderMode.CHIPPER_NEW:
      Map<String, String> chipperOptionsMap = chpCmd.createChipperOptions()


      def width = chpCmd?.size?.width as Integer
      def height = chpCmd?.size?.height as Integer
      def numBands = ( chpCmd.transparent ) ? 4 : 3
      def format = chpCmd.format

      def sampleModel = createSampleModel( width, height, numBands, chpCmd.transparent )
      def dataBuffer = populateTile( sampleModel, chipperOptionsMap, chpCmd.transparent )
      def image = createImage( sampleModel, dataBuffer, chpCmd.transparent )

      ImageIO.write( image, format, ostream )
      break

    case RenderMode.CHIPPER:
      def cs = ColorSpace.getInstance( ColorSpace.CS_sRGB )

      def numBands = 4
      def mask = ( ( 0..<numBands ).collect { 8 } ) as int[]

      def colorModel = new ComponentColorModel( cs, mask,
          true, false, Transparency.TRANSLUCENT, DataBuffer.TYPE_BYTE )

      def width = chpCmd.size.width as Integer
      def height = chpCmd.size.height as Integer

      def sampleModel = new PixelInterleavedSampleModel(
          DataBuffer.TYPE_BYTE,
          width,                    // width
          height,                   // height
          numBands,                 // pixelStride
          width * numBands,         // scanlineStride
          ( 0..<numBands ) as int[] // band offsets
      )

      def dataBuffer = sampleModel.createDataBuffer()
      def chipperOptionsMap = chpCmd.createChipperOptions()
      def chipper = new Chipper()

      try
      {
        if ( chipper.initialize( chipperOptionsMap ) )
        {
          println "calling chipper.initialize( ${chipperOptionsMap} )"

          if ( chipper.getChip( dataBuffer.data, true ) )
          {
            println "chipper.getChip good..."
          }
        }
      }
      catch ( e )
      {
        e.printStackTrace()
      }
      finally
      {
        chipper.delete()
      }

      def raster = Raster.createRaster( sampleModel, dataBuffer, new Point( 0, 0 ) )
      def image = new BufferedImage( colorModel, raster, false, null )
      def outputFormat = chpCmd?.format?.split( '/' )[-1];
      //image = fixImageForOutputFormat(image, outputFormat)
      ImageIO.write( image, outputFormat, ostream )

      break

    // End: case RenderMode.CHIPPER:

    } // End: switch( renderMode

    [contentType: chpCmd?.format, buffer: ostream.toByteArray()]
  } // End: def get2CMV(def chpCmd)


  def getHillShade(ChipCommand chpCmd)
  {
    println chpCmd

    // def renderMode = RenderMode.BLANK
    def renderMode = RenderMode.CHIPPER
    def ostream = new ByteArrayOutputStream()

    switch ( renderMode )
    {
    case RenderMode.BLANK:
      //createBlankTile( chpCmd, ostream )
      break

    case RenderMode.CHIPPER:
      Map<String, String> chipperOptionsMap = createHillShadeParams( chpCmd )

      if ( chipperOptionsMap )
      {
        if ( !populateTile( chipperOptionsMap, chpCmd, ostream ) )
        {
          //createBlankTile( chpCmd, ostream )
        }
        break
        // End: case RenderMode.CHIPPER:

      } // End: switch( renderMode
    }
    [contentType: chpCmd?.format, buffer: ostream.toByteArray()]
  } // End: def getHillShade(def chpCmd)

  def findElevationCells(String path, Bounds bounds)
  {
    def cells = new StringVector()
    def filenames = []

    ElevMgr.instance().getCellsForBounds( path, bounds.minY, bounds.minX, bounds.maxY, bounds.maxX, cells )

    for ( x in ( 0..<cells?.size() ) )
    {
      filenames << cells?.get( x as int )
    }

    return filenames
  }


  def createImage(def sampleModel, def dataBuffer, def hasAlpha = false, def alphaPreMultiplied = false)
  {
/*
*/
    def raster = Raster.createRaster( sampleModel, dataBuffer, new Point( 0, 0 ) )

/*
    println colorModel


*/

    def image

    if ( hasAlpha )
    {
/*
        def cs = ColorSpace.getInstance( ColorSpace.CS_sRGB )
        def mask = ( ( 0..<sampleModel.numBands ).collect { 8 } ) as int[]

        def colorModel = new ComponentColorModel( cs, mask,
            hasAlpha, alphaPreMultiplied, Transparency.TRANSLUCENT, DataBuffer.TYPE_BYTE )

        image = new BufferedImage( colorModel, raster, alphaPreMultiplied, null )
*/
      image = new BufferedImage( raster.width, raster.height, BufferedImage.TYPE_INT_ARGB )
      image.setData( raster )
    }
    else
    {
      image = new BufferedImage( raster.width, raster.height, BufferedImage.TYPE_INT_RGB )
      image.setData( raster )
    }

    return image
  }

  def createSampleModel(Integer width, Integer height, Integer numBands, Boolean addAlpha = false)
  {
    Integer numSamples = ( addAlpha ) ? numBands + 1 : numBands
    def sampleModel = new PixelInterleavedSampleModel(
        DataBuffer.TYPE_BYTE,
        width,                      // width
        height,                     // height
        numSamples,                 // pixelStride
        width * numSamples,         // scanlineStride
        ( 0..<numSamples ) as int[] // band offsets
    )

    return sampleModel
  }

  def getThumbnail(Thumbnail chpCmd)
  {
    // println chpCmd

    // def renderMode = RenderMode.BLANK
    def renderMode = RenderMode.CHIPPER
    def ostream = new ByteArrayOutputStream()

    switch ( renderMode )
    {
    case RenderMode.BLANK:
      //createBlankTile( chpCmd, ostream )
      break

    case RenderMode.CHIPPER:
      Map<String, String> chipperOptionsMap = chpCmd.createChipperOptions()


      println chipperOptionsMap

      def width = chpCmd.size.width as Integer
      def height = chpCmd.size.height as Integer
      def numBands = ( chpCmd.transparent ) ? 4 : 3
      def format = chpCmd.format

      def sampleModel = createSampleModel( width, height, numBands, chpCmd.transparent )
      def dataBuffer = populateTile( sampleModel, chipperOptionsMap, chpCmd.transparent )
      def image = createImage( sampleModel, dataBuffer, chpCmd.transparent )

      ImageIO.write( image, format, ostream )
      break

    // End: case RenderMode.CHIPPER:

    } // End: switch( renderMode

    [contentType: chpCmd?.format, buffer: ostream.toByteArray()]
  }


} // End: class ChipperService
