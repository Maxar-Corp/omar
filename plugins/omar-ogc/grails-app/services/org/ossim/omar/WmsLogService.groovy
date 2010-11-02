package org.ossim.omar

import grails.converters.JSON
import org.ossim.postgis.Geometry
import joms.oms.ossimGpt
import joms.oms.ossimDpt
import org.joda.time.DateTime
import org.joda.time.DateTimeZone

class WmsLogService {
  static transactional = true
  static metersPerDegree = null
  def fixLogParamsForRouting(def params)
  {
    def paramsSave = [:]//new HashMap(params)

    params.each{k, v->
      if(params."${k}" != null)
      {
        paramsSave."${k}" = v
      }
    }

    def startDate  = new DateTime(paramsSave.startDate?.time).toDateTime(DateTimeZone.UTC)
    def endDate    = new DateTime(paramsSave.endDate?.time).toDateTime(DateTimeZone.UTC)
    paramsSave."startDate" = startDate.toString()
    paramsSave."endDate"   = endDate.toString()

    def bboxSplit = params.bbox?.split(',')
    if(bboxSplit?.size() == 4)
    {
      double minX = bboxSplit[0] as double;
      double minY = bboxSplit[1] as double;
      double maxX = bboxSplit[2] as double;
      double maxY = bboxSplit[3] as double;

      paramsSave."geometry" = Geometry.createPolygon(minX, minY, maxX, maxY)

       // we are assuming SRS 4326.
      // need to support others later
      try
      {
        if(!metersPerDegree)
        {
          metersPerDegree = (new ossimGpt(0.0, 0.0)).metersPerDegree().y
        }
        def metersY = (maxY-minY)*metersPerDegree;
        def metersX = (maxX-minX)*metersPerDegree;
        if(paramsSave.width&&paramsSave.height)
        {
           paramsSave."meanGsd" = ((metersX/(paramsSave.width as Double))+
                                  ((metersY/(paramsSave.height as Double))))*0.5
        }
      }
      catch(Exception e)
      {
        log.error(e)
      }
    }

    paramsSave
   }
  def logParams(def params)
  {
     def paramsSave = fixLogParamsForRouting(params)
     log.info(paramsSave as grails.converters.JSON)
  }
}
