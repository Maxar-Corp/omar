/**
 * Created by IntelliJ IDEA.
 * User: sbortman
 * Date: Aug 12, 2008
 * Time: 12:42:19 PM
 * To change this template use File | Settings | File Templates.
 */
import java.text.SimpleDateFormat
import java.awt.Color
class WMSRequest
{
  String bbox
  String width
  String height
  String format
  String layers
  String srs
  String service
  String version
  String request
  String transparent
  String bgcolor
  String styles
  String stretch_mode
  String stretch_mode_region
  String sharpen_mode
  String sharpen_width
  String sharpen_sigma
  String quicklook
  String null_flip
  String exception
  String bands
  String time

  public String toString()
  {
    return [bbox: bbox, width: width, height: height, format: format, layers: layers, srs: srs, service: service,
            version: version, request: request, transparent: transparent, bgcolor: bgcolor, styles: styles,
            stretch_mode: stretch_mode, stretch_mode_region: stretch_mode_region, sharpen_mode:sharpen_mode,
            sharpen_width: sharpen_width, sharpen_sigma: sharpen_sigma, time:time, null_flip:null_flip, exception: exception].sort { it.key } 
  }
  String[] getDates()
  {
    if(time)
    {
      return time.split(",")
    }
    return new String[0]
  }
  def getDateRange = 
  {
    def result = []
    String[] dates = getDates();
    if(dates.size()<1) return []
    (0..<dates.size()).each{
      def range = ISO8601DateParser.getDateRange(dates[it])
      if(range.size()>0)
      {
        result.add(range[0])
       if(range.size() > 1)
       {
         result.add(range[1])
       }
      }
    }
    return result
  }
  def getBackgroundColor =
  {
    def result = new Color(0, 0, 0)
    if (bgcolor)
    {
      if (bgcolor.size() == 8)
      {
        // skip 0x
        result = new Color(Integer.decode("0x"+bgcolor[2]+bgcolor[3]),
                           Integer.decode("0x"+bgcolor[4]+bgcolor[5]),
                           Integer.decode("0x"+bgcolor[6]+bgcolor[7]))
      }
    }

    return result
  }
  def getTransparentFlag =
  {
    def result = false;
    if(transparent)
    {
       result = Boolean.toBoolean(transparent)
    }
    return result
  }
}