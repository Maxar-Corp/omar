package org.ossim.omar.raster

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
import org.ossim.omar.core.Utility
import joms.oms.ImageModel

class ImageSpaceService {
	def imageChainService
	
  static transactional = true
   def getPixels(Rectangle rect,
                          def rasterEntry,
                          def params)
    {
//		def newParams = params.clone()
//		newParams.image_cut = "${rect.x},${rect.y},${rect.width},${rect.height}"
		def result = null
		def maxBands = 0
		def rasterChain             = imageChainService.createImageChain(rasterEntry, params).chain
		def stretchMode       = params.stretch_mode?params.stretch_mode.toLowerCase():null
		def stretchModeRegion = params.stretch_mode_region?params.stretch_mode_region.toLowerCase():null
		//rasterChain.print()
		if(rasterChain)
		{
			maxBands = rasterChain.getChainAsImageSource().getNumberOfOutputBands()
			def objectPrefixIdx = 0
			def kwlString = "type:ossimImageChain\n"
			kwlString += "object${objectPrefixIdx}.type:ossimRectangleCutFilter\n"
			kwlString += "object${objectPrefixIdx}.rect:(${rect.x},${rect.y},${rect.width},${rect.height},lh)\n"
			kwlString += "object${objectPrefixIdx}.cut_type:null_outside\n"
			kwlString += "object${objectPrefixIdx}.id:10001\n"
			++objectPrefixIdx
			if(stretchModeRegion == "viewport")
			{
				kwlString += "object${objectPrefixIdx}.type:ossimImageHistogramSource\n"
				kwlString += "object${objectPrefixIdx}.id:10002\n"
				++objectPrefixIdx
				kwlString += "object${objectPrefixIdx}.type:ossimHistogramRemapper\n"
				kwlString += "object${objectPrefixIdx}.id:10003\n"
				kwlString += "object${objectPrefixIdx}.stretch_mode:${stretchMode}\n"
				kwlString += "object${objectPrefixIdx}.input_connection1:10001\n"
				kwlString += "object${objectPrefixIdx}.input_connection2:10002\n"
				++objectPrefixIdx
			}
			if(maxBands == 2)
			{
					kwlString += "object${objectPrefixIdx}.type:ossimBandSelector\n"
					kwlString += "object${objectPrefixIdx}.bands:(0)\n"
					++objectPrefixIdx
			}
			else if(maxBands > 3)
			{
					kwlString += "object${objectPrefixIdx}.type:ossimBandSelector\n"
					kwlString += "object${objectPrefixIdx}.bands:(0,1,2)\n"
					++objectPrefixIdx
			}
	        kwlString += "object${objectPrefixIdx}.type:ossimScalarRemapper\n"
			++objectPrefixIdx
			//println kwlString
			//println "*"*40
			def chipChain = new joms.oms.Chain();
			chipChain.loadChainKwlString(kwlString)
			
			//chipChain.print()
			chipChain.connectMyInputTo(rasterChain)
			result = imageChainService.grabOptimizedImageFromChain(chipChain, params)
			chipChain.deleteChain();
			rasterChain.deleteChain();
			chipChain = null
			rasterChain = null
		}
		result
	}
	BufferedImage getPixelsOld(Rectangle rect,
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
    def computeUpIsUp(String filename, Integer entryId)
    {
        Double upIsUp = 0.0

        def imageSpaceModel = new ImageModel()
        if ( imageSpaceModel.setModelFromFile(filename, entryId as Integer) )
        {
            upIsUp = imageSpaceModel.upIsUpRotation();
            imageSpaceModel.destroy()
            imageSpaceModel.delete()
        }

        return upIsUp
    }
}
