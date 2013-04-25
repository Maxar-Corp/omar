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
		// convert the over height and width values to integers
		textHeight = textHeight as Integer
		textWidth = textWidth as Integer

		// create a blank image upon widht the text will be drawn
		def textBufferedImage = new BufferedImage(textWidth, textHeight, BufferedImage.TYPE_4BYTE_ABGR)
		def textGraphic = textBufferedImage.createGraphics()

		// turn on anti-aliasing		
		def renderingHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
		textGraphic.setRenderingHints(renderingHints)

		// set the color of the text
		textGraphic.setColor(Color.decode("#${textColor}"))

		// iteratively scale the text to fill the entire box
		def font
		def fontMetrics
		def fontSize = 0
		def stringHeight = 0
		def stringWidth = 0
		while (stringHeight < textHeight && stringWidth < textWidth)
		{
			fontSize++
			font = new Font("Arial", Font.BOLD, fontSize)
			textGraphic.setFont(font)
			fontMetrics = textGraphic.getFontMetrics()
			stringWidth = fontMetrics.stringWidth("${text}")
			stringHeight = fontMetrics.getHeight() - fontMetrics.getDescent()
		}
		fontSize--
		font = new Font("Arial", Font.BOLD, fontSize)
		textGraphic.setFont(font)
		fontMetrics = textGraphic.getFontMetrics()

		// determine the text offset for the appropriate alingment
		def textOffsetX
		if (textAlignment == "left") { textOffsetX = 0 }
		else if (textAlignment == "center") { textOffsetX = (textWidth - fontMetrics.stringWidth("${text}")) / 2 as Integer }
		else if (textAlignment == "right") { textOffsetX = textWidth - fontMetrics.stringWidth("${text}") }

		// draw the text
		textGraphic.drawString("${text}", textOffsetX, textHeight - fontMetrics.getDescent())

		// clean up
		textGraphic.dispose()


		return textBufferedImage
	}
}
