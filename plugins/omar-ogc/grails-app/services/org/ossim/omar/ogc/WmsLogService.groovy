package org.ossim.omar.ogc

import joms.oms.ossimGpt
import joms.oms.ossimDpt
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import geoscript.geom.Bounds
import org.apache.commons.lang.builder.ToStringBuilder

import org.codehaus.groovy.grails.plugins.DomainClassGrailsPlugin

class WmsLogService
{
  static transactional = true
  static metersPerDegree = null

  def wmsLoggingAppender

  def sessionFactory
  def propertyInstanceMap = DomainClassGrailsPlugin.PROPERTY_INSTANCE_MAP

  def index = 0

  def cleanUpGorm()
  {
    def session = sessionFactory.currentSession
    session.flush()
    session.clear()
    propertyInstanceMap.get().clear()
  }

  def fixLogParamsForRouting(def params)
  {
    def paramsSave = [:]

    params.each {k, v ->
      if ( params."${k}" != null )
      {
        paramsSave."${k}" = v
      }
    }

    //def startDate = new DateTime(paramsSave.startDate?.time).toDateTime(DateTimeZone.UTC).toDate()
    //def endDate = new DateTime(paramsSave.endDate?.time).toDateTime(DateTimeZone.UTC).toDate()

    paramsSave["startDate"] = params.startDate //.toString()
    paramsSave["endDate"] = params.endDate //.toString()

    def bboxSplit = params.bbox?.split(',')
    if ( bboxSplit?.size() == 4 )
    {
      double minX = bboxSplit[0] as double;
      double minY = bboxSplit[1] as double;
      double maxX = bboxSplit[2] as double;
      double maxY = bboxSplit[3] as double;

      paramsSave["geometry"] = new Bounds(minX, minY, maxX, maxY).polygon.g

      // we are assuming SRS 4326.
      // need to support others later
      try
      {
        if ( !metersPerDegree )
        {
          metersPerDegree = (new ossimGpt(0.0, 0.0)).metersPerDegree().y
        }
        def metersY = (maxY - minY) * metersPerDegree;
        def metersX = (maxX - minX) * metersPerDegree;
        if ( paramsSave.width && paramsSave.height )
        {
          paramsSave."meanGsd" = ((metersX / (paramsSave.width as Double)) +
                  ((metersY / (paramsSave.height as Double)))) * 0.5
        }
      }
      catch (Exception e)
      {
        log.error(e)
      }
    }

    paramsSave
  }

  def logParams(def params)
  {
    def paramsSave = fixLogParamsForRouting(params)
    def wmsLogRecord = new WmsLog()

      wmsLogRecord.properties = paramsSave

    //println ToStringBuilder.reflectionToString(foo)

    if ( wmsLogRecord.hasErrors() || !wmsLogRecord.save() )
    {
        wmsLogRecord.errors.allErrors.each { println it }
    }

    synchronized ( index )
    {
      if ( ++index % 100 == 0 )
      {
        cleanUpGorm()
      }
    }
  }
}
