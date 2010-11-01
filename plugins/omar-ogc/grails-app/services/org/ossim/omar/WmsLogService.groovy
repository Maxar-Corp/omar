package org.ossim.omar

import grails.converters.JSON
import org.ossim.postgis.Geometry
import joms.oms.ossimGpt
import joms.oms.ossimDpt
class WmsLogService {
  static transactional = true
  def fixLogParamsForRouting(def params)
  {
    def paramsSave = new HashMap(params)
    def startDate  = new org.joda.time.DateTime(paramsSave.start_date.time).toDateTime(org.joda.time.DateTimeZone.UTC)
    def endDate    = new org.joda.time.DateTime(paramsSave.end_date.time).toDateTime(org.joda.time.DateTimeZone.UTC)
    paramsSave.start_date = startDate.toString()
    paramsSave.end_date   = endDate.toString()

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
           paramsSave.mean_gsd = ((metersX/(paramsSave.width as Double))+
                                 ((metersY/(paramsSave.height as Double))))*0.5
        }
      }
      catch(Exception e)
      {
        log.error(e)
      }
    }
    paramsSave.url    = paramsSave.url?.size()>2048?paramsSave?.url[0..2047]:paramsSave?.url
    paramsSave.layers = paramsSave.layers?.size()>2048?paramsSave?.layers[0..2047]:paramsSave?.layers
 
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
