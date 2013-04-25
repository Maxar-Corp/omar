package omar.image.magick

import java.awt.Color
import java.awt.image.BufferedImage
import java.awt.RenderingHints

import javax.imageio.ImageIO

class NorthArrowGeneratorService
{
	def serviceMethod(def northAngle, def northArrowColor, def northArrowBackgroundColor, def northArrowSize)
	{
		def size = 1000
		def strokeWidth = 50
		def buffer = 50

		def imageSize = (northArrowSize.toDouble()).toInteger()
		def image = new BufferedImage(imageSize, imageSize, BufferedImage.TYPE_4BYTE_ABGR)
		
		def graph = image.createGraphics()
		def renderingHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
		graph.setRenderingHints(renderingHints)

		def scale = (northArrowSize).toDouble() / size
		graph.scale(scale, scale)
		
		def northAngleInRadians = (northAngle.toDouble()).toInteger() * Math.PI / 180
		graph.rotate(northAngleInRadians, size/2, size/2)

		graph.setColor(new Color(0, 0, 0, 0))
		graph.fillRect(0, 0, size, size)

		graph.setColor(Color.decode("#${northArrowBackgroundColor}"))
		graph.drawOval(0, 0, size, size)
		graph.fillOval(0, 0, size, size)

		def outterCircleDiameter = (0.66 * size).toInteger()
		def outterCircleX = ((size - outterCircleDiameter) / 2).toInteger()
		def outterCircleY = size - outterCircleDiameter - buffer
		graph.setColor(Color.decode("#${northArrowColor}"))
		graph.fillOval(outterCircleX, outterCircleY, outterCircleDiameter, outterCircleDiameter)

		def innerCircleDiameter = outterCircleDiameter - 2 * strokeWidth
		def innerCircleX = outterCircleX + strokeWidth
		def innerCircleY = outterCircleY + strokeWidth
		graph.setColor(Color.decode("#${northArrowBackgroundColor}"))
		graph.fillOval(innerCircleX, innerCircleY, innerCircleDiameter, innerCircleDiameter)

		def rectangleHeight = outterCircleY - 2 * buffer
		def rectangleWidth = rectangleHeight
		int[] triangleX = [size/2, size/2 + rectangleWidth/2, size/2 - rectangleWidth / 2]
		int[] triangleY = [buffer, buffer + rectangleHeight, buffer + rectangleHeight]
		graph.setColor(Color.decode("#${northArrowColor}"))
		graph.fillPolygon(triangleX, triangleY, 3)

		def nHeight = 0.5 * innerCircleDiameter
		def nWidth = 0.35 * innerCircleDiameter
		def nBottomY = innerCircleY + innerCircleDiameter/2 + nHeight/2
		def nTopY = innerCircleY + innerCircleDiameter/2 - nHeight/2
		def nRightX = innerCircleX + innerCircleDiameter/2 + strokeWidth/2
		def nLeftX = nRightX - nWidth

		int[] nPointsX = [nRightX, nRightX, nRightX - strokeWidth, nLeftX + strokeWidth, nLeftX + strokeWidth, nLeftX, nLeftX, nLeftX + strokeWidth, nRightX - strokeWidth, nRightX - strokeWidth]
		int[] nPointsY = [buffer + rectangleHeight, nBottomY, nBottomY, nTopY + 2 * strokeWidth, nBottomY, nBottomY, nTopY, nTopY, nBottomY - 2 * strokeWidth, buffer + rectangleHeight]
		graph.fillPolygon(nPointsX, nPointsY, 10)

		graph.dispose()

		return image
	}
}
