/**
 * Created by IntelliJ IDEA.
 * User: sbortman
 * Date: Aug 12, 2008
 * Time: 12:42:19 PM
 * To change this template use File | Settings | File Templates.
 */
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
  String quicklook_flag
  String null_flip
  String exception

  public String toString()
  {
    return [bbox: bbox, width: width, height: height, format: format, layers: layers, srs: srs, service: service,
            version: version, request: request, transparent: transparent, bgcolor: bgcolor, styles: styles,
            stretch_mode: stretch_mode, stretch_mode_region: stretch_mode_region, sharpen_mode:sharpen_mode,
            sharpen_width: sharpen_width, sharpen_sigma: sharpen_sigma, null_flip:null_flip, exception: exception].sort { it.key } 
  }


}