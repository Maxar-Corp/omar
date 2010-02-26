import java.text.SimpleDateFormat
import javax.xml.datatype.Duration
class ISO8601DateParser
{
  static def convertStringToSimpleDate(String dateString, String pattern)
  {
    def simpleDateFormat = new SimpleDateFormat(pattern)
    def result = (Date)null

     try
     {
       result = simpleDateFormat.parse(dateString)
     }
     catch(java.lang.Exception e)
     {
       result = (Date)null
     }

    return result
  }
  static def convertStringToDuration(String durationString)
  {
    def result = (OmarDuration)null;

    if(durationString.size()<1)
    {
      return result
    }

    if(durationString.charAt(0) == 'P')
    {
      def currentValue = ""
      def testString = ""
      def timeFlag = false;
      result = new OmarDuration();
      (1..<durationString.size()).each{
        char c = durationString.charAt(it);
        switch(c)
        {
          case 'Y':
            result.years = Integer.valueOf(testString)
            testString = ""
            break
          case 'M':
            if(timeFlag)
            {
              result.minutes =  Integer.valueOf(testString)
            }
            else
            {
              result.months =  Integer.valueOf(testString)
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
    if(dateString.size() < 1)
    {
      return (Date)null
    }
    Date result = convertStringToSimpleDate(dateString, "yyyyMMdd")

    if(result)
    {
      return result;
    }
    result = convertStringToSimpleDate(dateString, "yyyy-MM-dd'T'hh:mm:ssZ")
    if(result)
    {
      return result;
    }
    result = convertStringToSimpleDate(dateString, "yyyy-MM-dd'T'hh:mm:ss")
    if(result)
    {
      return result;
    }
    result = convertStringToSimpleDate(dateString, "yyyy-MM-dd hh:mm:ss")
    if(result)
    {
      return result;
    }
    result = convertStringToSimpleDate(dateString, "yyyy-MM-dd'T'hh:mm")
    if(result)
    {
      return result;
    }
    result = convertStringToSimpleDate(dateString, "yyyy-MM-dd hh:mm")
    if(result)
    {
      return result;
    }
    result = convertStringToSimpleDate(dateString, "yyyy-MM-dd'T'hh")
    if(result)
    {
      return result;
    }
    result = convertStringToSimpleDate(dateString, "yyyy-MM-dd hh")
    if(result)
    {
      return result;
    }
    result = convertStringToSimpleDate(dateString, "yyyy-MM-dd")
    if(result)
    {
      return result;
    }
    result = convertStringToSimpleDate(dateString, "yyyy-mm")
    if(result)
    {
      return result;
    }
    result = convertStringToSimpleDate(dateString, "yyyy")
    
    return result;
  }
  

  static def getDateRange(String iso8601String)
  {
    String[] startEndSplit = iso8601String.split('/')

    if(startEndSplit.size() ==0)
    {
      return false
    }
    Date         startDate = (Date)null
    Date         endDate   = (Date)null
    OmarDuration startDuration = (OmarDuration)null
    OmarDuration endDuration   = (OmarDuration)null
    if(startEndSplit.size()>0)
    {
      String start =  startEndSplit[0].trim()
      startDuration = convertStringToDuration(start)
      if(!startDuration)
      {
        startDate   = convertStringToSimpleDate(start)
      }

      if(startEndSplit.size() > 1)
      {
        String end =  startEndSplit[1].trim()
        endDuration = convertStringToDuration(end)
        if(!endDuration)
        {
          endDate   = convertStringToSimpleDate(end)
        }
      }
    }
    if(startDate)
    {
      if(endDuration)
      {
        def calendar = new GregorianCalendar()
        calendar.setTime(startDate);
        endDuration.addTo(calendar);
        endDate = calendar.getTime();
      }
    }
    else if(endDate)
    {
      if(startDuration)
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