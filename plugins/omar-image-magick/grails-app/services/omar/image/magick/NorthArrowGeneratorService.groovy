package omar.image.magick

import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.awt.RenderingHints
import javax.imageio.ImageIO
import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import org.ossim.omar.core.Utility

class NorthArrowGeneratorService
{
	LinkGenerator grailsLinkGenerator

	def grailsApplication

	def serviceMethod(def northAngle, def northArrowBackgroundColor, def northArrowColor, def northArrowSize)
	{
		def command
		def date = new Date().getTime()
		def tempFilesLocation = grailsApplication.config.export.workDir + "/"
		def tempFilesLocationAsFile = new File(tempFilesLocation)

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

		graph.setColor(new Color(0, 0, 0, 255))
		graph.drawOval(0, 0, size, size)
		graph.fillOval(0, 0, size, size)

		def outterCircleDiameter = (0.66 * size).toInteger()
		def outterCircleX = ((size - outterCircleDiameter) / 2).toInteger()
		def outterCircleY = size - outterCircleDiameter - buffer
		graph.setColor(new Color(255, 255, 255, 255))
		graph.fillOval(outterCircleX, outterCircleY, outterCircleDiameter, outterCircleDiameter)

		def innerCircleDiameter = outterCircleDiameter - 2 * strokeWidth
		def innerCircleX = outterCircleX + strokeWidth
		def innerCircleY = outterCircleY + strokeWidth
		graph.setColor(new Color(0, 0, 0, 255))
		graph.fillOval(innerCircleX, innerCircleY, innerCircleDiameter, innerCircleDiameter)

		def rectangleHeight = outterCircleY - 2 * buffer
		def rectangleWidth = rectangleHeight
		int[] triangleX = [size/2, size/2 + rectangleWidth/2, size/2 - rectangleWidth / 2]
		int[] triangleY = [buffer, buffer + rectangleHeight, buffer + rectangleHeight]
		graph.setColor(new Color(255, 255, 255, 255))
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

		def tempNorthArrowFile = File.createTempFile("northArrow", ".png", tempFilesLocationAsFile);
		ImageIO.write(image,  'png', tempNorthArrowFile.toString() as File)


		return tempNorthArrowFile.toString()
	}
}
