package org.ossim.omar.ogc
/**
 * Created by IntelliJ IDEA.
 * User: sbortman
 * Date: Aug 11, 2008
 * Time: 8:51:41 PM
 * To change this template use File | Settings | File Templates.
 */
class MapObject {
  def keywords = []
  def layers = []

  def minX
  def minY
  def maxX
  def maxY
  def srs

  def name
  def title

  def getCapabilitiesURL
  def getMapURL
  
}