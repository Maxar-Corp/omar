package org.ossim.omar.oms

import joms.oms.ossimSensorModel
import joms.oms.ossimDpt
import joms.oms.ossimGpt
import joms.oms.ossimString

class ProjectionService
{

  static transactional = false

  def imageSpaceToWorld(String filename, long line, long sample)
  {
    def sensorModel = ossimSensorModel.createSensorModel(new ossimString(filename))
    def imagePoint = new ossimDpt(line, sample)
    def groundPoint = new ossimGpt()

    sensorModel.lineSampleToWorld(imagePoint, groundPoint)

    return [lat: groundPoint.latd(), lon: groundPoint.lond(), height: groundPoint.height()]
  }
}
