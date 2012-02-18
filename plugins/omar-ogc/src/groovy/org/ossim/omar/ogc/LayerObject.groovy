package org.ossim.omar.ogc
/**
 * Created by IntelliJ IDEA.
 * User: sbortman
 * Date: Aug 11, 2008
 * Time: 8:51:51 PM
 * To change this template use File | Settings | File Templates.
 */
class LayerObject {
  def minX
  def minY
  def maxX
  def maxY
  def srs

  def name
  def title
  def description

  def filename
  def acquisition


  public String toString()
  {
    return [minX:minX, minY:minY, maxX:maxX, maxY:maxY]
  }


}