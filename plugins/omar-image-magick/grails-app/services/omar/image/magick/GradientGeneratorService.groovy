package omar.image.magick

import java.awt.Color
import java.awt.GradientPaint
import java.awt.image.BufferedImage

import javax.imageio.ImageIO

class GradientGeneratorService 
{
	def serviceMethod(def gradientHeight)
	{
		gradientHeight = gradientHeight as Double
		int gradientBufferedImageHeight = gradientHeight as Integer
		def gradientBufferedImage = new BufferedImage(1, gradientBufferedImageHeight, BufferedImage.TYPE_4BYTE_ABGR)

		float gradientColorHeight = (float) gradientHeight as Integer
		def gradientPaint = new GradientPaint(0, 0, Color.GRAY, 1, gradientColorHeight, Color.BLACK);
		
		def gradientGraphic = gradientBufferedImage.createGraphics()
		gradientGraphic.setPaint(gradientPaint)
		gradientGraphic.fillRect(0, 0, 1, gradientBufferedImageHeight)

		gradientGraphic.dispose()

		return gradientBufferedImage
	}
}
