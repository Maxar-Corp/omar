package org.ossim.omar.raster

import java.awt.Color

/**
 * Created with IntelliJ IDEA.
 * User: sbortman
 * Date: 8/30/12
 * Time: 8:16 PM
 * To change this template use File | Settings | File Templates.
 *
 */

class PropertyNameStyle
{
  def propertyName
  def defaultOutlineColor = new Color( 255, 255, 255, 255 )
  def defaultFillColor = new Color( 0, 0, 0, 0 )

  def outlineLookupTable = [:]
  def fillLookupTable = [:]

  def getOutlineColor(def propertyValue)
  {
    outlineLookupTable[propertyValue] ?: defaultOutlineColor
  }

  def getFillColor(def propertyValue)
  {
    fillLookupTable[propertyValue] ?: defaultFillColor
  }
}
