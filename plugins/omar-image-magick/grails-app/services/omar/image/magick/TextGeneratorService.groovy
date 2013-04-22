package omar.image.magick

import java.awt.Color
import java.awt.Font
import java.awt.FontMetrics
import java.awt.image.BufferedImage
import java.awt.RenderingHints

import javax.imageio.ImageIO

class TextGeneratorService
{
	def serviceMethod(def text, def textAlignment, def textColor, def textHeight, def textWidth)
	{
		textHeight = textHeight as Integer
		textWidth = textWidth as Integer
		def font = new Font("Arial", Font.PLAIN, textHeight);

		def textBufferedImage = new BufferedImage(textWidth, textHeight, BufferedImage.TYPE_4BYTE_ABGR)
		def textGraphic = textBufferedImage.createGraphics()

		def renderingHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
		textGraphic.setRenderingHints(renderingHints)

		textGraphic.setFont(font)
		def fontMetrics = textGraphic.getFontMetrics()

		def textOffsetX
		if (textAlignment == "left")
		{
			textOffsetX = 0
		}
		else if (textAlignment == "center")
		{
			def stringWidth = 0
			//def fontSize = 1 as Integer
			//while (stringWidth < textWidth) // || textHeight < fontMetrics.getHeight())
			//{
			//	font = new Font("Arial", Font.PLAIN, fontSize)
			//	textGraphic.setFont(font)
			//	stringWidth = fontMetrics.stringWidth("${text}")
			//	println stringWidth
			//	println textWidth
			//	println fontMetrics.getHeight()
			//	fontSize++
			//}

			//fontSize--
			//font = new Font("Arial", Font.PLAIN, fontSize)
			//textGraphic.setFont(font)
			stringWidth = fontMetrics.stringWidth("${text}")
			textOffsetX = (textWidth - stringWidth) / 2 as Integer
		}
		else if (textAlignment == "right")
		{
			def stringWidth = fontMetrics.stringWidth("${text}")
			textOffsetX = textWidth - stringWidth
		}

		textGraphic.setColor(textColor)
		textGraphic.drawString("${text}", textOffsetX, textHeight)

		textGraphic.dispose()


		return textBufferedImage
	}
}
