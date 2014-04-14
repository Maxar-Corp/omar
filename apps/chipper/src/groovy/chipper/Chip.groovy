package chipper

import geoscript.geom.Bounds
import org.apache.commons.lang.builder.ToStringBuilder

import java.awt.Dimension

/**
 * Created by sbortman on 4/10/14.
 */
class Chip
{
  List<GeospatialImage> layers = []
  Bounds bbox
  Dimension size
  String format
  Boolean transparent = false

  String bands

  @Override
  String toString()
  {
    return ToStringBuilder.reflectionToString( this )
  }

  Chip(def params)
  {
    params.layers?.split( ',' )?.each { String filename ->
      layers << GeospatialImage.findByFilename( filename )
    }

    if ( params.bbox )
    {
      def (minLon, minLat, maxLon, maxLat) = params.bbox.split( ',' ).collect { it as Double }

      bbox = new Bounds( minLon, minLat, maxLon, maxLat, params.srs )
    }

    if ( params.width && params.height )
    {
      size = new Dimension( params.width as Integer, params.height as Integer )
    }

    format = params.format ?: 'jpg'
    transparent = params.transparent as Boolean ?: false

    bands = params?.bands
  }

  Map<String, String> createChipperOptions()
  {
    def chipperOptionsMap = [
        cut_min_lon   : bbox.minX as String,
        cut_min_lat   : bbox.minY as String,
        cut_max_lon   : bbox.maxX as String,
        cut_max_lat   : bbox.maxY as String,
        cut_height    : size?.height as String,
        cut_width     : size?.width as String,
        'hist-op'     : 'auto-minmax',
        operation     : 'ortho',
        scale_2_8_bit : 'true',
        srs           : bbox?.proj?.id,
        three_band_out: 'true'
    ]
//    }

    layers?.eachWithIndex { image, i ->
      chipperOptionsMap["image${i}.file"] = image.filename
    }


    if ( bands )
    {
      chipperOptionsMap['bands'] = bands
    }


    return chipperOptionsMap
  }

}
