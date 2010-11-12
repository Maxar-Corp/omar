package org.ossim.omar

import java.awt.Rectangle
import java.awt.image.BufferedImage
import joms.oms.WmsMap
import java.awt.image.DataBuffer
import joms.oms.ossimKeywordlist
import java.awt.image.DataBufferByte
import java.awt.Point
import java.awt.image.WritableRaster
import java.awt.image.ColorModel
import org.ossim.oms.image.omsImageSource

class IcpService {

  static transactional = true
  BufferedImage getPixels(Rectangle rect,
                          String inputFile,
                          int entry,
                          def inputBandCount,
                          BigDecimal scale,
                          def params)
    {
      def sharpenMode = params.sharpen_mode ?: ""
      def bands = params?.bands ?: ""
      def rotate = params?.rotate ?: "0.0"
      int viewableBandCount = 1
      if ( sharpenMode.equals("light") )
      {
        params.sharpen_width = "3"
        params.sharpen_sigma = ".5"
      }
      else if ( sharpenMode.equals("heavy") )
      {
        params.sharpen_width = "5"
        params.sharpen_sigma = "1"
      }
      if ( inputBandCount >= 3 )
      {
        viewableBandCount = 3
      }
      def bandSelectorCount = bands ? bands.split(",").length : 0
      if ( bandSelectorCount > 0 )
      {
        if ( bandSelectorCount >= 3 )
        {
          viewableBandCount = 3;
        }
        else
        {
          viewableBandCount = 1;
        }
      }
//    println params
//    println rect
//    println "${inputFile} ${entry}"
//    println stretchMode
//    println viewportStretchMode
//    println scale
//    println "${startSample} ${endSample} ${startLine} ${endLine}"

      byte[] data = new byte[rect.width * rect.height * 3]
      def kwl = new ossimKeywordlist();
      params.each {name, value ->
        kwl.add(name, value as String)
      }
      kwl.add("viewable_bands", "${viewableBandCount}")
      kwl.add("rotate", "${rotate}")

      WmsMap.getUnprojectedMap(
              inputFile,
              entry,
              scale,
              rect.x, rect.x + rect.width-1, rect.y, rect.y + rect.height-1,
              data,
              kwl
      )
      DataBuffer dataBuffer = new DataBufferByte(data, data.size())
      int pixelStride = viewableBandCount
      int lineStride = viewableBandCount * rect.width
      int[] bandOffsets = null;
      if ( viewableBandCount == 1 )
      {
        bandOffsets = [0] as int[]
      }
      else
      {
        bandOffsets = [0, 1, 2] as int[]
      }
      def image;
      if ( viewableBandCount == 1 )
      {
        image = Utility.convertToColorIndexModel(dataBuffer, rect.width as Integer, rect.height as Integer, false)
      }
      else
      {
        Point location = null
        WritableRaster raster = WritableRaster.createInterleavedRaster(
                dataBuffer,
                rect.width as Integer,
                rect.height as Integer,
                lineStride,
                pixelStride,
                bandOffsets,
                location)

        ColorModel colorModel = omsImageSource.createColorModel(raster.sampleModel)

        boolean isRasterPremultiplied = true
        Hashtable<?, ?> properties = null

        image = new BufferedImage(
                colorModel,
                raster,
                isRasterPremultiplied,
                properties)
      }
      return image
    }
}
