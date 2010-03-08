package org.ossim.omar

import java.text.SimpleDateFormat

class ISO8601DateParser
{
  static def convertStringToSimpleDate(String dateString, String pattern)
  {
    def simpleDateFormat = new SimpleDateFormat(pattern)
    def result = (Date) null

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
      result = (Date) null
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
}