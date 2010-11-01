package org.ossim.omar

import grails.converters.JSON
import org.ossim.postgis.Geometry
import joms.oms.ossimGpt
import joms.oms.ossimDpt
import org.joda.time.DateTime
import org.joda.time.DateTimeZone

class WmsLogService {
  static transactional = true
  def fixLogParamsForRouting(def params)
  {
    def paramsSave = new HashMap(params)
    def startDate  = new DateTime(paramsSave.startDate.time).toDateTime(DateTimeZone.UTC)
    def endDate    = new DateTime(paramsSave.endDate.time).toDateTime(DateTimeZone.UTC)
    paramsSave.startDate = startDate.toString()
    paramsSave.endDate   = endDate.toString()

    def bboxSplit = paramsSave.bbox?.split(',')
    if(bboxSplit?.size() == 4)
    {
      double minX = bboxSplit[0] as double;
      double minY = bboxSplit[1] as double;
      double maxX = bboxSplit[2] as double;
      double maxY = bboxSplit[3] as double;

      paramsSave.geometry = Geometry.createPolygon(minX, minY, maxX, maxY)

       // we are assuming SRS 4326.
      // need to support others later
      try
      {
        def dpt = (new ossimGpt(0.0, 0.0)).metersPerDegree()
        def metersY = (maxY-minY)*dpt.y;
        def metersX = (maxX-minX)*dpt.x;
        if(paramsSave.width&&paramsSave.height)
        {
           paramsSave.meanGsd = ((metersX/(paramsSave.width as Double))+
                                 ((metersY/(paramsSave.height as Double))))*0.5
        }
      }
      catch(Exception e)
      {
        log.error(e)
      }
    }
 
    paramsSave.each{k,v->
      paramsSave."${k}" ='"' + v + '"'

    }
    paramsSave
   }
  def logParams(def params)
  {
    def paramsSave = new HashMap(params)

   log.info(fixLogParamsForRouting(paramsSave).toMapString())
  }
}
