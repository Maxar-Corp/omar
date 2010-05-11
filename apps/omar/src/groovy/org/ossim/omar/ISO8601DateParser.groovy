package org.ossim.omar

import java.text.SimpleDateFormat
import org.joda.time.DateTime
import org.joda.time.Period
import org.joda.time.format.ISODateTimeFormat
import org.joda.time.format.ISOPeriodFormat
import org.joda.time.format.PeriodFormatter
import org.joda.time.format.DateTimeFormatter

class ISO8601DateParser
{
  static Date convertStringToSimpleDate(String dateString, String pattern)
  {
    def simpleDateFormat = new SimpleDateFormat(pattern)
    Date result = null

    try
    {
      if ( dateString?.toLowerCase().endsWith('z') && pattern?.toLowerCase().endsWith('z') )
      {
        simpleDateFormat.timeZone = TimeZone.getTimeZone("UTC");
      }

      result = simpleDateFormat.parse(dateString)
    }
    catch (java.lang.Exception e)
    {
      result = null
    }

    return result
  }

  static def convertStringToDuration(String durationString)
  {
    def result = (OmarDuration) null;

    if ( durationString.size() < 1 )
    {
      return result
    }

    if ( durationString.charAt(0) == 'P' )
    {
      def currentValue = ""
      def testString = ""
      def timeFlag = false;
      result = new OmarDuration();
      (1..<durationString.size()).each {
        char c = durationString.charAt(it);
        switch ( c )
        {
          case 'Y':
            result.years = Integer.valueOf(testString)
            testString = ""
            break
          case 'M':
            if ( timeFlag )
            {
              result.minutes = Integer.valueOf(testString)
            }
            else
            {
              result.months = Integer.valueOf(testString)
            }
            testString = ""
            break
          case 'D':
            result.days = Integer.valueOf(testString)
            break
          case 'H':
            result.hours = Integer.valueOf(testString)
            break
          case 'S':
            result.seconds = Integer.valueOf(testString)
            break
          case 'T':
            timeFlag = true
            break
          default:
            testString += c
        }
      }
    }

    return result
  }

  static def convertStringToSimpleDate(String dateString)
  {
    def formatter = DateUtil.findDateFormatter(dateString)
    def date = null

    if ( formatter )
    {
      date = formatter.parse(dateString)
    }

    return date
  }


  static def getDateRange(String iso8601String)
  {
    def startEndSplit = iso8601String?.split('/')

    if ( !startEndSplit )
    {
      return false
    }

    Date startDate = null
    Date endDate = null

    OmarDuration startDuration = null
    OmarDuration endDuration = null

    if ( startEndSplit )
    {
      String start = startEndSplit[0].trim()

      startDuration = convertStringToDuration(start)

      if ( !startDuration )
      {
        startDate = convertStringToSimpleDate(start)
      }

      if ( startEndSplit.size() > 1 )
      {
        String end = startEndSplit[1].trim()

        endDuration = convertStringToDuration(end)

        if ( !endDuration )
        {
          endDate = convertStringToSimpleDate(end)
        }
      }
    }

    if ( startDate )
    {
      if ( endDuration )
      {
        def calendar = new GregorianCalendar()
        calendar.setTime(startDate);
        endDuration.addTo(calendar);
        endDate = calendar.getTime();
      }
    }
    else if ( endDate )
    {
      if ( startDuration )
      {
        def calendar = new GregorianCalendar()
        startDuration.durationSign = -1
        calendar.setTime(endDate);
        startDuration.addTo(calendar);
        startDate = calendar.getTime();
      }
    }

    return [startDate, endDate]
  }



   /**
    * This uses the joda time library to parse a period formatted string.
    * We will limit it to the standard ISO format - PyYmMwWdDThHmMsS
    *
    * @param iso8601Period
    * @return null if unable to produce a period and a valid period object otherwise
    *
    */
  static def parsePeriod(String iso8601Period)
  {
    org.joda.time.Period result = null;
    PeriodFormatter periodFormatter = ISOPeriodFormat.standard();
    // Try the stadnard period format of the form
    // The standard ISO format - PyYmMwWdDThHmMsS
    try
    {
       result = periodFormatter.parsePeriod(iso8601Period); 
    }
    catch(Exception e)
    {
      result = null;
    }

    return result;
  }
 
  /**
   * This uses the joda time library to parse a IOS8601 date time string.  We first look for the
   * hard coded forms such as:
   * yyyyMMdd'T'HHmmss.SSSZ
   * yyyyMMdd'T'HHmmssZ
   * yyyy-MM-dd'T'HH:mm:ssZZ
   * yyyy-MM-dd'T'HH:mm:ss.SSSZZ
   * yyyyMMdd
   *
   * If they fail we allocate a generalized joida time parser that looks
   * for the following described patterns:
   *
   * datetime          = time | date-opt-time
   * time              = 'T' time-element [offset]
   * date-opt-time     = date-element ['T' [time-element] [offset]]
   * date-element      = std-date-element | ord-date-element | week-date-element
   * std-date-element  = yyyy ['-' MM ['-' dd]]
   * ord-date-element  = yyyy ['-' DDD]
   * week-date-element = xxxx '-W' ww ['-' e]
   * time-element      = HH [minute-element] | [fraction]
   * minute-element    = ':' mm [second-element] | [fraction]
   * second-element    = ':' ss [fraction]
   * fraction          = ('.' | ',') digit+
   * offset            = 'Z' | (('+' | '-') HH [':' mm [':' ss [('.' | ',') SSS]]])
   *
   * @param iso8601DateTime
   * @return null if unable to produce a DateTime else a valid DateTime object
   *
   */
  static def parseDateTime(String iso8601DateTime)
  {
    org.joda.time.DateTime result = null;

    // Try the basic date time of the form
    // yyyyMMdd'T'HHmmss.SSSZ
    DateTimeFormatter formatter = org.joda.time.format.ISODateTimeFormat.basicDateTime();
    try
    {
       result = formatter.parseDateTime(iso8601DateTime);
    }
    catch (Exception e)
    {
       result = null;
    }

    if(!result)
    {
      // now lets try the form
      // yyyyMMdd'T'HHmmssZ
      formatter = org.joda.time.format.ISODateTimeFormat.basicDateTimeNoMillis();
      try
      {
         result = formatter.parseDateTime(iso8601DateTime);
      }
      catch (Exception e)
      {
         result = null;
      }
    }

    if(!result)
    {
      // now lets try the form
      // yyyy-MM-dd'T'HH:mm:ssZZ
      formatter = org.joda.time.format.ISODateTimeFormat.dateTimeNoMillis();
      try
      {
         result = formatter.parseDateTime(iso8601DateTime);
      }
      catch (Exception e)
      {
         result = null;
      }
    }
    if(!result)
    {
      // now lets try the form
      // yyyy-MM-dd'T'HH:mm:ss.SSSZZ
      formatter = org.joda.time.format.ISODateTimeFormat.dateTime();
      try
      {
         result = formatter.parseDateTime(iso8601DateTime);
      }
      catch (Exception e)
      {
         result = null;
      }
    }

    if(!result)
    {
      // Try to parse for format yyyyMMdd
      formatter = org.joda.time.format.ISODateTimeFormat.basicDate();
      try
      {
        result = formatter.parseDateTime(iso8601DateTime);
      }
      catch (Exception e)
      {
        result = null;
      }
    }

    // last resort we will let joda do a generic
    // date or time parsing and return the proper object
    //
    if(!result)
    {
      formatter = org.joda.time.format.ISODateTimeFormat.dateTimeParser();
      try
      {
        result = formatter.parseDateTime(iso8601DateTime);
      }
      catch (Exception e)
      {
        result = null;
      }
    }
    return result;
  }


   /**
    * Sets up a WMS interval parser and uses the parsePeriod and parseDateTime
    * parsers to constrcut the intervals.  We currently do not support periodicity
    * but we do support intervals and interval lists.  For example:
    * P1Y/1999,2000/P10Y
    *
    * would produce 2 intervals the first
    * would be from jan 1 1998 through jan 1 1999
    * and the second
    * would be from january 1 2000 -  january 1 2010
    *
    * @param wmsIntervals
    * @return a list of intervals.  If the intervals were invalid or none
    *         specified an empty list would be returned. 
    */
  static def parseWMSIntervals(String wmsIntervals)
  {
    def intervals = wmsIntervals?.split(',');
    def intervalResult = [];
    intervals?.each{intervalValue->
      def d1 = null;
      def d2 = null;
      def range =intervalValue.split("/");
      if(range)
      {
        String dateString = range[0].trim();
        d1 = parsePeriod(dateString);
        if(!d1)
        {
          d1 = parseDateTime(dateString);
        }
        if(range.size() > 1)
        {
          dateString =  range[1].trim();
          d2 = parsePeriod(dateString);
          if(!d2)
          {
            d2 = parseDateTime(dateString);
          }

        }
        else
        {
          d2 = d1;
        }

        if(d1 && d2)
        {
          try
          {
            def interval = new org.joda.time.Interval(d1, d2);
            intervalResult.add(interval);
          }
          catch(Exception e)
          {
          }
        }
        d1 = null;
        d2 = null;
      }
    }
    return intervalResult;
  }
}