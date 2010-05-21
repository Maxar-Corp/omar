package org.ossim.omar

import java.math.MathContext
import java.text.SimpleDateFormat

/**
 * Created by IntelliJ IDEA.
 * User: sbortman
 * Date: Jun 16, 2008
 * Time: 1:58:06 PM
 * To change this template use File | Settings | File Templates.
 */

class DateUtil
{
  def rng = new Random()

  def createDateBetweenYears(def startYear, def endYear)
  {
    def year = startYear + rng.nextInt(endYear - startYear + 1)
    def month = rng.nextInt(12)

    def calendar = new GregorianCalendar(year, month, 1);
    def day = rng.nextInt(calendar.getActualMaximum(Calendar.DAY_OF_MONTH) + 1)

    def hour = rng.nextInt(24)
    def minute = rng.nextInt(60)
    def second = rng.nextInt(60)
    def millisecond = rng.nextInt(1000)

    def date = Date.parse("yyyy-MM-dd HH:mm:ss.SSS",
        "${year}-${month}-${day} ${hour}:${minute}:${second}.${millisecond}")

    return date
  }

  public static Date parseDate(String dateString)
  {
    TimeZone utc = null
    SimpleDateFormat sdf = null
    Date date = null
    switch ( dateString )
    {
      case ~/[0-9]{4}[0-1][0-9][0-3][0-9]/:
        sdf = new SimpleDateFormat("yyyyMMdd");
//        println "one: ${dateString}"
        break
      case ~/[0-9]{4}-[0-1][0-9]-[0-3][0-9]/:
        sdf = new SimpleDateFormat("yyyy-MM-dd");
        //println "one: ${dateString}"
        break
      case ~/[0-9]{4}-[0-1][0-9]-[0-3][0-9]T[0-2][0-9]:[0-5][0-9]:[0-5][0-9]Z/:
        utc = TimeZone.getTimeZone("UTC");
        sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        //println "two: ${dateString}"
        break
      case ~/[0-9]{4}-[0-1][0-9]-[0-3][0-9]T[0-2][0-9]:[0-5][0-9]:[0-5][0-9].[0-9]{3}Z/:
        utc = TimeZone.getTimeZone("UTC");
        sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        //println "three: ${dateString}"
        break
      case ~/[0-9]{4}-[0-1][0-9]-[0-3][0-9]T[0-2][0-9]:[0-5][0-9]:[0-5]?[0-9].[0-9]{1,}Z/:
        def x = dateString.split('T')
        def y = x[0].split('-')
        def z = (x[1] - 'Z').split(':')
        def r = new BigDecimal(z[2]).round(new MathContext(5)) as String

        if ( r.size() == 1 )
        r = "00.000"

        z[2] = r
        dateString = "${y[0]}-${y[1]}-${y[2]}T${z[0]}:${z[1]}:${z[2]}Z"
        utc = TimeZone.getTimeZone("UTC");
        sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        //println "four: ${dateString}"
        break
    }

    if ( sdf )
    {
      if ( utc )
      {
        sdf.setTimeZone(utc);
      }

      date = sdf.parse(dateString)
    }

    return date
  }

  static parseDateGivenFormats(String dateString, def dateFormats)
  {

    if ( !dateFormats || (dateFormats.size() < 1) )
    {
      dateFormats = [
          "MM'/'DD'/'yyyy HH:mm:ss",
          "yyyyMMdd'T'HH:mm:ss",
          "yyyyMMdd'T'HH:mm:ss.ssss",

      ]
    }
    if ( !dateString )
    {
      return null
    }
    def format = new SimpleDateFormat()

    dateFormats.each {dateFormat ->
      try
      {
        format.applyPattern(dateFromat)
        def date = fromat.parse(dateString)
        if ( date )
        {
          return date
        }
      }
      catch (Exception e)
      {
//        println "Cannot parse ${dateString}: using ${dateFormat}"
      }
    }
    return null
  }

  public static Date initializeDate(String dateField, Map params)
  {
    Date date = null

    if ( params[dateField] )
    {
      if ( params[dateField] == "struct" )
      {
        if ( params["${dateField}_year"] && params["${dateField}_month"] && params["${dateField}_day"] )
        {
          def year = (params["${dateField}_year"]?.isInteger()) ? params["${dateField}_year"]?.toInteger() : null
          def month = (params["${dateField}_month"]?.isInteger()) ? params["${dateField}_month"]?.toInteger() : null
          def day = (params["${dateField}_day"]?.isInteger()) ? params["${dateField}_day"]?.toInteger() : null

          if ( year != null && month != null && day != null )
          {
            def calendar = new GregorianCalendar()

            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month - 1)
            calendar.set(Calendar.DAY_OF_MONTH, day)

            if ( params["${dateField}_hour"] && params["${dateField}_minute"] )
            {
              def hour = (params["${dateField}_hour"]?.isInteger()) ? params["${dateField}_hour"]?.toInteger() : null
              def minute = (params["${dateField}_minute"]?.isInteger()) ? params["${dateField}_minute"]?.toInteger() : null

              if ( hour != null && minute != null )
              {
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                calendar.set(Calendar.SECOND, 0)
              }
            }
            else
            {
              calendar.set(Calendar.HOUR_OF_DAY, 0)
              calendar.set(Calendar.MINUTE, 0)
              calendar.set(Calendar.SECOND, 0)
            }

            if ( params["${dateField}_timezone"] )
            {
              def timezone = TimeZone.getTimeZone(params["${dateField}_timezone"])

              calendar.timeZone = timezone ?: TimeZone.getDefault()
            }

            date = calendar.time
          }
        }
      }
      else if(params[dateField] instanceof Date)
      {
        date = params[dateField]
      }
      else if(params[dateField] instanceof String)
      {
        date = parseDateGivenFormats(params[dateField], [])
      }
    }

    return date
  }

  public static Date rollToEndOfDay(Date inputDate)
  {
    Date outputDate = null

    // Change the time portion of the date
    // to be the end of the day. 23:59:59.999
    if ( inputDate )
    {
      def cal = Calendar.instance
      cal.time = inputDate
      cal.set(Calendar.HOUR, 23)
      cal.set(Calendar.MINUTE, 59)
      cal.set(Calendar.SECOND, 59)
      cal.set(Calendar.MILLISECOND, 999)
      outputDate = cal.time
    }

    return outputDate
  }



  static SimpleDateFormat findDateFormatter(String dateString)
  {
    def formatter = null
    def format = null
    def timeZone = null

    switch ( dateString )
    {
      case ~/[0-9]{4}[0-9]{2}[0-9]{2}/:
        format = "yyyyMMdd";
        break
      case ~/[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}[zZ]/:
        format = "yyyy-MM-dd'T'hh:mm:ss'Z'"
        timeZone = TimeZone.getTimeZone("UTC")
        break
      case ~/[0-9]{4}[0-9]{2}[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}[zZ]/:
        format = "yyyyMMdd'T'hh:mm:ss'Z'"
        timeZone = TimeZone.getTimeZone("UTC")
        break
      case ~/[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}/:
        format = "yyyy-MM-dd'T'hh:mm:ss"
        break
      case ~/[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}/:
        format = "yyyy-MM-dd hh:mm:ss"
        break
      case ~/[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}/:
        format = "yyyy-MM-dd'T'hh:mm"
        break
      case ~/[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}/:
        format = "yyyy-MM-dd hh:mm"
        break
      case ~/[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}/:
        format = "yyyy-MM-dd'T'hh"
        break
      case ~/[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}/:
        format = "yyyy-MM-dd hh"
        break
      case ~/[0-9]{4}-[0-9]{2}-[0-9]{2}/:
        format = "yyyy-MM-dd"
        break
      case ~/[0-9]{4}-[0-9]{2}/:
        format = "yyyy-MM"
        break
      case ~/[0-9]{4}/:
        format = "yyyy"
        break
    }

    if ( format )
    {
      formatter = new SimpleDateFormat(format)

      if ( timeZone )
      {
        formatter.timeZone = timeZone
      }
    }

    return formatter
  }
}
